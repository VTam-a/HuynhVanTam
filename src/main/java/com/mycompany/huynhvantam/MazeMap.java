package com.mycompany.huynhvantam;

import java.util.*;

public class MazeMap {

    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int START = 2;
    public static final int END = 3;

    private static final int[][] LEVEL_SIZES = {
        {11, 11},
        {15, 15},
        {19, 19},
        {23, 23},
        {27, 27}
    };

    public static int[][] getMap(int level) {
        if (level < 1 || level > 5) {
            level = 1;
        }

        int[] size = LEVEL_SIZES[level - 1];
        int rows = size[0];
        int cols = size[1];

        return generateRandomMaze(rows, cols);
    }

    private static int[][] generateRandomMaze(int rows, int cols) {

        if (rows % 2 == 0) {
            rows++;
        }
        if (cols % 2 == 0) {
            cols++;
        }

        int[][] maze = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = WALL;
            }
        }

        Random random = new Random();
        carvePath(maze, 1, 1, random);

        maze[1][1] = START;
        maze[rows - 2][cols - 2] = END;

        ensurePathExists(maze, rows, cols);

        return maze;
    }

    private static void carvePath(int[][] maze, int row, int col, Random random) {
        maze[row][col] = PATH;

        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
        shuffleArray(directions, random);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValid(maze, newRow, newCol) && maze[newRow][newCol] == WALL) {
                maze[row + dir[0] / 2][col + dir[1] / 2] = PATH;
                carvePath(maze, newRow, newCol, random);
            }
        }
    }

    private static boolean isValid(int[][] maze, int row, int col) {
        return row > 0 && row < maze.length - 1
                && col > 0 && col < maze[0].length - 1;
    }

    private static void shuffleArray(int[][] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private static void ensurePathExists(int[][] maze, int rows, int cols) {
        if (!hasPath(maze, 1, 1, rows - 2, cols - 2)) {
            createStraightPath(maze, 1, 1, rows - 2, cols - 2);
        }
    }

    private static boolean hasPath(int[][] maze, int startRow, int startCol, int endRow, int endCol) {
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            if (row == endRow && col == endCol) {
                return true;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < maze.length
                        && newCol >= 0 && newCol < maze[0].length
                        && !visited[newRow][newCol] && maze[newRow][newCol] != WALL) {

                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol});
                }
            }
        }

        return false;
    }

    private static void createStraightPath(int[][] maze, int startRow, int startCol, int endRow, int endCol) {
        int currentRow = startRow;
        int currentCol = startCol;

        while (currentCol < endCol) {
            maze[currentRow][currentCol] = PATH;
            currentCol++;
        }

        while (currentRow < endRow) {
            maze[currentRow][currentCol] = PATH;
            currentRow++;
        }

        maze[endRow][endCol] = PATH;
    }
}
