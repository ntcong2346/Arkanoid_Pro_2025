import java.awt.*;
import java.util.Random;

public class Ball {
    double x, y, diameter;
    double dx, dy;
    double speed = 3;
    boolean inMotion = false;
    public Ball(double x, double y, double d, double dx, double dy) {
        this.x = x; this.y = y; this.diameter = d; 
        this.dx = dx; this.dy = dy;
    }

    public void move() {
        if (inMotion) {
            x+=dx; y+=dy;
        }
    }

    public void launch() {
        if(!inMotion) {
            inMotion = true;
            Random rand = new Random(); // random direction of the ball
            dx = rand.nextBoolean() ? speed : -speed;
            dy = -speed;
        }
       
    }

    public void draw(Graphics g) {
        g.drawImage(Assets.ball, (int)x, (int)y, (int)diameter, (int)diameter, null);
    }

    public Rectangle getRect() {
        return new Rectangle((int)x, (int)y, (int)diameter, (int)diameter);
    }

    public void reset(double nx, double ny) {
        x = nx; y = ny;
        dx = 0; dy = 0;
        inMotion = false;
    }
}
