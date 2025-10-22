package entity;

import graphics.Assets;

import java.awt.*;

public class Paddle extends MovableObject {
    private int speed; //Overall speed
    private boolean glowing;
    private int glowTimer;// số frame phát sáng (~0.15s nếu 100fps)

    public Paddle(double x, double y, int width, int height, int speed) {
        super(x, y, width, height);
        this.speed = speed;
        this.glowing = false;
        this.glowTimer = 0;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void moveLeft() {
        x -= speed;
        if (getLeft() < 0) {
            x = width / 2.0;
        }
    }

    public void moveRight(int panelWidth) {
        x += speed;
        if (getRight() > panelWidth) {
            x = panelWidth - width / 2.0;
        }
    }

    public void setGlow() {
        glowing = true;
        glowTimer = 15;
    }

    public void updateGlow() {
        if (glowing) {
            glowTimer--;
            if (glowTimer <= 0) glowing = false;
        }
    }

    @Override
    public void update() {
        updateGlow();
    }

    @Override
    public void render(Graphics g) {
        int drawX = (int)getLeft();
        int drawY = (int)getTop();

        if (glowing)
            g.drawImage(Assets.paddleGlow, drawX, drawY, width, height, null);
        else
            g.drawImage(Assets.paddleNormal, drawX, drawY, width, height, null);
    }
}
