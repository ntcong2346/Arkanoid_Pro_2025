import java.awt.*;
import java.util.ArrayList;

public class Brick {
    // Các loại gạch
    public static final int NORMAL = 0;
    public static final int EXPLOSIVE = 1;
    public static final int STRONG = 2;
    public static final int UNBREAKABLE = 3;

    // Giá trị điểm (Thêm mới)
    private static final int SCORE_NORMAL = 10;
    private static final int SCORE_STRONG = 30;
    private static final int SCORE_EXPLOSIVE = 10;
    private static final int SCORE_UNBREAKABLE = 0;

    private final int x, y, width, height;
    private final int type;
    private int hitPoints;
    private boolean destroyed;

    public Brick(int x, int y, int w, int h, int type) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.type = type;
        this.destroyed = false;

        switch (type) {
            case NORMAL:
            case EXPLOSIVE:
                hitPoints = 1;
                break;
            case STRONG:
                hitPoints = 3; // cần 3 lần
                break;
            case UNBREAKABLE:
                hitPoints = Integer.MAX_VALUE; // không bao giờ vỡ
                break;
        }
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

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getType() {
        return type;
    }

    public int getScoreValue() {
        switch (type) {
            case NORMAL:
                return SCORE_NORMAL;
            case STRONG:
                return SCORE_STRONG;
            case EXPLOSIVE:
                return SCORE_EXPLOSIVE;
            case UNBREAKABLE:
                return SCORE_UNBREAKABLE;
            default:
                return 0;
        }
    }

    public int hit(ArrayList<Brick> bricks) {
        if (type == UNBREAKABLE || destroyed) return 0;

        hitPoints--;
        if (hitPoints <= 0) {
            destroyed = true;
            int totalScore = getScoreValue();
            if (type == EXPLOSIVE) {
                Rectangle thisRect = getRect();
                explode(bricks);
                for (Brick b : bricks) {
                    if (b != this && !b.destroyed && b.type != UNBREAKABLE) {
                        Rectangle bRect = b.getRect();
                        if (thisRect.intersects(bRect)) {
                            b.destroyed = true;
                            totalScore += b.getScoreValue(); // Just add the score value directly
                        }
                    }
                }
            }
            return totalScore;
        }
        return 0;
    }

    private void explode(ArrayList<Brick> bricks) {
        // Tạo Vùng Nổ (3x3 lần kích thước gạch, lùi lại 1 lần width/height để bao quanh gạch đang nổ)
        Rectangle explosionZone = new Rectangle(
                this.x - this.width,       // Góc X mới
                this.y - this.height,      // Góc Y mới
                this.width * 3,            // Chiều rộng mới (gấp 3 lần)
                this.height * 3            // Chiều cao mới (gấp 3 lần)
        );

        for (Brick b : bricks) {
            if (b != this && !b.destroyed && b.type != UNBREAKABLE) {
                int totalScore = getScoreValue();
                if (explosionZone.intersects(b.getRect())) {
                    b.destroyed = true;
                    totalScore += b.getScoreValue();
                }
            }
        }
    }

    public void draw(Graphics g) {
        if (destroyed) return;
        switch (type) {
            case NORMAL:
                g.drawImage(Assets.brickNormal, x, y, width, height, null);
                break;
            case EXPLOSIVE:
                g.drawImage(Assets.brickExplosive, x, y, width, height, null);
                break;
            case STRONG:
                if (hitPoints == 3) {
                    g.drawImage(Assets.brickStrong3, x, y, width, height, null);
                } else if (hitPoints == 2) {
                    g.drawImage(Assets.brickStrong2, x, y, width, height, null);
                } else if (hitPoints == 1) {
                    g.drawImage(Assets.brickStrong1, x, y, width, height, null);
                }
                break;
            case UNBREAKABLE:
                g.drawImage(Assets.brickUnbreakable, x, y, width, height, null);
                break;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
}
