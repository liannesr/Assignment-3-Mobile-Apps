package com.techexchange.mobileapps.assignment3;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.GestureDetectorCompat;

import java.util.ArrayList;

public class LaberinthView extends View  {
    private static final String TAG = "LaberinthView";

    float initialXTouch, initialYTouch;
    private final int DISTANCE_TO_RUN = 4;
    private int screenWidth;
    private int screenHeight;
    private GestureDetectorCompat mDetector;

    private Bitmap down,up,right,left;
    private final int WIDTH = 180;
    private final int HEIGHT = 176;

    private  Tank tankOne = new Tank(0,0,null,0,0);
    private ArrayList<Tank> tanks = new ArrayList<>();
    private GameLogicGrid game= new GameLogicGrid(9,6,tankOne,null);

    Bitmap grayBitmap =  BitmapFactory.decodeResource(this.getResources(),R.drawable.graybrick);
   //ng this constructor is necessary.
    public LaberinthView(Context context) {
        super(context);
        setUpLaberinth();
        screenHeight = super.getHeight();
        screenWidth = super.getWidth();
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
        tanks.add(tankOne);

    }

    public void setUpLaberinth(){
        Bitmap spritesheet = BitmapFactory.decodeResource(this.getResources(),R.drawable.multicolortanks);
        Bitmap bitmap =  BitmapFactory.decodeResource(this.getResources(),R.drawable.bricksx64);
        bitmap = Bitmap.createScaledBitmap(bitmap, 180, 176, false);

        Bitmap tankOne = Bitmap.createBitmap(spritesheet,0,0,80,80);
        tankOne = Bitmap.createScaledBitmap(tankOne, 180, 176, false);
        this.tankOne.setBitmap(tankOne);
        initializeBitmaps();
        int initialXPos=0; // increments of 180
        int initialYPos=0; // increments of 176

        for(int i=0;i<game.getRows();i++){
            for(int j=0;j<game.getColumns();j++){
                if(i<4 && j==1) game.setElementInGrid(new GameElement(initialXPos,initialYPos,initialXPos,initialYPos, 180,176, bitmap,2), i,j);
                else if(i==3 && j==2){
                    game.setElementInGrid(new GameElement(initialXPos,initialYPos, initialXPos,initialYPos, 180,176, bitmap,2), i,j);
                }
                else if(i==0 && (j==3 || j==4)){
                    game.setElementInGrid(new GameElement(initialXPos,initialYPos,initialXPos,initialYPos, 180,176, bitmap,2), i,j);
                }
                else if((i>5 && j==3) || (i==6 && (j==2 || j==4))){
                    game.setElementInGrid(new GameElement(initialXPos,initialYPos,initialXPos,initialYPos, 180,176, bitmap,2), i,j);
                }
                else{
                    game.setElementInGrid(new GameElement(initialXPos,initialYPos,initialXPos,initialYPos, 180,176, null,0), i,j);
                }
                initialXPos+=180;
            }
            initialYPos+=176;
            initialXPos=0;
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawColor(Color.BLACK);
        GameElement[][] gameArray = game.getGameGrid();
        for(int i=0;i<game.getRows();i++){
            for(int j=0;j<game.getColumns();j++){
                if(gameArray[i][j].getLifes() == 1){
                    gameArray[i][j].setBitmap(grayBitmap);
                }
                else if(gameArray[i][j].getLifes() == 0 ){
                    gameArray[i][j].setBitmap(null);
                }
                if(gameArray[i][j].getBitmap()!=null){
                    canvas.drawBitmap(gameArray[i][j].getBitmap(),gameArray[i][j].getPositionTopX(),gameArray[i][j].getPositionTopY(),paint);
                    for(int k=0;k<tanks.size();k++){
                        if(tanks.get(k).getFireball().getActive()) {
                           // game.doesFireballCollide(tanks.get(k).getFireball(), gameArray[i][j]);
                        }
                    }
                }
            }
        }
        canvas.drawBitmap(tankOne.getBitmap(),tankOne.getCurrXPos(),tankOne.getCurrYPos(),paint);
        for(int i=0;i<tanks.size();i++){
            if(tanks.get(i).getFireball().getActive()){
                canvas.drawOval(tanks.get(i).getFireball().getPositionX(), tanks.get(i).getFireball().getPositionY(),
                        tanks.get(i).getFireball().getPositionX()+50, tanks.get(i).getFireball().getPositionY()+50, paint);
                updateFireball(tanks.get(i).getFireball());
            }
        }
        updateTank();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Log.e(TAG, "Sleep interrupted!", ex);
        }

        invalidate(); // Force a redraw.
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenHeight = h;
        screenWidth = w;
    }
    public void updateFireball(Fireball fireball){
        if(fireball.getPositionX()+fireball.getWidth()>screenWidth){
            fireball.setActive(false);
        }
        else if(fireball.getPositionX()<0){
            fireball.setActive(false);
        }
        else if(fireball.getPositionY()+fireball.getHeight()>screenHeight){
            fireball.setActive(false);
        }
        else if(fireball.getPositionY()<0){
            fireball.setActive(false);
        }
        else{
            fireball.setPositionX(fireball.getPositionX()+4);
            fireball.setPositionY(fireball.getPositionY()+4);
        }

    }

    public void updateTank(){
        GameElement gameElement = game.getGameGrid()[tankOne.getRow()][tankOne.getColumn()];
        int currX = tankOne.getCurrXPos();
        int currY =tankOne.getCurrYPos();
        int goalPosX = gameElement.getPositionTopX();
        int goalPosY= gameElement.getPositionTopY();
        if(currX!=goalPosX){
            if(currX<goalPosX){
                currX+=DISTANCE_TO_RUN;

            }
            else{
                currX-=DISTANCE_TO_RUN;
            }
            tankOne.setCurrXPos(currX);
        }
        else if(currY!=goalPosY){
            if(currY<goalPosY){
                currY+=DISTANCE_TO_RUN;
            }
            else{
                currY-=DISTANCE_TO_RUN;
            }
            tankOne.setCurrYPos(currY);

        }
        else {
            tankOne.setMovement(false);
            tankOne.setRight(false);
            tankOne.setLeft(false);
            tankOne.setUp(false);
            tankOne.setDown(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            if(!tankOne.getFireball().getActive()){
                Log.d(TAG,"FIREBALLLLLL" + tankOne.getFireball().getActive());
                tankOne.getFireball().setActive(true);
                tankOne.getFireball().setPositionX(tankOne.getCurrXPos() + WIDTH/2);
                tankOne.getFireball().setPositionY(tankOne.getCurrYPos() + HEIGHT);
            }
            return true;
        }else{
            int action = MotionEventCompat.getActionMasked(event);
            switch(action) {
                case (MotionEvent.ACTION_DOWN) :
                    initialXTouch = event.getX();
                    initialYTouch = event.getY();
                    return true;
                case (MotionEvent.ACTION_MOVE) :
                    return true;
                case (MotionEvent.ACTION_UP) :
                    float finalXTouch = event.getX();
                    float finalYTouch = event.getY();
                    float differenceX = Math.abs(initialXTouch - finalXTouch);
                    float differenceY = Math.abs(initialYTouch - finalYTouch);
                    if(differenceX>=50 || differenceY>=50){
                        if(initialXTouch<finalXTouch && differenceX>differenceY){
                            tankOne.setRight(true);
                            game.changeTankPosition(0,1, right);
                        }
                        else if(initialXTouch>finalXTouch && differenceX>differenceY){
                            tankOne.setLeft(true);
                            game.changeTankPosition(0,-1, left);
                        }
                        else if(initialYTouch>finalYTouch && differenceY>differenceX){
                            tankOne.setUp(true);
                            game.changeTankPosition(-1,0, up);
                        }
                        else if(initialYTouch<finalYTouch && differenceY>differenceX){
                            tankOne.setDown(true);
                            game.changeTankPosition(1,0, down);
                        }
                        tankOne.setMovement(true);
                    }
                    return true;
                case (MotionEvent.ACTION_CANCEL) :
                    Log.d(TAG,"Action was CANCEL");
                    return true;
                case (MotionEvent.ACTION_OUTSIDE) :
                    Log.d(TAG,"Movement occurred outside bounds " +
                            "of current screen element");
                    return true;
                default :
                    return super.onTouchEvent(event);
            }
        }

    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }
    }

    public Bitmap rotateBitmap(Bitmap input, int angle){
        Bitmap output;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        output = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), matrix, true);
        return output;
    }

    public void initializeBitmaps(){
        this.up = rotateBitmap(tankOne.getBitmap(),-90);
        this.down = rotateBitmap(tankOne.getBitmap(), 90);
        this.right = rotateBitmap(tankOne.getBitmap(), 0);
        this.left = rotateBitmap(tankOne.getBitmap(), 180);
    }


}
