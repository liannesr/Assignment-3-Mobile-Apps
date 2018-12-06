package com.techexchange.mobileapps.assignment3;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.GestureDetectorCompat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.net.Socket;


public class LaberinthView extends View  {
    private static final String TAG = "LaberinthView";

    float initialXTouch, initialYTouch;
    private final int DISTANCE_TO_RUN = 4;
    private int screenWidth;
    private int screenHeight;
    private GestureDetectorCompat mDetector;
    public int damageWallRow=-1;
    public int damageWallCol=-1;

    public Bitmap down,up,right,left;
    private final int WIDTH = 180;
    private final int HEIGHT = 176;

    private  Tank tankOne = new Tank(0,0,null,0,0);
    private  Tank tankTwo = new Tank(0,5,null, 1,1000);
    private ArrayList<Tank> tanks = new ArrayList<>();
    private GameLogicGrid game= new GameLogicGrid(9,6,tankOne,tankTwo);

    private SendThread send;
    private ReceiveThread receive;
    private Handler ht;

    private boolean isServer;
    Bitmap grayBitmap =  BitmapFactory.decodeResource(this.getResources(),R.drawable.graybrick);

   //ng this constructor is necessary.
    public LaberinthView(Context context, Socket socket, Boolean isServer) {
        super(context);
        setUpLaberinth();
        this.isServer = isServer;
        screenHeight = super.getHeight();
        screenWidth = super.getWidth();
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
        tanks.add(tankOne);
        tanks.add(tankTwo);
        send = new SendThread("SendThread", socket);
        send.start();
        send.prepareHandler();
        receive = new ReceiveThread("ReceiveThread", socket, game, this);
        receive.start();
        receive.prepareHandler();
        receive.postTask(receive.repCall(isServer));
        ht = new Handler(receive.getLooper());

    }

    public void setUpLaberinth(){
        Bitmap spritesheet = BitmapFactory.decodeResource(this.getResources(),R.drawable.multicolortanks);
        Bitmap bitmap =  BitmapFactory.decodeResource(this.getResources(),R.drawable.bricksx64);
        bitmap = Bitmap.createScaledBitmap(bitmap, 180, 176, false);
        grayBitmap = Bitmap.createScaledBitmap(grayBitmap, 180, 176, false);
        Bitmap tankOne = Bitmap.createBitmap(spritesheet,0,0,80,80);
        tankOne = Bitmap.createScaledBitmap(tankOne, 180, 176, false);
        this.tankOne.setBitmap(tankOne);
        this.tankTwo.setBitmap(rotateBitmap(tankOne, 180));

        this.tankOne.setCurrXPos(0);
        this.tankOne.setCurrYPos(0);
        this.tankTwo.setCurrXPos(1080-WIDTH);
        this.tankTwo.setCurrYPos(0);
        initializeBitmaps();
        this.tankOne.setBitmap(down);
        this.tankTwo.setBitmap(down);
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
                }
            }
        }

        for(int i=0;i<tanks.size();i++){
            canvas.drawBitmap(tanks.get(i).getBitmap(),tanks.get(i).getCurrXPos(),tanks.get(i).getCurrYPos(),paint);

            if(tanks.get(i).getFireball().getActive()){
                canvas.drawOval(tanks.get(i).getFireball().getPositionX(), tanks.get(i).getFireball().getPositionY(),
                        tanks.get(i).getFireball().getPositionX()+50, tanks.get(i).getFireball().getPositionY()+50, paint);
                updateFireball(tanks.get(i).getFireball());
            }

        }

        for(int i=0;i<tanks.size();i++){
            updateTank(tanks.get(i));

        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Log.e(TAG, "Sleep interrupted!", ex);
        }

        if(isServer){
            int activation = 0;
            if( tankOne.getFireball().getActive()){
                activation=1;
            }
            System.out.println("I MADE IT");
            send.postTask(makeStringBundle(tankOne.getCurrXPos(),tankOne.getCurrYPos(),tankOne.getRow(),tankOne.getColumn(),
                    damageWallRow,damageWallCol,activation, tankOne.getFireball().getPositionX(),tankOne.getFireball().getPositionY(),
                    tankOne.getFireball().getVelocityX(), tankOne.getFireball().getVelocityY(),checkOrientation(tankOne.getBitmap())));
            damageWallRow=-1;
            damageWallCol=-1;
        }
        else{
            int activation = 0;
            if( tankTwo.getFireball().getActive()){
                activation=1;
            }
            send.postTask(makeStringBundle(tankTwo.getCurrXPos(),tankTwo.getCurrYPos(),tankTwo.getRow(),tankTwo.getColumn(),
                    damageWallRow,damageWallCol,activation, tankTwo.getFireball().getPositionX(),tankTwo.getFireball().getPositionY(),
                    tankTwo.getFireball().getVelocityX(), tankTwo.getFireball().getVelocityY(), checkOrientation(tankTwo.getBitmap())));
            damageWallRow=-1;
            damageWallCol=-1;
        }
        invalidate(); // Force a redraw.


    }

    public String makeStringBundle(int xTank, int yTank, int column, int row, int damageRow, int damageWallCol, int fireballActive, int fireX,
                                   int fireY, int firexVelocity, int fireyVelocity, String orientation){
        String stringBundle = xTank + ","+yTank+","+column+","+row+","+damageRow+","+damageWallCol+","+fireballActive+","+
                fireX + ","+fireY+ ","+firexVelocity+","+fireyVelocity+ ","+orientation;
        System.out.println("stringBundle: "+ stringBundle);
        return stringBundle;
    }
    public String checkOrientation(Bitmap bitmap){
        if(bitmap==up){
            return "up";
        }
        else if(bitmap==down){
            return "down";

        }
        else if(bitmap==left){
            return "left";
        }
        else if(bitmap==right){
            return "right";
        }
        return null;
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
        else if(fireballCollision(fireball)){
            fireball.setActive(false);
        }
        else{
            fireball.setPositionX(fireball.getPositionX()+(15*fireball.getVelocityX()));
            fireball.setPositionY(fireball.getPositionY()+(15*fireball.getVelocityY()));
        }

    }

    public void updateTank(Tank tank){
        GameElement gameElement = game.getGameGrid()[tank.getRow()][tank.getColumn()];
        int currX = tank.getCurrXPos();
        int currY =tank.getCurrYPos();
        int goalPosX = gameElement.getPositionTopX();
        int goalPosY= gameElement.getPositionTopY();
        if(currX!=goalPosX){
            if(currX<goalPosX){
                currX+=DISTANCE_TO_RUN;

            }
            else{
                currX-=DISTANCE_TO_RUN;
            }
            tank.setCurrXPos(currX);
        }
        else if(currY!=goalPosY){
            if(currY<goalPosY){
                currY+=DISTANCE_TO_RUN;
            }
            else{
                currY-=DISTANCE_TO_RUN;
            }
            tank.setCurrYPos(currY);

        }
        else {
            tank.setMovement(false);
            tank.setRight(false);
            tank.setLeft(false);
            tank.setUp(false);
            tank.setDown(false);
        }
    }

    public void establishDirection(Tank tank){
        Bitmap tankBitmap = tank.getBitmap();
        Fireball fireball = tank.getFireball();
        fireball.setFiredColumn(tank.getColumn());
        fireball.setFiredRow(tank.getRow());
        fireball.setActive(true);
        if(tankBitmap==this.up){
            //column
            fireball.setPositionX((tank.getCurrXPos()+(WIDTH/2))-2-fireball.getRadius());
            fireball.setPositionY(tank.getCurrYPos()-fireball.getHeight());
            fireball.setVelocityX(0);
            fireball.setVelocityY(-1);
        }
        else if(tankBitmap==this.down){
            //column
            fireball.setPositionX((tank.getCurrXPos()+(WIDTH/2))-2-fireball.getRadius());
            fireball.setPositionY(tank.getCurrYPos()+HEIGHT);
            fireball.setVelocityX(0);
            fireball.setVelocityY(1);

        }
        else if(tankBitmap==this.left){
            //row
            fireball.setPositionX(tank.getCurrXPos()-fireball.getWidth());
            fireball.setPositionY(tank.getCurrYPos()+HEIGHT/2-fireball.getRadius());
            fireball.setVelocityX(-1);
            fireball.setVelocityY(0);
        }
        else if(tankBitmap==this.right){
            //row
            fireball.setPositionX(tank.getCurrXPos()+WIDTH);
            fireball.setPositionY(tank.getCurrYPos()+HEIGHT/2-fireball.getRadius());
            fireball.setVelocityX(1);
            fireball.setVelocityY(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            if(isServer){
                if(!tankOne.getFireball().getActive()){
                    establishDirection(tankOne);
                }
            }
            else{
                if(!tankTwo.getFireball().getActive()){
                    establishDirection(tankTwo);
                }
            }
            return true;
        }else{
            Tank tank;
            if(isServer){
                tank = tankOne;
            }
            else{
                tank = tankTwo;
            }
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
                            if(isLongSwipe(differenceX,differenceY)){
                                //tankOne.setRight(true);
                                tank.setRight(true);
                                game.changeTankPosition(0,1, right, tank);
                                //tankOne.setMovement(true);
                                tank.setMovement(true);
                            }
                            else if(!tank.getMovement()){ //tankOne.getMovement()
                                tank.setBitmap(right);
                                //tankOne.setBitmap(right);
                            }
                        }
                        else if(initialXTouch>finalXTouch && differenceX>differenceY){
                            if(isLongSwipe(differenceX,differenceY)){
                                //tankOne.setLeft(true);
                                tank.setLeft(true);
                                game.changeTankPosition(0,-1, left, tank);
                                tank.setMovement(true);
                               // tankOne.setMovement(true);
                            }
                            else if(!tank.getMovement()){//tankOne.getMovement()
                                //tankOne.setBitmap(left);
                                tank.setBitmap(left);
                            }
                        }
                        else if(initialYTouch>finalYTouch && differenceY>differenceX){
                            if(isLongSwipe(differenceX,differenceY)){
                                //tankOne.setUp(true);
                                tank.setUp(true);
                                game.changeTankPosition(-1,0, up, tank);
                               // tankOne.setMovement(true);
                                tank.setMovement(true);
                            }
                            else if(!tank.getMovement()){ //tankOne.getMovement()
                                //tankOne.setBitmap(up);
                                tank.setBitmap(up);
                            }
                        }
                        else if(initialYTouch<finalYTouch && differenceY>differenceX){
                            if(isLongSwipe(differenceX,differenceY)){
                                //tankOne.setDown(true);
                                tank.setDown(true);
                                game.changeTankPosition(1,0, down, tank);
                                tank.setMovement(true);
                                //tankOne.setMovement(true);
                            }
                            else if(!tank.getMovement()){ //ankOne.getMovement()
                                //tankOne.setBitmap(down);
                                tank.setBitmap(down);
                            }
                        }

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

    public boolean isLongSwipe(float differenceX, float differenceY){
        return (differenceX>WIDTH*2 || differenceY>WIDTH*2);
    }


    public boolean fireballCollision(Fireball fireball) {
        int column = fireball.getFiredColumn();
        int row = fireball.getFiredRow();
        int xVel = fireball.getVelocityX();
        int yVel = fireball.getVelocityY();

        if (xVel != 0) {
            //row is imp!!
            if (xVel < 0) {
                //left
                for (int i = 0; i < game.getRows(); i++) {
                    for (int j = 0; j < game.getColumns(); j++) {
                        if (i == row && j<=column && game.getGameGrid()[i][j].getBitmap() != null) {
                            if (fireball.getPositionX() < game.getGameGrid()[i][j].getPositionTopX() + WIDTH){
                                game.getGameGrid()[i][j].damageOcurred();
                                damageWallRow=i;
                                damageWallCol=j;
                                return true;
                            }
                        }


                    }
                }
                for(int k=0;k<tanks.size();k++){
                    if(fireball != tanks.get(k).getFireball()){
                        if(fireball.getPositionX() < tanks.get(k).getCurrXPos()+WIDTH
                                && (fireball.getPositionY()> tanks.get(k).getCurrYPos() &&
                                fireball.getPositionY()<tanks.get(k).getCurrYPos() + WIDTH)){
                            if(k==0) tanks.get(1).addPoint();
                            if(k==1) tanks.get(0).addPoint();
                            return true;
                        }
                    }
                }

            }
            else {
                //right
                for (int i = 0; i < game.getRows(); i++) {
                    for (int j = 0; j < game.getColumns(); j++) {
                        if (i == row && j>=column && game.getGameGrid()[i][j].getBitmap() != null) {
                            if (fireball.getPositionX() + fireball.getWidth()> game.getGameGrid()[i][j].getPositionTopX()){
                                game.getGameGrid()[i][j].damageOcurred();
                                damageWallRow=i;
                                damageWallCol=j;
                                return true;
                            }
                        }

                    }
                }
                for(int k=0;k<tanks.size();k++){
                    if(fireball != tanks.get(k).getFireball()){
                        if(fireball.getPositionX() + fireball.getWidth()> tanks.get(k).getCurrXPos()
                                && (fireball.getPositionY()> tanks.get(k).getCurrYPos() &&
                                fireball.getPositionY()<tanks.get(k).getCurrYPos() + WIDTH)){
                            if(k==0) tanks.get(1).addPoint();
                            if(k==1) tanks.get(0).addPoint();
                            return true;
                        }
                    }
                }
            }
        }

        else if(yVel!=0){
            //column is imp!!
            if(yVel<0){
                //up correcto
                for (int j = 0; j < game.getColumns(); j++) {
                    for (int i = game.getRows(); i >= 0; i--) {
                        if (j == column && i<= row && game.getGameGrid()[i][j].getBitmap() != null) {
                            if (fireball.getPositionY() < game.getGameGrid()[i][j].getPositionTopY()+HEIGHT) {
                                game.getGameGrid()[i][j].damageOcurred();
                                damageWallRow=i;
                                damageWallCol=j;
                                return true;
                            }
                        }

                    }
                }
                for(int k=0;k<tanks.size();k++){
                    if(fireball != tanks.get(k).getFireball()){
                        if(fireball.getPositionY() < tanks.get(k).getCurrYPos()+HEIGHT
                                && (fireball.getPositionX()> tanks.get(k).getCurrXPos() &&
                                fireball.getPositionX()<tanks.get(k).getCurrXPos() + WIDTH) ){
                            if(k==0) tanks.get(1).addPoint();
                            if(k==1) tanks.get(0).addPoint();
                            return true;
                        }
                    }
                }

            }
            else{
                //down
                for (int j = 0; j < game.getColumns(); j++) {
                    for (int i = 0; i < game.getRows(); i++) {
                        if (j == column && i>=row && game.getGameGrid()[i][j].getBitmap() != null) {
                            if (fireball.getPositionY() + fireball.getHeight() > game.getGameGrid()[i][j].getPositionTopY()){
                                game.getGameGrid()[i][j].damageOcurred();
                                damageWallRow=i;
                                damageWallCol=j;
                                return true;
                            }
                        }

                    }
                }
                for(int k=0;k<tanks.size();k++){
                    if(fireball != tanks.get(k).getFireball()){
                        if(fireball.getPositionY() +fireball.getHeight() > tanks.get(k).getCurrYPos()
                                && (fireball.getPositionX()> tanks.get(k).getCurrXPos() &&
                                fireball.getPositionX()<tanks.get(k).getCurrXPos() + WIDTH)){
                            if(k==0) tanks.get(1).addPoint();
                            if(k==1) tanks.get(0).addPoint();
                            return true;
                        }
                    }
                }
            }
        }

        return false;
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
        game.up = this.up;
        this.down = rotateBitmap(tankOne.getBitmap(), 90);
        game.down = this.down;
        this.right = rotateBitmap(tankOne.getBitmap(), 0);
        game.right = this.right;
        this.left = rotateBitmap(tankOne.getBitmap(), 180);
        game.left = this.left;
    }


}
