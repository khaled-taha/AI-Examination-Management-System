package com.university.exam.exceptions;

import java.util.ArrayList;
import java.util.List;

public class PS {

    public static void main(String[] args) {
        int[][] matrix = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        int[][] directions = {
                {0, 1},
                {1, 0},
                {0, -1},
                {-1, 0}
        };

        int dirIndex = 0;
        int row = 0, column = 0;
        int total = matrix.length * matrix[0].length;

        while (total-- > 0) {
            System.out.print(matrix[row][column] + " ");
            visited[row][column] = true;

            int nextRow = row + directions[dirIndex][0];
            int nextColumn = column + directions[dirIndex][1];

            if (nextRow < 0 || nextRow >= matrix.length || nextColumn < 0 || nextColumn >= matrix[0].length || visited[nextRow][nextColumn]) {
                dirIndex = (dirIndex + 1) % matrix.length;
            }

            row += directions[dirIndex][0];
            column += directions[dirIndex][1];
        }
    }

    private static String parseTime(String time){
        if(time == null || time.length() != 6) return "Invalid Value!";

        StringBuilder hour = new StringBuilder();
        StringBuilder min = new StringBuilder();
        StringBuilder sec = new StringBuilder();
        String unit = " AM";

        for(int i = 0; i < time.length(); i++){
            if(i <= 1) hour.append(time.charAt(i));
            else if(i <= 3) min.append(time.charAt(i));
            else sec.append(time.charAt(i));
        }

        int parsedH = Integer.parseInt(hour.toString());

        if(parsedH > 12){
            if(parsedH - 12 <= 9) hour = new StringBuilder("0" + (parsedH - 12));
            else hour = new StringBuilder(String.valueOf(parsedH - 12));
            unit = " PM";
        }

        return hour + ":" + min + ":" + sec + unit;
    }
}
