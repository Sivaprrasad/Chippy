package com.example.chippy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;

public class Player {

    public int x;
    public int y;

    // PROPERTIES
    private Bitmap image;
    private Rect hitbox;


    private int xPosition;
    private int yPosition;

    private ArrayList<Rect> pbullets = new ArrayList<Rect>();
    private final int pBULLET_WIDTH = 20;



    public Player(Context context, int x, int y) {
        // 1. set up the initial position of the Enemy
        this.xPosition = x;
        this.yPosition = y;


        // 2. Set the default image - all enemies have same image
        this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);

        // 3. Set the default hitbox - all enemies have same hitbox
        this.hitbox = new Rect(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.image.getWidth(),
                this.yPosition + this.image.getHeight()
        );
    }


    // GETTER AND SETTER METHODS
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }


    public ArrayList<Rect> getBullets() {
        return pbullets;
    }

    public void setBullets(ArrayList<Rect> bullets) {
        this.pbullets = bullets;
    }


    public int getBulletWidth() {
        return pBULLET_WIDTH;
    }


    public void updateHitbox() {
        this.hitbox.left = this.xPosition;
        this.hitbox.top = this.yPosition;
        this.hitbox.right = this.xPosition + this.image.getWidth();
        this.hitbox.bottom = this.yPosition + this.image.getHeight();
    }

    // Make a new bullet
    public void spawnBullet() {
        // make bullet come out of middle of enemty
        Rect pbullet = new Rect(this.xPosition,
                this.yPosition + this.image.getHeight() / 2,
                this.xPosition + pBULLET_WIDTH,
                this.yPosition + this.image.getHeight() / 2 + pBULLET_WIDTH
        );
        this.pbullets.add(pbullet);
        //test commit
    }
}