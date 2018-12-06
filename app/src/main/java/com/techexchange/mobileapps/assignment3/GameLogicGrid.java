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
    public Bitmap down,up,right,left;


    public GameLogicGrid(int rows, int columns, Tank tankOne, Tank tankTwo){
        this.gameGrid = new GameElement[rows][columns];
        this.columns = columns;
        this.rows = rows;
        tanks.add(tankOne);
        tanks.add(tankTwo);
    }

    public void setElementInGrid(GameElement gridElement, int row, int column){
        gameGrid[row][column] = gridElement;
    }
    public Tank getTankOne(){
        return tanks.get(0);
    }
    public Tank getTankTwo(){
        return tanks.get(1);
    }

    public boolean changeTankPosition(int rowDif, int columnDif, Bitmap bitmap, Tank tank){
        int oldRow = tank.getRow();
        int oldColumn = tank.getColumn();
//        int oldRow = tanks.get(0).getRow();
//        int oldColumn = tanks.get(0).getColumn();
        int newRow = oldRow + rowDif;
        int newColumn = oldColumn + columnDif;

        if(newRow < 0 || newRow == rows || newColumn < 0 || newColumn == columns) return false; //out of bounds
        if(gameGrid[newRow][newColumn].getBitmap()!=null) return false;
        if(tank.getHorizontal() && tank.getVertical()) return false;

        //Tank tank = tanks.get(0);
        tank.setBitmap(bitmap);
        tank.setRow(newRow);
        tank.setColumn(newColumn);
        return true;
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
