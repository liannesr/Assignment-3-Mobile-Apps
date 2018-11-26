package com.techexchange.mobileapps.assignment3;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;

public class GameLogicGrid {

    private GameElement[][] gameGrid;
    private ArrayList<Tank> tanks= new ArrayList<>();
    private int columns;
    private int rows;
    private static final String TAG = "LaberinthView";


    public GameLogicGrid(int rows, int columns, Tank tankOne, Tank tankTwo){
        this.gameGrid = new GameElement[rows][columns];
        this.columns = columns;
        this.rows = rows;
        tanks.add(tankOne);
    }

    public void setElementInGrid(GameElement gridElement, int row, int column){
        gameGrid[row][column] = gridElement;
    }

    public boolean changeTankPosition(int rowDif, int columnDif, Bitmap bitmap){
        int oldRow = tanks.get(0).getRow();
        int oldColumn = tanks.get(0).getColumn();
        int newRow = oldRow + rowDif;
        int newColumn = oldColumn + columnDif;

        if(newRow < 0 || newRow == rows || newColumn < 0 || newColumn == columns) return false; //out of bounds
        if(gameGrid[newRow][newColumn].getBitmap()!=null) return false;
        if(tanks.get(0).getHorizontal() && tanks.get(0).getVertical()) return false;

        Tank tank = tanks.get(0);
        tanks.get(0).setBitmap(bitmap);
        tanks.get(0).setRow(newRow);
        tanks.get(0).setColumn(newColumn);
        return true;
    }

    public boolean doesFireballCollide(Fireball fireball, GameElement gameElement){

        if(fireball.getPositionY()<gameElement.getPositionTopY()+gameElement.getHeight()){//upcollision
            Log.d(TAG, "up");
            fireball.setActive(false);
            return true;
        }
        else if(fireball.getPositionY()+fireball.getHeight()>gameElement.getPositionTopY()){ //down collision
            Log.d(TAG, "down");
            fireball.setActive(false);
            return true;
        }
        else if(fireball.getPositionX()<gameElement.getPositionTopX()+gameElement.getWidth()){ //left collision
            Log.d(TAG, "left");
            fireball.setActive(false);
            return true;
        }
        else if(fireball.getPositionX()+fireball.getWidth()>gameElement.getPositionTopX()){//right collision
            Log.d(TAG, "right");
            fireball.setActive(false);
            return true;
        }
        return false;
    }

    public int getColumns(){
        return this.columns;
    }
    public int getRows(){
        return this.rows;
    }
    public GameElement[][] getGameGrid() {
        return gameGrid;
    }

}
