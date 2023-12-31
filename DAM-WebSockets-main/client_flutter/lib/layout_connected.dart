import 'package:client_flutter/layout_ranking.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';

class LayoutConnected extends StatefulWidget {
  const LayoutConnected({Key? key}) : super(key: key);

  @override
  State<LayoutConnected> createState() => _LayoutConnectedState();
}

class _LayoutConnectedState extends State<LayoutConnected> {
  
  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);
    appData.connectedContext = context;
    
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

          middle: Text("MemoryGame ${appData.username}"),

          trailing: CupertinoButton(
            child: const Icon(
              CupertinoIcons.star_fill,
                  color:  CupertinoColors.systemYellow,

                  size: 24.0,
                  semanticLabel: 'Text to announce in accessibility modes',
            ), 
            onPressed: () {
              // Ir al ranking
              appData.createFileIfNotExists();
              appData.readJson();
              Navigator.push(
                context,
                CupertinoPageRoute(
                  builder: (context) => const LayoutRanking(),
                ),
              );
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
                         Text(
                          appData.isMyTurn ? "Torn de " : "En espera ",
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w200),
                        ),
                        Text(
                          "${appData.username} | Points: ${appData.myPoints}",
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w400, color: CupertinoColors.activeGreen),
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
                         Text(
                          !appData.isMyTurn ? "Torn de " : "En espera ",
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w200),
                        ),
                        Text(
                          "${appData.rival_name} | Points: ${appData.rivalPoints}",
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w400, color: CupertinoColors.systemRed),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
            
            Expanded(
              child: Stack(
                children:[
                  Center(
                    child: Container(
                      height: MediaQuery.of(context).size.height * 0.7, 
                      width: MediaQuery.of(context).size.height * 0.7,

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

                          String imagePath = 'assets/images/image$imageNumber.png';
                          
                          return GestureDetector(
                            onTap: () {
                              if (appData.imagesVisibility[index] == false) {
                                  if (appData.isMyTurn && appData.flippedCards < 2) {
                                    appData.imagesVisibility[index] = !appData.imagesVisibility[index];
                                    appData.sendCardsVisibility();
                                    appData.gameLogic(imageNumber, index);
                                  }
                                  
                              }
                            },
                            child: appData.imagesVisibility[index]
                            ? Image.asset(
                              imagePath,
                              fit: BoxFit.cover,
                            )
                            : const ColoredBox(color: CupertinoColors.activeBlue),
                          );
                        },
                      ),
                    )
                
                  ),
                  if (appData.isMyTurn == false) Container(
                    color: Colors.black.withOpacity(0.5), // Adjust opacity as needed
                  ),
                ]
                
            ))
          
          ],
          
        ));
  }
}
