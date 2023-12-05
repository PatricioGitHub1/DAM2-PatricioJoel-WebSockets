package com.project;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.project.AppSocketsClient.OnCloseObject;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AppData {

    private static final AppData INSTANCE = new AppData();
    private AppSocketsClient socketClient;
    private String ip = "localhost";
    private String port = "8888";
    private String name = "";
    /*
    private String rival_name = "";
    private String rival_id = "";
    */
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    private String mySocketId;
    private List<String> clients = new ArrayList<>();
    private String selectedClient = "";
    private Integer selectedClientIndex;
    private StringBuilder messages = new StringBuilder();

    public enum ConnectionStatus {
        DISCONNECTED, DISCONNECTING, CONNECTING, CONNECTED
    }

    // Boolean per saber si és el meu torn o no
    boolean isMyTurn = false;
    boolean isWinner = false;

    // Enter per saber els meus punts i els del meu rival
    int myPoints = 0;
    int rivalPoints = 0;

    ArrayList<List<Integer>> currentBoard = new ArrayList<>();

    // Array para mostrar si las imagenes son visible o no
    ArrayList<Boolean> imagesVisibility = new ArrayList<>(Collections.nCopies(16, false));

    // Enter per saber quantes cartes has aixecat
    int flippedCards = 0;

    // Aquest ArrayList és l'índex en e GridLayout de les cartes que s'han mogut durant el torn
    ArrayList<Integer> indexFlippedCards = new ArrayList<>();

    // Enter per comparar si les 2 cartes són iguals
    int previousCardValue = 0;

    boolean file_saving = false;
    boolean file_loading = false;

    // IMPORTANT PER A QUE FUNCIONI DEGUT A UN ERROR AMB LES IMATGES
    Map<String, Object> imageMap = new HashMap<>();
    String jsonImagePath = "/Users/joelb/Documents/imagenesMemoryBase64.json";
    
    private AppData() {}

    public static AppData getInstance() {
        return INSTANCE;
    }
    
    public String getLocalIPAddress() throws SocketException, UnknownHostException {
        
        String localIp = "";
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress ia = inetAddresses.nextElement();
                if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia.isSiteLocalAddress()) {
                    System.out.println(ni.getDisplayName() + ": " + ia.getHostAddress());
                    localIp = ia.getHostAddress();
                    // Si hi ha múltiples direccions IP, es queda amb la última
                }
            }
        }

        // Si no troba cap direcció IP torna la loopback
        if (localIp.compareToIgnoreCase("") == 0) {
            localIp = InetAddress.getLocalHost().getHostAddress();
        }
        return localIp;
    }

    public void connectToServer() {
        try {
            URI location = new URI("ws://" + ip + ":" + port);
            socketClient = new AppSocketsClient(
                    location,
                    (ServerHandshake handshake) ->  { this.onOpen(handshake);},
                    (String message) ->             { this.onMessage(message); },
                    (OnCloseObject closeInfo) ->    { this.onClose(closeInfo); },
                    (Exception ex) ->               { this.onError(ex); }
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        connectionStatus = ConnectionStatus.CONNECTING;
        socketClient.connect();
        UtilsViews.setViewAnimating("Connecting");
        /*
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            if (connectionStatus == ConnectionStatus.CONNECTED) {
                CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
                // ctrlConnected.updateInfo();
                UtilsViews.setViewAnimating("Connected");
            } else {
                UtilsViews.setViewAnimating("Disconnected");
            }
        });
        pause.play();
        */
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            if (connectionStatus == ConnectionStatus.CONNECTED) {
                CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
                // ctrlConnected.updateInfo();
                UtilsViews.setViewAnimating("Connected");
            } else {
                UtilsViews.setViewAnimating("Disconnected");
            }
        });
        pause.play();
    }

    public void disconnectFromServer() {
        connectionStatus = ConnectionStatus.DISCONNECTING;
        UtilsViews.setViewAnimating("Disconnecting");
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            socketClient.close();
        });
        pause.play();
    }

    private void onOpen (ServerHandshake handshake) {
        System.out.println("Handshake: " + handshake.getHttpStatusMessage());
        connectionStatus = ConnectionStatus.CONNECTED; 
    }

    private void onMessage(String message) {
        JSONObject data = new JSONObject(message);

        if (connectionStatus != ConnectionStatus.CONNECTED) {
            connectionStatus = ConnectionStatus.CONNECTED;
        }

        String type = data.getString("type");
        switch (type) {
            case "start_game":
                System.out.println("Mi rival es " + data.getString("rival_name"));
                /*
                socketClient.connect();
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    UtilsViews.setViewAnimating("Connected");
                });
                pause.play();
                */
                break;
            case "list":
                clients.clear();
                data.getJSONArray("list").forEach(item -> clients.add(item.toString()));
                clients.remove(mySocketId);
                messages.append("List of clients: ").append(data.getJSONArray("list")).append("\n");
                updateClientList();
                break;
            case "id":
                JSONObject msg = new JSONObject();
                msg.put("type", "username");
                msg.put("id", data.getString("value"));
                msg.put("name", getName());
                socketClient.send(msg.toString());
                // messages.append("Id received: ").append(data.getString("value")).append("\n");
                break;
            case "connected":
                clients.add(data.getString("id"));
                clients.remove(mySocketId);
                messages.append("Connected client: ").append(data.getString("id")).append("\n");
                updateClientList();
                break;
            case "disconnected":
                String removeId = data.getString("id");
                if (selectedClient.equals(removeId)) {
                    selectedClient = "";
                }
                clients.remove(data.getString("id"));
                messages.append("Disconnected client: ").append(data.getString("id")).append("\n");
                updateClientList();
                break;
            case "private":
                messages.append("Private message from '")
                        .append(data.getString("from"))
                        .append("': ")
                        .append(data.getString("value"))
                        .append("\n");
                break;
            default:
                messages.append("Message from '")
                        .append(data.getString("from"))
                        .append("': ")
                        .append(data.getString("value"))
                        .append("\n");
                break;
        }
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
            // ctrlConnected.updateMessages(messages.toString());        
        }
    }

    public void onClose(OnCloseObject closeInfo) {
        connectionStatus = ConnectionStatus.DISCONNECTED;
        UtilsViews.setViewAnimating("Disconnected");
    }

    public void onError(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
    }

    public void refreshClientsList() {
        JSONObject message = new JSONObject();
        message.put("type", "list");
        socketClient.send(message.toString());
    }

    public void updateClientList() {
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            CtrlLayoutConnected ctrlConnected = (CtrlLayoutConnected) UtilsViews.getController("Connected");
            // ctrlConnected.updateClientList(clients);
        }
    }

    public void selectClient(int index) {
        if (selectedClientIndex == null || selectedClientIndex != index) {
            selectedClientIndex = index;
            selectedClient = clients.get(index);
        } else {
            selectedClientIndex = null;
            selectedClient = "";
        }
    }

    public Integer getSelectedClientIndex() {
        return selectedClientIndex;
    }

    public void send(String msg) {
        if (selectedClientIndex == null) {
            broadcastMessage(msg);
        } else {
            privateMessage(msg);
        }
    }

    public void broadcastMessage(String msg) {
        JSONObject message = new JSONObject();
        message.put("type", "broadcast");
        message.put("value", msg);
        socketClient.send(message.toString());
    }

    public void privateMessage(String msg) {
        if (selectedClient.isEmpty()) return;
        JSONObject message = new JSONObject();
        message.put("type", "private");
        message.put("value", msg);
        message.put("destination", selectedClient);
        socketClient.send(message.toString());
    }
    /*
    public JSONObject newBoard() {
        
        // Especifiquem les dimensions de la matriu
        int rows = 4;
        int cols = 4;

        // Creem un Array en 2D per representar la matriu
        int[][] matrix = new int[rows][cols];
        
        // Creem un ArrayList per mantenir constància sobre les ocurrències
        ArrayList<Integer> occurrences = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            occurrences.add(2);
        }

        // Inicialitzem la matriu amb números entre l'1 i el 8
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int number = generateUniqueNumber(occurrences);
                matrix[i][j] = number;
                occurrences.set(number - 1, occurrences.get(number - 1) - 1);
            }
        }

        // Creem una llista per a cadascuna de les files
        List<List<Integer>> rowsList = new ArrayList<>();

        // Populem la llista amb els números de cadascuna de les files
        for (int i = 0; i < rows; i++) {
            List<Integer> rowList = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                rowList.add(matrix[i][j]);
            }
            rowsList.add(rowList);
        }

        // Imprimim les llistes per cada fila per pantalla
        for (int i = 0; i < rows; i++) {
            System.out.println(rowsList.get(i));
        }
        
        // Creem un objecte JSON, el qual serà el taulell del joc
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "new_board");

        // Creem un JSONArray per al taulell del joc fent servir rowsList
        JSONArray boardArray = new JSONArray(rowsList);
        jsonObject.put("board", boardArray);

        // Imprimim el taulell del joc
        System.out.println("=================\n" + jsonObject);

        // Retornem el taulell del joc
        return jsonObject;
    }
    */
    public List<List<Integer>> newBoard() {
        
        // Especifiquem les dimensions de la matriu
        int rows = 4;
        int cols = 4;

        // Creem un Array en 2D per representar la matriu
        int[][] matrix = new int[rows][cols];
        
        // Creem un ArrayList per mantenir constància sobre les ocurrències
        ArrayList<Integer> occurrences = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            occurrences.add(2);
        }

        // Inicialitzem la matriu amb números entre l'1 i el 8
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int number = generateUniqueNumber(occurrences);
                matrix[i][j] = number;
                occurrences.set(number - 1, occurrences.get(number - 1) - 1);
            }
        }

        // Creem una llista per a cadascuna de les files
        List<List<Integer>> rowsList = new ArrayList<>();

        // Populem la llista amb els números de cadascuna de les files
        for (int i = 0; i < rows; i++) {
            List<Integer> rowList = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                rowList.add(matrix[i][j]);
            }
            rowsList.add(rowList);
        }

        // Imprimim les llistes per cada fila per pantalla
        for (int i = 0; i < rows; i++) {
            System.out.println(rowsList.get(i));
        }

        // Retornem el taulell del joc
        return rowsList;
    }
    
    private int generateUniqueNumber(List<Integer> occurrences) {
        int number;
        do {
            number = new Random().nextInt(8) + 1;
        } while (occurrences.get(number - 1) == 0);
        return number;
    }

    public String getIp() {
        return ip;
    }

    public String setIp (String ip) {
        return this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public String setPort (String port) {
        return this.port = port;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        return this.name = name;
    }

    public String getMySocketId () {
        return mySocketId;
    }
}
