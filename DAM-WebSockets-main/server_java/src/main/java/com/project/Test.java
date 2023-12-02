package com.project;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) {
        int[][] matriz = randomBoard();
        
        JSONObject json = new JSONObject();
        json.put("type", "new_board");
        JSONArray json_board = new JSONArray();
        for (int[] row : matriz) {
            System.out.println(Arrays.toString(row));
            json_board.put(row);
        }
        json.put("board", json_board);
        System.out.println("=================");
        System.out.println(json.toString());
    }
    public static  int[][] randomBoard() {
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
}
