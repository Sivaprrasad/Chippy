package com.example.chippy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.graphics.BitmapFactory.*;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="CHIPPY-SPACESHIP";

    // screen size
    int screenHeight;
    int screenWidth;
    int min = 2;
    int max = 10;
    int score = 0;

    List<PowerUps> pUp = new ArrayList<PowerUps>();
    // ----------------------------
    // ## GAME STATS
    // ----------------------------

    int plives = 20;
    int elives = 10;
    int otherelives = 5;
    int BULLET_SPEED = 20;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;
    List<Enemy> enemyList = new ArrayList<Enemy>();

    Bitmap image;
    ArrayList<Rect> bullets = new ArrayList<Rect>();
    int random = new Random().nextInt((max - min) + 1) + min;
    Player player;
    Enemy enemy1;
    //Other Enemies
    Enemy enemy2;
    Enemy enemy3;
    Enemy enemy4;
    //obstacles
//    Obstacles o1;
//    Obstacles o2;


    Bitmap powpic;
    PowerUps powerUp;
    PowerUps addlives;

    //Enemy enemy2;


    Bitmap background;
    int bgXPosition = 0;
    int backgroundRightSide = 0;



    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        int SQUARE_WIDTH = 100;
        this.score = 0;


        this.printScreenInfo();

        // put the initial starting position of your player and enemies

        this.player = new Player(context, 100, 600, SQUARE_WIDTH, 2);
        this.image = decodeResource(context.getResources(), R.drawable.enemy);

        this.enemy1 = new Enemy(getContext(), 1350, 300, image);
        this.enemy1.updateHitbox();


        this.image = decodeResource(context.getResources(),R.drawable.part1);
        this.enemy2 = new Enemy(getContext(), 1200, 100, image);
        this.enemy2.updateHitbox();

        this.image = decodeResource(context.getResources(), R.drawable.part1);
        this.enemy3 = new Enemy(getContext(), 1000, 250,image);
        this.enemy3.updateHitbox();

        this.image = decodeResource(context.getResources(), R.drawable.part1 );
        this.enemy4 = new Enemy(getContext(), 1200, 500,image);
        this.enemy4.updateHitbox();

        enemyList.add(enemy1);
        enemyList.add(enemy2);
        enemyList.add(enemy3);
        enemyList.add(enemy4);

//        this.o1 = new Obstacles(getContext(), 500, 350);


        this.powpic = decodeResource(context.getResources(), R.drawable.powerup);
        this.powerUp = new PowerUps(getContext(),random*800, random*300,powpic);
        this.powerUp.updateHitbox();

        this.powpic = decodeResource(context.getResources(), R.drawable.extralife);
        this.addlives = new PowerUps(getContext(),random*800, random*400,powpic);
        this.addlives.updateHitbox();

        pUp.add(powerUp);
        pUp.add(addlives);

        // setup the backgroundrandom
        this.background = decodeResource(context.getResources(), R.drawable.backgroundrandom);

        // dynamically resize the backgroundrandom to fit the device
        this.background = Bitmap.createScaledBitmap(
                this.background,
                this.screenWidth,
                this.screenHeight,
                false
        );

        this.bgXPosition = 0;

    }


    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    // Stack overflow resource
    public int getrand(int min, int max){

        return (int)(Math.random() * max + min);

    }

    
    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }

    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void restartGame()
    {
        this.score = 0;
        gameIsRunning = true;
    }


    int numLoops = 0;


    public void updatePositions() {


        // UPDATE BACKGROUND POSITION
        // 1. Move the backgroundrandom
        this.bgXPosition = this.bgXPosition - 10;

        backgroundRightSide = this.bgXPosition + this.background.getWidth();
        // 2. Background collision detection
        if (backgroundRightSide < 0) {
            this.bgXPosition = 0;
        }
        numLoops = numLoops + 1;

        // @TODO: Update position of player based on mouse tap
        if (this.fingerAction == "mousedown") {
            player.setyPosition(player.getyPosition() - 100);
            player.updateHitbox();
        }
        else if (this.fingerAction == "mouseup") {
            player.setyPosition(player.getyPosition() + 10);
            player.updateHitbox();
        }
/*
        this.o1.setxPosition(this.o1.getxPosition()-25);

        // MOVE THE HITBOX (recalcluate the position of the hitbox)
        this.o1.updateHitbox();

        if (this.o1.getxPosition() <= 0) {
            // restart the enemy in the starting position
            this.o1.setxPosition(300);
            this.o1.setyPosition(120);
            this.o1.updateHitbox();
        }
*/

        // DEAL WITH BULLETS

        // Shoot a bullet every (5) iterations of the loop
        if (numLoops % 25  == 0) {
            this.enemy1.spawnBullet();
        }
/*
        if (numLoops % 50  == 0) {
            this.o1.spawnBullet();
        }

        for (int i = 0; i < this.o1.getBullets().size();i++) {
            Rect bullet = this.o1.getBullets().get(i);

            // For each bullet, check if teh bullet touched the wall
            if (bullet.right < 0) {
                this.o1.getBullets().remove(bullet);
            }

        }

        for (int i = 0; i < this.o1.getBullets().size();i++) {
            Rect bullet = this.o1.getBullets().get(i);

            if (this.player.getHitbox().intersect(bullet)) {
                this.player.setxPosition(300);
                this.player.setyPosition(120);
                this.player.updateHitbox();
            }

        }

        if (this.player.getHitbox().intersect(this.o1.getHitbox()) == true) {
            this.player.setxPosition(300);
            this.player.setyPosition(120);
            this.player.updateHitbox();

            plives = plives - 1;
        }
*/

//         Shoot a bullet every (5) iterations of the loop
//        if (numLoops % 5  == 0) {
//            this.player.spawnBullet();
//        }


        // MOVING THE BULLETS

        for (int i = 0; i < this.enemy1.getBullets().size();i++) {
            Rect bullet = this.enemy1.getBullets().get(i);
            bullet.left = bullet.left - BULLET_SPEED;
            bullet.right = bullet.right - BULLET_SPEED;
        }

        // MOVING THE BULLETS
        int pBULLET_SPEED = 50;
        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect pbullet = this.player.getBullets().get(i);
            pbullet.left = pbullet.left + pBULLET_SPEED;
            pbullet.right = pbullet.right + pBULLET_SPEED;
        }

        // COLLISION DETECTION ON THE Enemy BULLET AND WALL
        for (int i = 0; i < this.enemy1.getBullets().size();i++) {
            Rect bullet = this.enemy1.getBullets().get(i);

            // For each bullet, check if the bullet touched the wall
            if (bullet.right < 0) {
                this.enemy1.getBullets().remove(bullet);
            }

        }

        // COLLISION DETECTION ON THE Player BULLET AND WALL
        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect bullet = this.player.getBullets().get(i);

            // For each bullet, check if the bullet touched the wall
            if (bullet.left > this.screenWidth) {
                this.player.getBullets().remove(bullet);
            }

        }

        // COLLISION DETECTION BETWEEN ENEMY BULLET AND PLAYER
        for (int i = 0; i < this.enemy1.getBullets().size();i++) {
            Rect bullet = this.enemy1.getBullets().get(i);

            if (this.player.getHitbox().intersect(bullet)) {
                this.player.updateHitbox();
                plives = plives - 1;
            }
        }

        if (plives == 0) {
            getContext().startActivity(new Intent(getContext(),LooseScreenActivity.class));
        }

        // COLLISION DETECTION BETWEEN BULLET AND ENEMY
        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect bullet = this.player.getBullets().get(i);

            if (bullet.intersect(this.enemy1.getHitbox())) {
                this.enemy1.updateHitbox();
                this.player.getBullets().remove(bullet);
                elives = elives - 1;
                score = score + 2;
            }


            if (bullet.intersect(this.enemy2.getHitbox())) {
                this.enemy2.updateHitbox();
                this.player.getBullets().remove(bullet);
                otherelives = otherelives - 1;
                if (otherelives == 0) {
                    enemy2.setHitbox(new Rect(0, 0, 0, 0));
                    enemyList.remove(enemy2);
                    score = score + 2;
                }
            }

            if (bullet.intersect(this.enemy3.getHitbox())) {
                this.enemy3.updateHitbox();
                this.player.getBullets().remove(bullet);
                otherelives = otherelives - 1;
                if (otherelives == 0) {
                    enemy3.setHitbox(new Rect(0, 0, 0, 0));
                    enemyList.remove(enemy3);
                    score = score + 1;
                }
            }

            if (bullet.intersect(this.enemy4.getHitbox())) {
                this.enemy4.updateHitbox();
                this.player.getBullets().remove(bullet);
                otherelives = otherelives - 1;
                if (otherelives == 0) {
                    enemy4.setHitbox(new Rect(0, 0, 0, 0));
                    enemyList.remove(enemy4);
                    score = score + 2;
                }
            }
        }

        if (elives == 0) {
                enemyList.removeAll(enemyList);
                enemy1.setHitbox(new Rect(0, 0, 0, 0));
                getContext().startActivity(new Intent(getContext(), WinScreenActivity.class));
         }


        this.powerUp.setxPosition(this.powerUp.getxPosition()-15);
        this.powerUp.updateHitbox();
        this.addlives.setxPosition(this.addlives.getxPosition()-10);
        this.addlives.updateHitbox();

        int rand = new Random().nextInt((max - min) + 1) + min;

        if(this.powerUp.getxPosition()<0 ){
            this.powpic = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.powerup);
            this.powerUp = new PowerUps(getContext(),rand*1000, rand*200,powpic);
            pUp.add(powerUp);
        }

        if(this.addlives.getxPosition()<0 ){
            this.powpic = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.extralife);
            this.addlives = new PowerUps(getContext(),rand*1000, rand*550,powpic);
            pUp.add(addlives);
        }


        // @TODO:  Check collisions between enemy and player
        if (this.player.getHitbox().intersect(this.enemy1.getHitbox()) == true) {
            // decrease the lives
            plives = plives - 1;
        }

        if (this.player.getHitbox().intersect(this.enemy2.getHitbox()) == true) {
            // decrease the lives
            plives = plives - 1;
        }

        if (this.player.getHitbox().intersect(this.enemy3.getHitbox()) == true) {
            // decrease the lives
            plives = plives - 1;
        }

        if (this.player.getHitbox().intersect(this.enemy4.getHitbox()) == true) {
            // decrease the lives
            plives = plives - 1;
        }

        if (this.player.getHitbox().intersect(this.powerUp.getHitbox()) == true) {
            this.pUp.remove(powerUp);
            int BULLET_SPEED = 50;
            player.spawnBullet();
        }

        if (this.player.getHitbox().intersect(this.addlives.getHitbox()) == true) {
            this.pUp.remove(addlives);
            plives = plives + 1;

        }

        if (this.fingerAction == "mousedown") {

            player.setxPosition(player.x);
            player.setyPosition(player.y);
            player.updateHitbox();


        }
    }

    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);


            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.YELLOW);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);


            // DRAW THE BACKGROUND
            // -----------------------------
            canvas.drawBitmap(this.background,
                    this.bgXPosition,
                    0,
                    paintbrush);

            canvas.drawBitmap(this.background,
                    backgroundRightSide,
                    0,
                    paintbrush);



            // draw player graphic on screen
            canvas.drawBitmap(player.getImage(), player.getxPosition(), player.getyPosition(), paintbrush);
            // draw the player's hitbox
            canvas.drawRect(player.getHitbox(), paintbrush);

//            //Obstacle
//            canvas.drawBitmap(o1.getImage(), o1.getxPosition(), o1.getyPosition(), paintbrush);
//            // 2. draw the Obstacles hitbox
//            canvas.drawRect(o1.getHitbox(), paintbrush);

            for (int i = 0; i < enemyList.size(); i++) {
                Enemy alleni = enemyList.get(i);
                canvas.drawBitmap(alleni.getImage(), alleni.getxPosition(), alleni.getyPosition(), paintbrush);
            }


            paintbrush.setColor(Color.WHITE);
            canvas.drawBitmap(powerUp.getImage(), powerUp.getxPosition(), powerUp.getyPosition(), paintbrush);
            canvas.drawRect(powerUp.getHitbox(), paintbrush);

            paintbrush.setColor(Color.WHITE);
            canvas.drawBitmap(addlives.getImage(), addlives.getxPosition(), addlives.getyPosition(), paintbrush);
            canvas.drawRect(addlives.getHitbox(), paintbrush);

            // draw enemy bullet on screen
            for (int i = 0; i < this.enemy1.getBullets().size(); i++) {
                Rect bullet = this.enemy1.getBullets().get(i);
                paintbrush.setColor(Color.RED);
                paintbrush.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawRect(bullet, paintbrush);
            }

//            // draw obstacle bullet on screen
//            for (int i = 0; i < this.enemy1.getBullets().size(); i++) {
//                Rect bullet = this.enemy1.getBullets().get(i);
//                paintbrush.setColor(Color.MAGENTA);
//                paintbrush.setStyle(Paint.Style.STROKE);
//                canvas.drawRect(bullet, paintbrush);
//            }


            // draw player bullet on screen
            for (int i = 0; i < this.player.getBullets().size(); i++) {
                Rect bullet = this.player.getBullets().get(i);
                paintbrush.setColor(Color.GREEN);
                paintbrush.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawRect(bullet, paintbrush);
            }


            // DRAW GAME STATS
            // -----------------------------
            paintbrush.setColor(Color.YELLOW);
            paintbrush.setTextSize(60);
            canvas.drawText("Remaining Lives: " + plives,
                    1100,
                    800,
                    paintbrush
            );

            canvas.drawText("Score: " + score,
                    1100,
                    720,
                    paintbrush
            );

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(120);
        }
        catch (Exception e) {

        }
    }



    // User Input Functions

    String fingerAction = "";


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:

                player.x = (int) event.getX();
                player.y = (int) event.getY();

                Log.d("PUSH", "PERSON CLICKED AT: (" + event.getX() + "," + event.getY() + ")");
                fingerAction = "mousedown";
                this.player.spawnBullet();
                break;
        }
        return true;
    }
}

