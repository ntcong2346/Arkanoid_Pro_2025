import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    
    // hằng số trong game
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Timer timer;
    private Paddle paddle;
    private Ball ball;
    private ArrayList<Brick> bricks;
    private boolean leftPressed = false, rightPressed = false;
    private int score = 0, lives = 3;
    private boolean gameOver = false;
    private boolean win = false;
    private int level = 1;
    private final int MAX_LEVEL = 5;
    private boolean showStartText = true;
    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        initGame();

        timer = new Timer(10, this); // 100 fps
        timer.start();
    }

    private void initGame() {
        paddle = new Paddle(WIDTH/2 - 60, HEIGHT - 50, 120, 15);
        ball = new Ball(WIDTH/2, HEIGHT - 70, 12, 8, 8);
        bricks = new ArrayList<>();

        int rows = 5, cols = 10;
        int brickW = 70, brickH = 20;
        int offsetX = (WIDTH - cols*brickW)/2;
        int offsetY = 50;

        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                int x = offsetX + c * brickW;
                int y = offsetY + r * brickH;

                bricks.add(new Brick(x, y, brickW-2, brickH-2, Brick.NORMAL));
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (leftPressed) paddle.moveLeft();
            if (rightPressed) paddle.moveRight(WIDTH);
            // ball in the paddle
            if (!ball.inMotion) {
                ball.x = paddle.x + paddle.width/2 - ball.diameter/2;
                ball.y = paddle.y - ball.diameter - 1;
            }
        
            ball.move();

            // ball vs walls
            if (ball.x <= 0 || ball.x + ball.diameter >= WIDTH) ball.dx *= -1;
            if (ball.y <= 0) ball.dy *= -1;

            // ball vs paddle
           if (ball.getRect().intersects(paddle.getRect())) {
                ball.dy = -Math.abs(ball.dy); // bật ngược lên
                paddle.glow();                // đổi ảnh sang paddle sáng

    // Sau 100ms đổi lại paddle thường
    new javax.swing.Timer(1000, _ -> paddle.normal()).start();
}


            // ball vs bricks
           for (Brick b : bricks) {
    if (!b.destroyed && ball.getRect().intersects(b.getRect())) {
        Rectangle br = b.getRect();
        Rectangle bl = ball.getRect();

        // Gọi hit để xử lý logic gạch
        b.hit(bricks);
        if (b.destroyed && b.type != Brick.UNBREAKABLE) {
            score += 100;
        }

        // Xác định hướng va chạm
        double overlapLeft   = bl.getMaxX() - br.getMinX();
        double overlapRight  = br.getMaxX() - bl.getMinX();
        double overlapTop    = bl.getMaxY() - br.getMinY();
        double overlapBottom = br.getMaxY() - bl.getMinY();

        // Tìm cạnh có overlap nhỏ nhất → đó là cạnh va chạm
        double minOverlapX = Math.min(overlapLeft, overlapRight);
        double minOverlapY = Math.min(overlapTop, overlapBottom);

        if (minOverlapX < minOverlapY) {
            // Va chạm ngang → đảo dx
            ball.dx = -ball.dx;
            if (overlapLeft < overlapRight) {
                ball.x = br.x - ball.diameter; // đẩy bóng ra bên trái
            } else {
                ball.x = br.x + br.width;      // đẩy bóng ra bên phải
            }
        } else {
            // Va chạm dọc → đảo dy
            ball.dy = -ball.dy;
            if (overlapTop < overlapBottom) {
                ball.y = br.y - ball.diameter; // đẩy bóng lên trên
            } else {
                ball.y = br.y + br.height;     // đẩy bóng xuống dưới
            }
        }

        break; // chỉ xử lý 1 gạch mỗi frame
    }
}


            // ball out of bounds
            if (ball.y > HEIGHT) {
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                } else {
                    ball.reset(paddle.x + paddle.width/2 - ball.diameter/2, paddle.y - ball.diameter - 2);
                }
            }

            // Kiểm tra còn gạch phá được không
            boolean allDestroyed = true;
            for (Brick b : bricks) {
                if (!b.destroyed && b.type != Brick.UNBREAKABLE) {
                    allDestroyed = false;
                    break;
                }
            }
            if (allDestroyed) {
                level++;
                if (level > 5) {
                    gameOver = true;
                    win = true;
                } else {
                    createLevel(level);
                    ball.reset(paddle.x + paddle.width/2 - ball.diameter/2,
                               paddle.y - ball.diameter - 2);
                }
            }
        }
        if (score >= 500000 ) {
            win = true;
            gameOver = true;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
         g.drawImage(Assets.background, 0, 0, WIDTH, HEIGHT, null);
        paddle.draw(g);
        ball.draw(g);

        for (Brick b : bricks) {
            if (!b.destroyed) b.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, WIDTH - 100, 20);

        if (showStartText) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Press SPACE to Start", WIDTH/2 - 130, HEIGHT/2);
        g.drawString("Using LEFT/RIGHT to move", WIDTH/2 - 160, HEIGHT/2 + 50);

    }
          if (win) {
        // Vẽ chữ
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Giỏi thíeeeee!", WIDTH/2 - 120 , HEIGHT/2 - 100 );
    }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString(" Press R to Restart", 235, HEIGHT/2);
        }
       
    }

    // Quản lý phím bấm
    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT) leftPressed = true;
        if (k == KeyEvent.VK_RIGHT) rightPressed = true;
        if (k == KeyEvent.VK_SPACE) ball.launch(); // bấm SPACE để bắn bóng
        if (k == KeyEvent.VK_R && gameOver) {
            gameOver = false; score = 0; lives = 3;
            initGame();
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            showStartText = false;
            ball.launch();
        }
        if (k == KeyEvent.VK_S) {
            cheatNextLevel();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT) leftPressed = false;
        if (k == KeyEvent.VK_RIGHT) rightPressed = false;
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    private void createLevel(int lvl) {
    bricks.clear();
    int rows = 6, cols = 11;
    int brickW = 60, brickH = 20;
    int offsetX = (WIDTH - cols * brickW) / 2;
    int offsetY = 50;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            int x = offsetX + c * brickW;
            int y = offsetY + r * brickH;

            int type = Brick.NORMAL;

            if (lvl == 1) {
                // Toàn gạch thường
                type = Brick.NORMAL;
            }
            else if (lvl == 2) {
                // Thêm gạch không phá vỡ ở hàng giữa 
                if (r == 3 && c % 3 == 0) type = Brick.UNBREAKABLE;
                else if (r == 5 && c % 3 == 1) type = Brick.UNBREAKABLE;
                else type = Brick.NORMAL;
            }
            else if (lvl == 3) {
                // Gạch nổ rải rác
                if ((r + c) % 7 == 0) type = Brick.EXPLOSIVE;
                else type = Brick.NORMAL;
            }
            else if (lvl == 4) {
                // Gạch bền ở giữa, gạch thường ở ngoài
                if (r == 1 || r == 3) type = Brick.STRONG;
                else type = Brick.NORMAL;
            }
            else if (lvl == 5) {
                // Mix tất cả: khó nhất
                if ((r + c) % 9 == 0) type = Brick.UNBREAKABLE;
                else if ((r + c) % 7 == 0) type = Brick.EXPLOSIVE;
                else if ((r + c) % 5 == 0) type = Brick.STRONG;
                else type = Brick.NORMAL;
            }

            bricks.add(new Brick(x, y, brickW - 2, brickH - 2, type));
        }
    }
}
private void cheatNextLevel() {
    if (gameOver) return; 

    int gained = 0;
    for (Brick b : bricks) {
        if (!b.destroyed && b.type != Brick.UNBREAKABLE) {
            b.destroyed = true;
            gained += 100; // cộng điểm cho mỗi gạch phá được
        }
    }

    // Cộng điểm
    score += gained;

    // Chuyển level: tăng level rồi tạo level mới hoặc xử lý thắng
    level++;
    if (level > MAX_LEVEL) {
        // đã vượt level cuối -> win
        win = true;
        gameOver = true;
    } else {
        // tạo level mới
        createLevel(level);

        // reset vị trí paddle và ball về trạng thái chờ
        paddle.reset(WIDTH/2 - paddle.width/2);
        ball.reset(paddle.x + paddle.width/2 - ball.diameter/2,
                   paddle.y - ball.diameter - 2);
    }

    // đảm bảo vẽ lại ngay
    repaint();
}

}

