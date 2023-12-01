package com.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppData {
    private static AppData instance;
    // Parejas jugadores
    ArrayList<String[]> playersId_matches = new ArrayList<>();
    String waitingPlayerId = "";
    Map<String, String> UserNameById = new HashMap<>();;

    private AppData() {

    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    // Crea el juego, si solo hay un jugador lo pone en espera, al siguiente los junta y devuelve el array con sus Id
    public String[] matchmaking(String newId) {
        if (waitingPlayerId.isEmpty()) {
            waitingPlayerId = newId;
            return null;
        } else {
            String[] NewGame = {waitingPlayerId, newId};
            playersId_matches.add(NewGame);
            System.out.println("New game can Begin");
            waitingPlayerId = "";

            return NewGame;
        }
    }
}
