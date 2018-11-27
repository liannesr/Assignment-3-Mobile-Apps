package com.techexchange.mobileapps.assignment3;

import android.graphics.Bitmap;

public class Fireball {

    private int positionX;
    private int positionY;
    private int height;
    private int width;
    private boolean isActive;
    private int radius;
    private int velocityX;
    private int velocityY;
    private int firedRow, firedColumn;

    public Fireball(int positionX, int positionY, int width, int height){
        this.positionX=positionX;
        this.positionY=positionY;
        this.width=width;
        this.height=height;
        this.isActive=false;
        radius = width/2;
        firedColumn=0;
        firedRow=0;
    }

    public void setFiredColumn(int firedColumn) {
        this.firedColumn = firedColumn;
    }

    public void setFiredRow(int firedRow) {
        this.firedRow = firedRow;
    }

    public int getFiredColumn() {
        return firedColumn;
    }

    public int getFiredRow() {
        return firedRow;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getRadius(){
        return this.radius;
    }

    public int getVelocityX(){
        return  this.velocityX;
    }
    public int getVelocityY(){
        return this.velocityY;
    }

    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setActive(boolean isActive){
        this.isActive=isActive;
    }
    public boolean getActive(){
        return this.isActive;
    }
}
