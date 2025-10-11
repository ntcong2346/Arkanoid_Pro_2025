import java.awt.*;
import java.util.Random;

public class Ball {
    private double x, y; //Position of ball
    private double dx, dy; //Speed & direction by axis of ball
    private int radius;
    private int speed;
    private boolean inMotion; //Is ball flying?

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public double getDX() { return dx; }
    public double getDY() { return dy; }
    public void setDX(double dx) { this.dx = dx; }
    public void setDY(double dy) { this.dy = dy; }

    public int getRadius() { return radius; }
    public int getDiameter() { return radius * 2; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public boolean isInMotion() { return inMotion; }

    public Ball(int x, int y, int r, int speed) {
        this.x = x;
        this.y = y;
        this.radius = r;
        this.speed = speed; // Gán giá trị speed từ tham số
        this.dx = 0;
        this.dy = 0;
        this.inMotion = false;
    }

    public void launch() {
        Random rand = new Random();
        if (!inMotion) {
            inMotion = true;
            dx = rand.nextBoolean() ? speed : -speed;
            dy = -speed; // bay len
        }
    }

    public void move() {
        if (inMotion) {
            x += dx;
            y += dy;
        }
    }

    public void draw(Graphics g) {
        g.drawImage(Assets.ball, (int)(x - radius), (int)(y - radius), 2 * radius, 2 * radius, null);
    }

    public Rectangle getRect() {
        return new Rectangle((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
    }

    public boolean hitsPaddle(Paddle paddle) {
        Rectangle ballRect = getRect();
        Rectangle paddleRect = paddle.getRect();
        return ballRect.intersects(paddleRect);
    }

    public boolean hitsBrick(Brick brick) {
        Rectangle ballRect = getRect();
        Rectangle brickRect = brick.getRect();
        return ballRect.intersects(brickRect);
    }

    public void reset(int nx, int ny) {
        x = nx;
        y = ny;
        dx = 0;
        dy = 0;
        inMotion = false;
    }
}
