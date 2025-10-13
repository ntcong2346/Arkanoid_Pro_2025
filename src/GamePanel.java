import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
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
    private int level = 1; // Thêm biến level

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
        level = 1;
        score = 0;
        lives = 3;
        win = false;
        gameOver = false;
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 50, 120, 15);
        paddle.setSpeed(MenuPanel.paddleSpeed);
        ball = new Ball(WIDTH / 2, HEIGHT - 70, MenuPanel.ballSize, MenuPanel.ballSpeed);
        bricks = new ArrayList<>();
        createLevel(level);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (leftPressed)
                paddle.moveLeft();
            if (rightPressed)
                paddle.moveRight(WIDTH);
            // ball in the paddle
            if (!ball.isInMotion()) {
                ball.setX(paddle.getX() + paddle.getWidth() / 2.0);
                ball.setY(paddle.getY() - ball.getRadius() - 1);
            }

            ball.move();

            // ball vs walls
            if (ball.getX() - ball.getRadius() <= 0 || ball.getX() + ball.getRadius() >= WIDTH) {
                ball.bounceHorizontal();
            }
            if (ball.getY() - ball.getRadius() <= 0) { // Tường Trên
                ball.bounceVertical();
            }

            // ball vs paddle
            if (ball.getRect().intersects(paddle.getRect())) {
                ball.bounceVertical();
                paddle.setGlow(); // Kích hoạt hiệu ứng phát sáng
            }

            paddle.updateGlow(); // Cập nhật hiệu ứng mỗi frame

            // ball vs bricks
            Rectangle nextBallRect = new Rectangle(
                    (int) Math.round(ball.getX() + ball.getDX() - ball.getRadius()),
                    (int) Math.round(ball.getY() + ball.getDY() - ball.getRadius()),
                    ball.getDiameter(),
                    ball.getDiameter()
            );

            for (Brick b : bricks) {
                if (!b.isDestroyed() && nextBallRect.intersects(b.getRect())) {
                    Rectangle brickRect = b.getRect();

                    // Xác định hướng va chạm
                    boolean hitFromLeft = ball.getX() + ball.getDiameter() <= brickRect.x && ball.getX() + ball.getDiameter() + ball.getDX() > brickRect.x;
                    boolean hitFromRight = ball.getX() >= brickRect.x + brickRect.width && ball.getX() + ball.getDX() < brickRect.x + brickRect.width;
                    boolean hitFromTop = ball.getY() + ball.getDiameter() <= brickRect.y && ball.getY() + ball.getDiameter() + ball.getDY() > brickRect.y;
                    boolean hitFromBottom = ball.getY() >= brickRect.y + brickRect.height && ball.getY() + ball.getDY() < brickRect.y + brickRect.height;

                    // Đảo chiều vận tốc phù hợp
                    if (hitFromLeft || hitFromRight) {
                        ball.bounceHorizontal();
                    } else if (hitFromTop || hitFromBottom) {
                        ball.bounceVertical();
                    } else {
                        // Nếu không xác định được, đảo cả hai
                        ball.bounceHorizontal();
                        ball.bounceVertical();
                    }

                    boolean wasDestroyed = b.isDestroyed(); // Kiểm tra trạng thái trước khi hit
                    b.hit(bricks); // Xử lý logic phá gạch

                    // --- Bổ sung logic tính điểm ---
                    if (!wasDestroyed && b.isDestroyed()) {
                        score += b.getScoreValue();
                    }

                    break; // Chỉ xử lý va chạm với 1 viên gạch mỗi frame
                }
            }

            // ball out of bounds
            if (ball.getY() > HEIGHT) {
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                } else {
                    // reset ball position in the paddle
                    ball.reset(paddle.getX() + paddle.getWidth() / 2, paddle.getY() - ball.getRadius() - 2);
                }
            }

            // Kiểm tra qua màn
            boolean allDestroyed = true;
            for (Brick b : bricks) {
                if (!b.isDestroyed() && b.getType() != Brick.UNBREAKABLE) {
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
                    ball.reset(paddle.getX() + paddle.getWidth() / 2,
                            paddle.getY() - ball.getRadius() - 2);
                }
            }
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
            if (!b.isDestroyed())
                b.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, WIDTH - 100, 20);
        g.drawString("Level: " + level, WIDTH / 2 - 40, 20);

        if (!ball.isInMotion() && !gameOver) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Bấm SPACE để bắt đầu", WIDTH/2 - 130, HEIGHT/2 + 100);
        }

        if (win) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Bạn đã chiến thắng!", WIDTH/2 - 180 , HEIGHT/2 - 100 );
        }

        if (gameOver && !win) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over! Press R to Restart", 120, HEIGHT/2);
        }
        if (gameOver && win) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Press R to Restart", 235, HEIGHT/2 + 50);
        }
    }

    // KeyListener
    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT)
            leftPressed = true;
        if (k == KeyEvent.VK_RIGHT)
            rightPressed = true;
        if (k == KeyEvent.VK_SPACE)
            ball.launch(); // press space to launch the ball
        if (k == KeyEvent.VK_R && gameOver) {
            initGame();
        }
        if (k == KeyEvent.VK_S) { // Cheat: chuyển vòng
            level++;
            if (level > 5)
                level = 1;
            createLevel(level);
            ball.reset(paddle.getX() + paddle.getWidth() / 2, paddle.getY() - ball.getRadius() - 2);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT)
            leftPressed = false;
        if (k == KeyEvent.VK_RIGHT)
            rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void createLevel(int lvl) {
        bricks.clear();

        int rows = 6;
        int cols = 12;
        int brickW = 60, brickH = 20;
        int offsetX = (WIDTH - cols * brickW) / 2;
        int offsetY = 60;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = offsetX + c * brickW;
                int y = offsetY + r * brickH;

                int type = Brick.NORMAL;

                switch (lvl) {
                    case 1:
                        // Toàn bộ là gạch thường
                        type = Brick.NORMAL;
                        break;

                    case 2:
                        // Thêm gạch UNBREAKABLE ở hàng giữa
                        if (r == 2 && c % 3 == 0)
                            type = Brick.UNBREAKABLE;
                        else
                            type = Brick.NORMAL;
                        break;

                    case 3:
                        // Thêm gạch EXPLOSIVE rải rác
                        if ((r + c) % 7 == 0)
                            type = Brick.EXPLOSIVE;
                        else
                            type = Brick.NORMAL;
                        break;

                    case 4:
                        // Hàng 1 và 3 là gạch STRONG
                        if (r == 1 || r == 3)
                            type = Brick.STRONG;
                        else
                            type = Brick.NORMAL;
                        break;

                    default: // Level 5 trở đi
                        if ((r + c) % 9 == 0)
                            type = Brick.UNBREAKABLE;
                        else if ((r + c) % 7 == 0)
                            type = Brick.EXPLOSIVE;
                        else if ((r + c) % 5 == 0)
                            type = Brick.STRONG;
                        else
                            type = Brick.NORMAL;
                        break;
                }

                bricks.add(new Brick(x, y, brickW - 2, brickH - 2, type));
            }
        }
    }
}