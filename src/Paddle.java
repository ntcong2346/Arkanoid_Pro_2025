import java.awt.*;


public class Paddle {
    int x, y, width, height;
    int speed = 6;
    private boolean glowing = false;

    public Paddle(int x, int y, int w, int h) {
        this.x = x; 
        this.y = y; 
        this.width = w; 
        this.height = h;
    }
    public void moveLeft() { x -= speed; if (x < 0) x = 0; }
    public void moveRight(int panelWidth) { 
        x += speed; 
        if (x + width > panelWidth) x = panelWidth - width; 
    }

    public void draw(Graphics g) {
        if (glowing) {
            g.drawImage(Assets.paddleGlow,x,y,width,height,null);
        } else{
            g.drawImage(Assets.paddleNormal, x, y, width, height, null);
        }
        
    }
    public void glow() {
        glowing = true;
    }
    public void normal() {
        glowing = false;
    }
    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
    public void reset(int x) {
        this.x = x;
    }

}

    