import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';

class LayoutRanking extends StatelessWidget {
  const LayoutRanking({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text("Leaderboard"),
        leading: CupertinoButton(
          child: const Icon(
            CupertinoIcons.back,
            color: CupertinoColors.activeBlue,
            size: 24.0,
          ),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      child: Center(
        child: SizedBox(
          width: MediaQuery.of(context).size.width * 0.8,
          child:  ListView.builder(
            itemCount: appData.winnersList.length,
            itemBuilder: (BuildContext context, int index) {
              var entry = appData.winnersList[index];
              return Container(
                padding: EdgeInsets.symmetric(vertical: 8.0),
                child: CupertinoListTile(
                  title: Center( child: Text(
                    "Winner: ${entry['winner']} | Opponent: ${entry['opponent']} | Points: ${entry['points']}",
                    style: TextStyle(
                      fontSize: 16.0, // Set your desired font size
                    ),
                  )),
                ),
              );
            },
          ),
        ),
      ),
    );
  }
}
