package com.example.chippy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class PowerUps {

    private Bitmap image;
    private int xPosition;
    private int yPosition;

    private Bitmap powpic;
    private Rect hitbox;

    public PowerUps(Context context, int x, int y, Bitmap image ) {
        // 1. set up the initial position of the Enemy
        this.xPosition = x;
        this.yPosition = y;
        this.image = image;
        this.hitbox = new Rect(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.image.getWidth(),
                this.yPosition + this.image.getHeight()
        );
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

    public Bitmap getPowpic() {
        return powpic;
    }

    public void setPowpic(Bitmap powpic) {
        this.powpic = powpic;
    }

    public int getrand(int min, int max){

        return (int)(Math.random() * max + min);

    }

    public void updateHitbox() {
        this.hitbox.left = this.xPosition;
        this.hitbox.top = this.yPosition;
        this.hitbox.right = this.xPosition + this.image.getWidth();
        this.hitbox.bottom = this.yPosition + this.image.getHeight();
    }


}
