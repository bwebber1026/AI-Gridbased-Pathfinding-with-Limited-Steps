package turnbasedgame;

import java.util.ArrayList;
import java.util.Random;

public class MoveAI {

    //60 x 3 because 60 total spaces to occupy, and then 1 row for x and 1 row for y
    //third row stores minimum number of steps to get to each tile
    //60 is assumed with 5 moves for enemy (four 15 space triangles)
    static int[][] coordinatesArray = new int[61][3];
    static int[][] finalPath = new int[findTurn.numEnemyMoves+1][3];
    static int finalPathNumSteps = 1;
    static int finalPathNumStepsTemp;
    static int tileCount = 1;
    static int moveCount = 1;
    
    public static int genRandMove() {
        Random rand = new Random();
        int randNum = rand.nextInt(4) + 1;
        return randNum;
    }

    public static int[][] genMove(int x, int y, int numMoves) {

        coordinatesArray = findPossibleCoordinates(x,y);
        int[] destination = pickDestination();
        int[][] path = findPath(destination);

        System.out.println("FINAL PATH:");
        for (int i=0; i<finalPathNumSteps; i++){
            System.out.println(finalPath[i][0] + ", " + finalPath[i][1]);
        }

        finalPathNumStepsTemp = finalPathNumSteps;
        finalPathNumSteps=1;
        return path;

    }

    public static int getFinalPathNumSteps(){
        return finalPathNumStepsTemp;
    }

    //x and y are REAL coodinates of enemy entity, not coordinates of shown map
    public static int[][] findPossibleCoordinates(int x, int y){

        coordinatesArray[0][0] = x;
        coordinatesArray[0][1] = y;
        coordinatesArray[0][2] = 0;

        if (!(TurnBasedGame.getLevel().getTile(x+1,y).isSolid())) {checkCoordinates(x+1,y);}
        moveCount = 1;
        if (!(TurnBasedGame.getLevel().getTile(x-1,y).isSolid())) {checkCoordinates(x-1,y);;}
        moveCount = 1;
        if (!(TurnBasedGame.getLevel().getTile(x,y+1).isSolid())) {checkCoordinates(x,y+1);}
        moveCount = 1;
        if (!(TurnBasedGame.getLevel().getTile(x,y-1).isSolid())) {checkCoordinates(x,y-1);}

        for (int i=0; i<tileCount; i++)
        {
            System.out.println(coordinatesArray[i][0] + ", " + coordinatesArray[i][1] + ", " + coordinatesArray[i][2]);
        }

        return coordinatesArray;
    }

    private static void checkCoordinates(int x, int y){
        boolean inArray = false;
        boolean higherMoveCount = false;

        if ( (!(TurnBasedGame.getLevel().getTile(x,y).isSolid())) & (moveCount < 6) & (tileCount < 61) )
        {
            for (int i=0; i<coordinatesArray.length; i++) {
                if ((coordinatesArray[i][0] == x) & (coordinatesArray[i][1] == y)) {
                    //then tile is already in array
                    inArray = true;
                    if (coordinatesArray[i][2] >= moveCount) {
                        //Tile has a higher moveCount than current path!
                        higherMoveCount = true;
                    }
                }
            }

            if ( (!inArray) | (higherMoveCount) ) {
                int index;
                int overwriteIndex;
                if (higherMoveCount) {
                    //the tile already exists in the array, so overwrite it's movecount to the lower one
                    overwriteIndex = findIndex(x,y);
                    coordinatesArray[overwriteIndex][0] = x;
                    coordinatesArray[overwriteIndex][1] = y;
                    coordinatesArray[overwriteIndex][2] = moveCount;
                }
                else {
                    //tile doesn't exist in the array, so create it and increment the tileCount
                    coordinatesArray[tileCount][0] = x;
                    coordinatesArray[tileCount][1] = y;
                    coordinatesArray[tileCount][2] = moveCount;
                    tileCount++;
                }

                moveCount++;

                //check tile to the right
                checkCoordinates(x + 1, y);

                index = findIndex(x,y);
                moveCount = coordinatesArray[index][2] + 1;
                //check tile to the left
                checkCoordinates(x - 1, y);

                index = findIndex(x, y);
                moveCount = coordinatesArray[index][2] + 1;
                //check tile below
                checkCoordinates(x, y + 1);

                index = findIndex(x,y);
                moveCount = coordinatesArray[index][2] + 1;
                //check tile above
                checkCoordinates(x, y - 1);

            }
        }
    }

    public static int findIndex(int TempX, int TempY) {
        int index = 0;
        for (int i=0; i<tileCount; i++) {
            if ((coordinatesArray[i][0] == TempX) & (coordinatesArray[i][1] == TempY)) {
                index = i;
            }
        }
        return index;
    }

    public static int[][] findPath(int[] dest) {

        if (!(dest[2] == 0)) {

            int[][] nextTiles = new int[4][3];
//        int[][] possibleNextTiles = new int[4][3]; //0 is right, 1 is left, 2 is down, 3 is up
            int possibleNextTileCount = 0;
            int[] chosenNextTile = new int[3];
            int[] right = new int[3];
            int[] left = new int[3];
            int[] down = new int[3];
            int[] up = new int[3];
            boolean rightInArray = false;
            boolean leftInArray = false;
            boolean downInArray = false;
            boolean upInArray = false;

            //set coordinates of surrounding tiles
//        possibleNextTiles[0][0] = dest[0] + 1;

            right[0] = dest[0] + 1;
            right[1] = dest[1];

            left[0] = dest[0] - 1;
            left[1] = dest[1];

            down[0] = dest[0];
            down[1] = dest[1] + 1;

            up[0] = dest[0];
            up[1] = dest[1] - 1;

            //get moveCounts of surrounding tiles
            for (int i = 0; i < tileCount; i++) {
                if ((coordinatesArray[i][0] == right[0]) & (coordinatesArray[i][1] == right[1])) {
                    right[2] = coordinatesArray[i][2];
                    rightInArray = true;
                }
                if ((coordinatesArray[i][0] == left[0]) & (coordinatesArray[i][1] == left[1])) {
                    left[2] = coordinatesArray[i][2];
                    leftInArray = true;
                }
                if ((coordinatesArray[i][0] == down[0]) & (coordinatesArray[i][1] == down[1])) {
                    down[2] = coordinatesArray[i][2];
                    downInArray = true;
                }
                if ((coordinatesArray[i][0] == up[0]) & (coordinatesArray[i][1] == up[1])) {
                    up[2] = coordinatesArray[i][2];
                    upInArray = true;
                }
            }

            if ( (right[2] == dest[2] - 1) & (rightInArray) ){
                nextTiles[possibleNextTileCount][0] = right[0];
                nextTiles[possibleNextTileCount][1] = right[1];
                nextTiles[possibleNextTileCount][2] = right[2];
                possibleNextTileCount++;
            }
            if ( (left[2] == dest[2] - 1) & (leftInArray) ){
                nextTiles[possibleNextTileCount][0] = left[0];
                nextTiles[possibleNextTileCount][1] = left[1];
                nextTiles[possibleNextTileCount][2] = left[2];
                possibleNextTileCount++;
            }
            if ( (down[2] == dest[2] - 1) & (downInArray) ){
                nextTiles[possibleNextTileCount][0] = down[0];
                nextTiles[possibleNextTileCount][1] = down[1];
                nextTiles[possibleNextTileCount][2] = down[2];
                possibleNextTileCount++;
            }
            if ( (up[2] == dest[2] - 1) & (upInArray) ){
                nextTiles[possibleNextTileCount][0] = up[0];
                nextTiles[possibleNextTileCount][1] = up[1];
                nextTiles[possibleNextTileCount][2] = up[2];
                possibleNextTileCount++;
            }

            System.out.println("possibleNextTileCount: " + possibleNextTileCount);
            for (int i = 0; i < possibleNextTileCount; i++) {
                System.out.println(nextTiles[i][0] + ", " + nextTiles[i][1] + ", " + nextTiles[i][2]);
            }

            chosenNextTile = pickNextTile(nextTiles, possibleNextTileCount);
            for (int i=0;i<3;i++) {
                finalPath[finalPathNumSteps][i] = chosenNextTile[i];
            }
            finalPathNumSteps++;
            findPath(chosenNextTile);

        }
        return finalPath;
    }

    public static int[] pickNextTile(int[][] possibleTiles, int numberOfChoices){

        Random rand = new Random();
        int randNum = rand.nextInt(numberOfChoices);

        int[] choice = new int[3];
        choice[0] = possibleTiles[randNum][0];
        choice[1] = possibleTiles[randNum][1];
        choice[2] = possibleTiles[randNum][2];

        return choice;

    }

    public static int[] pickDestination(){
        int[] coordinateChoice = new int[3];

        Random rand = new Random();
        int randNum = rand.nextInt(tileCount);

        //x coordinate
        coordinateChoice[0] = coordinatesArray[randNum][0];
        //y coordinate
        coordinateChoice[1] = coordinatesArray[randNum][1];
        //moveCount
        for (int i=0; i<tileCount; i++) {
            if ((coordinatesArray[i][0] == coordinateChoice[0]) & (coordinatesArray[i][1] == coordinateChoice[1])) {
                coordinateChoice[2] = coordinatesArray[i][2];
            }
        }

        for (int i=0;i<3;i++) {
            finalPath[0][i] = coordinateChoice[i];
        }

        System.out.println("Destination Choice: ");
        System.out.println(coordinateChoice[0] + ", " + coordinateChoice[1] + ", " + coordinateChoice[2]);
        return coordinateChoice;
    }

    public static int findPathX(int x,int y){
        return x;
    }

    public static int findPathY(int x,int y){
        return y;
    }

}
