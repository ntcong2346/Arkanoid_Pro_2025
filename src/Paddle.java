import java.awt.*;
import java.awt.event.KeyEvent;

public class Paddle {
    private int x, y; //Paddle location on screen
    private int width, height; //Paddle size
    private int speed; //Overall speed

    private boolean glowing;
    private int glowTimer;// số frame phát sáng (~0.15s nếu 100fps)

    private boolean leftPressed;
    private boolean rightPressed;

    public Paddle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.speed = 6;
        this.glowing = true;
        this.glowTimer = 0;
        this.leftPressed = false;
        this.rightPressed = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) {
            x = 0;
        }
    }

    public void moveRight(int panelWidth) {
        x += speed;
        if (x + width > panelWidth)
            x = panelWidth - width;
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

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void draw(Graphics g) {
        if (glowing) {
            g.drawImage(Assets.paddleGlow, x, y, width, height, null);
        } else {
            g.drawImage(Assets.paddleNormal, x, y, width, height, null);
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    public void reset(int nx, int ny) {
        // Nếu cần reset vị trí paddle, thêm code ở đây
        x = nx;
        y = ny;
        // Hiện tại để trống
    }
}
