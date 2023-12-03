import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';
import 'widget_selectable_list.dart';

class LayoutConnected extends StatefulWidget {
  const LayoutConnected({Key? key}) : super(key: key);

  @override
  State<LayoutConnected> createState() => _LayoutConnectedState();
}

class _LayoutConnectedState extends State<LayoutConnected> {
  final ScrollController _scrollController = ScrollController();
  final _messageController = TextEditingController();
  final FocusNode _messageFocusNode = FocusNode();

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    /*WidgetsBinding.instance.addPostFrameCallback((_) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    });*/

    return CupertinoPageScaffold(
        navigationBar: CupertinoNavigationBar(
          leading: CupertinoButton(
            child: const Icon(
              CupertinoIcons.clear,
                  color:  CupertinoColors.activeBlue,

                  size: 24.0,
                  semanticLabel: 'Text to announce in accessibility modes',
            ), 
            onPressed: () {
              appData.disconnectFromServer();
            }),

          middle: const Text("Memory Game"),

          trailing: CupertinoButton(
            child: const Icon(
              CupertinoIcons.star_fill,
                  color:  CupertinoColors.systemYellow,

                  size: 24.0,
                  semanticLabel: 'Text to announce in accessibility modes',
            ), 
            onPressed: () {
              // Ir al ranking
            }),

        ),
        child: Column(
          children: [
            const SizedBox(height: 52),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Expanded(
                  child: Align(
                    alignment: Alignment.center,
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text(
                          "Connected to ",
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.w200),
                        ),
                        Text(
                          "ws://${appData.ip}:${appData.port}",
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w400),
                        ),
                      ],
                    ),
                  ),
                ),
                Expanded(
                  child: Align(
                    alignment: Alignment.center,
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text(
                          ", with ID: ",
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.w200),
                        ),
                        Text(
                          "${appData.mySocketId}",
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w400),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
            
            Expanded(
              child: Center(
                child: GridView.builder(
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 4,
                  crossAxisSpacing: 8.0,
                  mainAxisSpacing: 8.0,
                ),
                itemCount: 16,
                itemBuilder: (BuildContext context, int index) {
                  int row = index ~/ 4;
                  int col = index % 4;
                  int imageNumber = appData.currentBoard[row][col];
                  //String imagePath = 'assets/images/image$imageNumber.png';
                  String imagePath = "/home/patricio/Documentos/ImagenesMemory/images/image$imageNumber.png";
                  return GestureDetector(
                    onTap: () {
                      print("Clicked on image: $imagePath");
                    },
                    child: Image.asset(
                      imagePath,
                      fit: BoxFit.cover,
                    ),
                  );
                },
              ),
              ),
            )
          
          ],
        ));
  }
}
