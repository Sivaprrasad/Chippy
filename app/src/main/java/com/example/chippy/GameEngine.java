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
    int max = 5;

//    int hp;
    int score = 0;
    int highScore = 0;
//    boolean enemyUp = true;

    List<PowerUps> pUp = new ArrayList<PowerUps>();
    // ----------------------------
    // ## GAME STATS
    // ----------------------------

    int playerlives =  30;
    int enemylives = 10;
    int oEnemylives = 5;
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


    Bitmap powpic;
    PowerUps powerUp;
    PowerUps addlives;



    //Enemy enemy2;


    Bitmap background;
    int bgXPosition = 0;
    int backgroundRightSide = 0;

    int lives = 10;
    private int bullet;


    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        int SQUARE_WIDTH = 100;
//        this.hp = 5;
        this.score = 0;


        this.printScreenInfo();

        // @TODO: Add your sprites



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

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen
    }
    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location

    }
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
            if(this.highScore < this.score) this.highScore = this.score;
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
//        this.hp = 5;
        gameIsRunning = true;
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

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
            // if mousedown, then move player up
            // Make the UP movement > than down movement - this will
            // make it look like the player is moving up alot
            player.setyPosition(player.getyPosition() - 100);
            player.updateHitbox();
        }
        else if (this.fingerAction == "mouseup") {
            // if mouseup, then move player down
            player.setyPosition(player.getyPosition() + 10);
            player.updateHitbox();
        }


        // DEAL WITH BULLETS

        // Shoot a bullet every (5) iterations of the loop
        if (numLoops % 10  == 0) {
            this.enemy1.spawnBullet();
        }

//         Shoot a bullet every (5) iterations of the loop
        if (numLoops % 5  == 0) {
            this.player.spawnBullet();
        }


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



        // COLLISION DETECTION BETWEEN BULLET AND PLAYER
        for (int i = 0; i < this.enemy1.getBullets().size();i++) {
            Rect bullet = this.enemy1.getBullets().get(i);

            if (this.player.getHitbox().intersect(bullet)) {
                this.player.updateHitbox();
                playerlives = playerlives - 1;
            }
        }

        if (playerlives == 0) {
            getContext().startActivity(new Intent(getContext(),LooseScreenActivity.class));
        }

        // COLLISION DETECTION BETWEEN BULLET AND ENEMY
        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect bullet = this.player.getBullets().get(i);

            if (this.enemy1.getHitbox().intersect(bullet)) {
                this.enemy1.updateHitbox();
                this.player.getBullets().remove(bullet);
                enemylives = enemylives - 1;
                score = score + 1;
            }


            if (this.enemy2.getHitbox().intersect(bullet)) {
                this.player.getBullets().remove(bullet);
                oEnemylives = oEnemylives - 1;
                if (oEnemylives == 0) {
                    enemyList.remove(enemy2);
                    enemy2.setHitbox(new Rect(0, 0, 0, 0));
                    score = score + 1;

                }
            }

            if (this.enemy3.getHitbox().intersect(bullet)) {
                this.enemy3.updateHitbox();
                this.player.getBullets().remove(bullet);
                oEnemylives = oEnemylives - 1;
                if (oEnemylives == 0) {
                    enemyList.remove(enemy3);
                    enemy3.setHitbox(new Rect(0, 0, 0, 0));
                    score = score + 1;

                }
            }
            if (this.enemy4.getHitbox().intersect(bullet)) {
                this.enemy4.updateHitbox();
                this.player.getBullets().remove(bullet);
                oEnemylives = oEnemylives - 1;
                if (oEnemylives == 0) {
                    enemyList.remove(enemy4);
                    enemy4.setHitbox(new Rect(0, 0, 0, 0));
                    score = score + 1;

                }
            }

            if (enemylives == 0) {
                enemyList.removeAll(enemyList);
                enemy1.setHitbox(new Rect(0, 0, 0, 0));
                getContext().startActivity(new Intent(getContext(), WinScreenActivity.class));

            }
        }

        this.powerUp.setxPosition(this.powerUp.getxPosition()-15);
        this.powerUp.updateHitbox();
        this.addlives.setxPosition(this.addlives.getxPosition()-10);
        this.addlives.updateHitbox();

        int rand = new Random().nextInt((max - min) + 1) + min;

        if(this.powerUp.getxPosition()<0 ){
            this.powpic = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.powerup);


            this.powerUp = new PowerUps(getContext(),rand*1000, rand*370,powpic);
            pUp.add(powerUp);
            this.powerUp.updateHitbox();

        }

        if(this.addlives.getxPosition()<0 ){
            this.powpic = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.extralife);

            this.addlives = new PowerUps(getContext(),rand*1000, rand*370,powpic);
            pUp.add(addlives);
            this.addlives.updateHitbox();

        }


        // @TODO:  Check collisions between enemy and player
        if (this.player.getHitbox().intersect(this.enemy1.getHitbox()) == true) {
            // decrease the lives
            this.player.setxPosition(100);
            this.player.setyPosition(600);
            this.player.updateHitbox();
            playerlives = playerlives - 1;
        }

        if (this.player.getHitbox().intersect(this.enemy2.getHitbox()) == true) {
            // decrease the lives
            this.player.setxPosition(100);
            this.player.setyPosition(600);
            this.player.updateHitbox();
            playerlives = playerlives - 1;
        }

        if (this.player.getHitbox().intersect(this.enemy3.getHitbox()) == true) {
            // decrease the lives
            this.player.setxPosition(100);
            this.player.setyPosition(600);
            this.player.updateHitbox();
            playerlives = playerlives - 1;
        }

        if (this.player.getHitbox().intersect(this.enemy4.getHitbox()) == true) {
            // decrease the lives
            this.player.setxPosition(100);
            this.player.setyPosition(600);
            this.player.updateHitbox();
            playerlives = playerlives - 1;
        }

        if (this.player.getHitbox().intersect(this.powerUp.getHitbox()) == true) {
            this.pUp.remove(powerUp);
            int BULLET_SPEED = 50;
            player.spawnBullet();
        }

        if (this.player.getHitbox().intersect(this.addlives.getHitbox()) == true) {
            this.pUp.remove(addlives);
            playerlives = playerlives + 1;

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
                canvas.drawRect(bullet, paintbrush);
            }


            // draw player bullet on screen
            for (int i = 0; i < this.player.getBullets().size(); i++) {
                Rect bullet = this.player.getBullets().get(i);
                canvas.drawRect(bullet, paintbrush);
            }


            // DRAW GAME STATS
            // -----------------------------
            paintbrush.setColor(Color.YELLOW);
            paintbrush.setTextSize(60);
            canvas.drawText("Remaining Lives: " + playerlives,
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



    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------


    String fingerAction = "";


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:

                player.x = (int) event.getX();
                player.y = (int) event.getY();

                Log.d("PUSH", "PERSON CLICKED AT: (" + event.getX() + "," + event.getY() + ")");
                fingerAction = "mousedown";
//                this.player.spawnBullet();
                break;
        }
        return true;
    }
}

