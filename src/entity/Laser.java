package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Represents a laser shot by the paddle with laser power-up.
 */
public class Laser extends MovableObject {
    /** Vertical velocity in pixels per frame. */
    private static final double VELOCITY_Y = -8.0;

    /** Width of the laser. */
    private static final int WIDTH = 4;

    /** Height of the laser. */
    private static final int HEIGHT = 12;

    public Laser(int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        this.dy = VELOCITY_Y;  // Bắn lên
        this.dx = 0.0;         // Không di chuyển ngang
    }

    @Override
    public void update() {
        super.update();  // Sử dụng move() từ MovableObject
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y, width, height);
        g.setColor(Color.YELLOW);
        g.drawRect((int) x, (int) y, width, height);
    }

    public boolean isActive() {
        return y > -HEIGHT;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}