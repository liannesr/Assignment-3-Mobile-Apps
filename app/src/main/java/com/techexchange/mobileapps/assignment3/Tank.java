package com.techexchange.mobileapps.assignment3;

import android.graphics.Bitmap;
public class Tank {

    private int row;
    private int column;
    private Bitmap bitmap;
    private int posX;
    private int posY;
    private int currXPos;
    private int currYPos;
    private boolean moving;
    private boolean up,down,left, right;
    private Fireball fireball;
    private int points;

    public Tank(int row, int column, Bitmap bitmap, int posX, int posY){
        this.row=row;
        this.column=column;
        this.bitmap = bitmap;
        this.posX = posX;
        this.posY = posY;
        moving = false;
        up = false;
        down = false;
        left = false;
        right = true;
        fireball = new Fireball(posX,posY,50,50);
        points=0;
    }

    public Fireball getFireball() {
        return fireball;
    }

    public void setFireball(Fireball fireball) {
        this.fireball = fireball;
    }

    public boolean getVertical(){
        if(up || down) return true;
        return false;
    }

    public boolean getHorizontal(){
        if(left || right) return true;
        return false;
    }
    public int getRow() {
        return row;
    }
    public void setUp(boolean up){
        this.up = up;
    }
    public void setDown(boolean down){
        this.down = down;
    }
    public void setLeft(boolean left){
        this.left = left;
    }

    public void setRight(boolean right){
        this.right = right;
    }

    public void setRow(int row) { this.row = row; }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Bitmap getBitmap(){ return this.bitmap;}

    public void setBitmap(Bitmap bitmap){this.bitmap=bitmap;}

    public boolean getMovement(){
        return this.moving;
    }
    public void setMovement(boolean moving){
        this.moving = moving;
    }

    public int getPosX() {
        return posX;
    }

    public void addPoint(){
        fireball.setActive(false);
        this.points++;
        System.out.println("POINTS: " +points);
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getCurrXPos() {
        return currXPos;
    }

    public void setCurrXPos(int currXPos) {
        this.currXPos = currXPos;
    }

    public int getCurrYPos() {
        return this.currYPos;
    }

    public void setCurrYPos(int currYPos) {
        this.currYPos = currYPos;
    }

}
