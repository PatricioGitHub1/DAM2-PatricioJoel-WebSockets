import 'dart:convert';
import 'dart:io';
import 'dart:math';
import 'package:flutter/cupertino.dart';
import 'package:path_provider/path_provider.dart';
import 'package:web_socket_channel/io.dart';

// Access appData globaly with:
// AppData appData = Provider.of<AppData>(context);
// AppData appData = Provider.of<AppData>(context, listen: false)

enum ConnectionStatus {
  disconnected,
  disconnecting,
  connecting,
  matchmaking,
  connected,
}

class AppData with ChangeNotifier {
  
  String ip = "localhost";
  String port = "8888";
  
  String username = "User_${Random().nextInt(100)}";
  String rival_name = "";
  String rival_id = "";

  List<dynamic> winnersList = [];

  IOWebSocketChannel? _socketClient;
  ConnectionStatus connectionStatus = ConnectionStatus.disconnected;

  String? mySocketId;
  List<String> clients = [];
  String selectedClient = "";
  int? selectedClientIndex;
  String messages = "";

  // Bool para saber si es mi turno o no
  bool isMyTurn = false;
  bool isWinner = false;

  int myPoints = 0;
  int rivalPoints = 0;

  List<List<int>> currentBoard = List.empty();

  // Array para mostrar si las imagenes son visible o no
  List<dynamic> imagesVisibility = List.generate(16, (index) => false);

  // int para saber cuantas cartas has levantado, la lista de  abajo es el index en el GridLayout de las cartas movidas durante el turno
  int flippedCards = 0;
  List<int> indexFlippedCards = [];
  int previousCardValue = 0; // int para comparar si las 2 cartas son iguales

  // Contexto de la pantalla Connected
  BuildContext ?connectedContext;

  bool file_saving = false;
  bool file_loading = false;

  // IMPORTANTE PARA QUE FUNCIONE DEBIDO A ERROR CON LAS IMAGENES
  /*Map<String, dynamic> imageMap = {};
  String jsonImagePath = "/home/patricio/Documentos/imagenesMemoryBase64.json";*/

  AppData() {
    _getLocalIpAddress();
  }

  Future<Map<String, dynamic>> loadJsonFromFile(String filePath) async {
    File file = File(filePath);
    String jsonString = await file.readAsString();
    return json.decode(jsonString);
  }

  void _getLocalIpAddress() async {
    try {
      final List<NetworkInterface> interfaces = await NetworkInterface.list(
          type: InternetAddressType.IPv4, includeLoopback: false);
      if (interfaces.isNotEmpty) {
        final NetworkInterface interface = interfaces.first;
        final InternetAddress address = interface.addresses.first;
        ip = address.address;
        notifyListeners();
      }
    } catch (e) {
      // ignore: avoid_print
      print("Can't get local IP address : $e");
    }
  }

  void connectToServer() async {
    connectionStatus = ConnectionStatus.connecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 2));

    // pantalla matchmaking
    connectionStatus = ConnectionStatus.matchmaking;

    _socketClient = IOWebSocketChannel.connect("ws://$ip:$port");
    _socketClient!.stream.listen(
      (message) async {
        final data = jsonDecode(message);
        
        print(message.toString());
        print("=================");

        switch (data['type']) {
          case 'start_game':
          // Esto quiere decir que encontraste rival y comienza la partida
            rival_name = data['rival_name'];
            rival_id = data['rival_id'];
            //connectionStatus = ConnectionStatus.connected;
            isMyTurn = !data['isRivalFirst'];
            // CARGAR IMAGENES A MAP
            //imageMap = await loadJsonFromFile(jsonImagePath);
            //print(imageMap.keys);

            break;

          case 'new_board':
            currentBoard = parseJsonMatrix(data['board']);
            print(currentBoard);
            connectionStatus = ConnectionStatus.connected;

          case 'cards_visibility':
            imagesVisibility = data['booleans'];
            break;

          case 'swap_turn':
            isMyTurn = !isMyTurn;
            rivalPoints = data['points'];

          case 'end_game':
            rivalPoints = data["sender_points"];
            myPoints = data['rival_points'];
            isMyTurn = false;
            if (data["sender_points"] > data["rival_points"]) {
              isWinner = false;
            } else {
              isWinner = true;
            }
            notifyListeners();
            showModal(connectedContext!);
            break;
          /*case 'list':
            clients = (data['list'] as List).map((e) => e.toString()).toList();
            clients.remove(mySocketId);
            messages += "List of clients: ${data['list']}\n";
            break;*/
          case 'id':
          // Esto lo recibes al entablar conexion con el server, te pasa tu ID
            mySocketId = data['value'];
            messages += "Id received: ${data['value']}\n";
            sendNameMessage(data['value'], username);
            break;
          case 'connected':
            clients.add(data['id']);
            clients.remove(mySocketId);
            messages += "Connected client: ${data['id']}\n";
            break;
          case 'disconnected':
            String removeId = data['id'];
            if (selectedClient == removeId) {
              selectedClient = "";
            }
            clients.remove(data['id']);
            messages += "Disconnected client: ${data['id']}\n";
            break;
          case 'private':
            messages +=
                "Private message from '${data['from']}': ${data['value']}\n";
            break;
          default:
            messages += "Message from '${data['from']}': ${data['value']}\n";
            break;
        }

        notifyListeners();
      },
      onError: (error) {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
      onDone: () {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
    );

  }

  sendNameMessage(String id, String username) {
    // Message on connection to send Username to server
    final usernameMessage = {
      'type': 'username',
      'id' : id,
      'name' : username
    };
    _socketClient!.sink.add(jsonEncode(usernameMessage));
  }

  disconnectFromServer() async {
    connectionStatus = ConnectionStatus.disconnecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient!.sink.close();
  }

  selectClient(int index) {
    if (selectedClientIndex != index) {
      selectedClientIndex = index;
      selectedClient = clients[index];
    } else {
      selectedClientIndex = null;
      selectedClient = "";
    }
    notifyListeners();
  }

  refreshClientsList() {
    final message = {
      'type': 'list',
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  send(String msg) {
    if (selectedClientIndex == null) {
      broadcastMessage(msg);
    } else {
      privateMessage(msg);
    }
  }

  broadcastMessage(String msg) {
    final message = {
      'type': 'broadcast',
      'value': msg,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  privateMessage(String msg) {
    if (selectedClient == "") return;
    final message = {
      'type': 'private',
      'value': msg,
      'destination': selectedClient,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  /*
  * Save file example:

    final myData = {
      'type': 'list',
      'clients': clients,
      'selectedClient': selectedClient,
      // i més camps que vulguis guardar
    };
    
    await saveFile('myData.json', myData);

  */

  Future<void> saveFile(String fileName, Map<String, dynamic> data) async {
    file_saving = true;
    notifyListeners();

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');
      final jsonData = jsonEncode(data);
      await file.writeAsString(jsonData);
    } catch (e) {
      // ignore: avoid_print
      print("Error saving file: $e");
    } finally {
      file_saving = false;
      notifyListeners();
    }
  }

  /*
  * Read file example:
  
    final data = await readFile('myData.json');

  */

  Future<Map<String, dynamic>?> readFile(String fileName) async {
    file_loading = true;
    notifyListeners();

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');
      if (await file.exists()) {
        final jsonData = await file.readAsString();
        final data = jsonDecode(jsonData) as Map<String, dynamic>;
        return data;
      } else {
        // ignore: avoid_print
        print("File does not exist!");
        return null;
      }
    } catch (e) {
      // ignore: avoid_print
      print("Error reading file: $e");
      return null;
    } finally {
      file_loading = false;
      notifyListeners();
    }
  }

  List<List<int>> parseJsonMatrix(List<dynamic> jsonList) {
    // Convert the List<dynamic> to List<List<int>>
    List<List<int>> matrix = List<List<int>>.from(
      jsonList.map((row) => List<int>.from(row)),
    );

    return matrix;
  }

  gameLogic(int cardValue, int cardIndex) async {
    flippedCards += 1;
    if (flippedCards == 1) {
      previousCardValue = cardValue;
      indexFlippedCards.add(cardIndex);
    }

    else if (flippedCards == 2) {
      indexFlippedCards.add(cardIndex);

      if (cardValue == previousCardValue) {

        myPoints += 1;
      } else {
        notifyListeners();

        await Future.delayed(const Duration(seconds: 2));
        for (var i in indexFlippedCards) {
          imagesVisibility[i] = false;
        }
        sendChangeTurn();
        notifyListeners();
      }

      // Reset de valores para siguiente ronda
      flippedCards = 0;
      previousCardValue = 0;
      indexFlippedCards.clear();
      sendCardsVisibility();
    }
    
    if (imagesVisibility.every((dynamic value) => value == true)) {
      print("Se acabo la partida");
      notifyListeners();
      sendEndGame();
      
      return;
    }

    notifyListeners();
  }

  sendCardsVisibility() {
    final message = {
      'type': 'cards_visibility',
      'booleans': imagesVisibility,
      'destination': rival_id
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  sendChangeTurn() {
    final message = {
      'type': 'swap_turn',
      'destination': rival_id,
      'points': myPoints
    };
    isMyTurn = !isMyTurn;
    _socketClient!.sink.add(jsonEncode(message));
  }



  sendEndGame() {
    isMyTurn = false;
    final message = {
      'type': 'end_game',
      'destination': rival_id,
      'sender_points': myPoints,
      'rival_points': rivalPoints
    };
    Map<String, dynamic> newEntry;
    if (myPoints > rivalPoints) {
      newEntry = {"winner":username,"opponent":rival_name, "points": myPoints};
      isWinner = true;
    } else {
      newEntry = {"winner":rival_name,"opponent":username, "points": rivalPoints};
      isWinner = false;
    }
    winnersList.add(newEntry);
    addElementToJson();

    isMyTurn = !isMyTurn;
    _socketClient!.sink.add(jsonEncode(message));
    showModal(connectedContext!);
  }

  Future<void> showModal(BuildContext context) async {
    await showCupertinoModalPopup(
      context: context,
      builder: (BuildContext context) => CupertinoActionSheet(
        title: Text(isWinner ? "You Won!" : "You Lost..."),
        message: const Text("Would you like to Play Again?"),
        actions: [
          CupertinoActionSheetAction(
            onPressed: () {
              resetStats(false);
              Navigator.pop(context);
            },
            child: const Text("Yes"),
          ),
          CupertinoActionSheetAction(
            onPressed: () {
              Navigator.pop(context);
              resetStats(true);
            },
            child: const Text("No"),
          ),
        ],
      ),
    );

  }

  // Reset de las stats para volver a jugar si le damos a volver a jugar al acabar la partida
  resetStats(bool isExiting) {
    rival_name = "";
    rival_id = "";
    isMyTurn = false;
    isWinner = false;
    myPoints = 0;
    rivalPoints = 0;
    currentBoard = List.empty();
    imagesVisibility = List.generate(16, (index) => false);
    flippedCards = 0;
    indexFlippedCards = [];
    previousCardValue = 0;
    
    if (isExiting == false) {
      // Mensaje al server para que juegue otra vez
      final message = {
        'type': 'play_again'
      };
      _socketClient!.sink.add(jsonEncode(message));
      print("Le diste que Si y deberias estar matchmaking");
      connectionStatus = ConnectionStatus.matchmaking;
      notifyListeners();
      
    } else {
      print("Le diste que No y te sales");
      disconnectFromServer();
    }
    
  }

  Future<String> get _localPath async {
    final directory = await getApplicationDocumentsDirectory();

    return directory.path;
  }

  Future<File> get _localFile async {
    final path = await _localPath;
    return File('$path/memoryWinners.json');
  }

  Future<void> createFileIfNotExists() async {
    final file = await _localFile;
    if (!await file.exists()) {
      await file.create();
      await file.writeAsString('[{"winner": "Bob", "opponent": "John", "points": 6}]');
    }
    
  }

  void readJson() async {
    File file = await _localFile;
    String jsonString = file.readAsStringSync();

    winnersList = jsonDecode(jsonString);

  }

  Future<void> addElementToJson() async {
    File file = await _localFile;

    // Convert messagesAsList to JSON string
    String jsonString = jsonEncode(winnersList);

    // Write the JSON string to the file
    await file.writeAsString(jsonString);
  }
}
