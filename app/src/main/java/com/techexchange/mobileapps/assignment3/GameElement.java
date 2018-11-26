package com.techexchange.mobileapps.assignment3;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameElement {

    private Bitmap bitmap;
    private int positionTopX;
    private int positionTopY;
    private int width;
    private int height;
    private int lifes;

    public GameElement(int positionTopX, int positionTopY, int currXPos, int currYPos, int width, int height, Bitmap bitmap, int life){
        this.positionTopX = positionTopX;
        this.positionTopY = positionTopY;
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
        this.lifes = life;

    }
    public int getLifes(){
        return this.lifes;
    }


    public void damageOcurred(){
        if(this.lifes>0)
            this.lifes = this.lifes-1;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getPositionTopX() {
        return positionTopX;
    }

    public int getPositionTopY() {
        return positionTopY;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
