package com.javarush.task.task35.task3513;

import java.util.*;
/*
 * Contains basic game logics. Creates 2D array of Tile class objects,
 * that contains previous, current and next states of all tiles and values.
 */
public class Model {
    private Tile[][] gameTiles;
    private static final int FIELD_WIDTH = 4;
    int maxTile = 0;
    int score = 0;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    //starts a new game
    void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    //adds a new tile with value of 2 or 4 to a playing area
    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            int index = (int) (Math.random() * emptyTiles.size());
            Tile emptyTile = emptyTiles.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    //returns all empty tiles out of current game state
    private List<Tile> getEmptyTiles() {
        final List<Tile> list = new ArrayList<>();
        for (Tile[] tileArray : gameTiles) {
            for (Tile t : tileArray)
                if (t.isEmpty()) {
                    list.add(t);
                }
        }
        return list;
    }

    //moves all non-empty non-null tiles to the left and sets them instead of null-tiles if possible
    //creates new empty tiles instead of first ones
    private boolean compressTiles(Tile[] tiles) {
        boolean isChanged = false;
        for (int i = FIELD_WIDTH-1; i > 0 ; i--) {
            for (int j = 0; j < i; j++) {
                if (tiles[j].value == 0 && tiles[j+1].value != 0) {
                    tiles[j] = tiles[j+1];
                    tiles[j+1] = new Tile();
                    isChanged = true;
                }
            }
        }
        return isChanged;
    }

    //sums values of 2 contiguous equivalent by value tiles located in the same row
    private boolean mergeTiles(Tile[] tiles) {
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH-1; i++) {
            if (tiles[i].value == tiles[i+1].value && tiles[i].value != 0) {
                tiles[i].value *= 2;
                if (tiles[i].value > maxTile) { maxTile = tiles[i].value;}
                score += tiles[i].value;
                tiles[i+1].value = 0;
                isChanged = true;
            }
        }
        compressTiles(tiles);
        return isChanged;
    }

    //rotates tiles arrangement clockwise 90 degrees
    private Tile[][] rotateMatrixBy90Clockwise(Tile[][] matrix) {
        Tile[][] rotatedMatrix = new Tile[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                rotatedMatrix[j][(matrix.length-1)-i] = matrix[i][j];
            }
        }
        return rotatedMatrix;
    }

    //moves, compresses and merges all tiles to the left
    public void left() {
        if (isSaveNeeded) { saveState(gameTiles);}
        for (Tile[] tiles: gameTiles) {
            if (compressTiles(tiles) | mergeTiles(tiles)) {
                addTile();
            }
        }
        isSaveNeeded = true;
    }

    //moves all tiles to the right (with merging) by rotating the arrangement twice before merging
    public void right() {
        if (isSaveNeeded) { saveState(gameTiles);}
        Tile[][] rotatedTiles = rotateMatrixBy90Clockwise(rotateMatrixBy90Clockwise(gameTiles));
        for (Tile[] tiles: rotatedTiles) {
            if (compressTiles(tiles) | mergeTiles(tiles)) {
                addTile();
            }
        }
        gameTiles = rotateMatrixBy90Clockwise(rotateMatrixBy90Clockwise(rotatedTiles));
        isSaveNeeded = true;
    }

    //moves all tiles down (with merging) by rotating the arrangement once before merging and thrice after that
    public void down() {
        if (isSaveNeeded) { saveState(gameTiles);}
        Tile[][] rotatedTiles = rotateMatrixBy90Clockwise(gameTiles);
        for (Tile[] tiles: rotatedTiles) {
            if (compressTiles(tiles) | mergeTiles(tiles)) {
                addTile();
            }
        }
        gameTiles = rotateMatrixBy90Clockwise(rotateMatrixBy90Clockwise(rotateMatrixBy90Clockwise(rotatedTiles)));
        isSaveNeeded = true;
    }

    //moves all tiles up (with merging) by rotating the arrangement thrice before merging
    public void up() {
        if (isSaveNeeded) { saveState(gameTiles);}
        Tile[][] rotatedTiles = rotateMatrixBy90Clockwise(rotateMatrixBy90Clockwise(rotateMatrixBy90Clockwise(gameTiles)));
        for (Tile[] tiles: rotatedTiles) {
            if (compressTiles(tiles) | mergeTiles(tiles)) {
                addTile();
            }
        }
        gameTiles = rotateMatrixBy90Clockwise(rotatedTiles);
        isSaveNeeded = true;
    }

    //checks if the playing field has contiguous equivalent by value tiles in rows in order to merge them
    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) { return true;}

        for (Tile[] gameTile : gameTiles) {
            for (int j = 0; j < gameTile.length - 1; j++) {
                if (gameTile[j].value == gameTile[j + 1].value) {
                    return true;
                }
            }
        }

        for (int i = 0; i < gameTiles.length - 1; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                if (gameTiles[i][j].value == gameTiles[i+1][j].value) {
                    return true;
                }
            }
        }

        return false;
    }

    //saves current game state including tiles location, values and total score
    private void saveState(Tile[][] gameTiles) {
        Tile[][] tiles = new Tile[gameTiles.length][gameTiles[0].length];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                tiles[i][j] = new Tile();
                tiles[i][j].value = gameTiles[i][j].value;
            }
        }
        previousStates.push(tiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    //rolls back current game state to previous state including tiles location, values and total score
    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    //checks if game state changed after last step
    private boolean hasBoardChanged() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != previousStates.peek()[i][j].value) {
                    return true;
                }
            }
        }
        return false;
    }

    //returns the strategy that calculates the best result for the next move
    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency = new MoveEfficiency(-1, 0, move);
        move.move();
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        rollback();
        return moveEfficiency;
    }

    //calculates the best result for the next move and implements it
    void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        priorityQueue.peek().getMove().move();
    }

}
