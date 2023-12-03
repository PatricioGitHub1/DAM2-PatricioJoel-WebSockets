package com.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

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

    // Funcion para crear tablero aleatorio por primera vez, las cartas tendras nombre tipo 1.jpg, 2.jpg...
    public  int[][] randomBoard() {
        int[][] matrix = new int[4][4];
        List<Integer> numbers = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            numbers.add(i);
            numbers.add(i);
        }

        Collections.shuffle(numbers, new Random());

        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = numbers.get(index++);
            }
        }

        return matrix;
    }

    public void sendToUsers(ArrayList<WebSocket> connections, JSONObject jsonMessage) {
        System.out.println(jsonMessage.toString());
        for (WebSocket conn : connections) {
            conn.send(jsonMessage.toString());
        }
    }

    public String[] findUsersArray(String id) {
        for (String[] idArray : playersId_matches) {
            for (String idInArray : idArray) {
                if (idInArray.equals(id)) {
                    return idArray;
                }
            }
        }

        return null;
    }

}
