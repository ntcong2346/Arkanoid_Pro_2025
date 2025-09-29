import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CoopGamePanel extends JPanel implements ActionListener, KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Timer timer;
    private Paddle paddle1, paddle2;
    private Ball ball;
    private int paddleLaunchIndex = 0; // 0: paddle1, 1: paddle2
    private ArrayList<Brick> bricks;
    private boolean leftPressed = false, rightPressed = false;
    private boolean aPressed = false, dPressed = false;
    private int score = 0, lives = 3;
    private boolean gameOver = false;
    private boolean win = false;
    private int level = 1;

    public CoopGamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initGame();

        timer = new Timer(10, this);
        timer.start();
    }

    private void initGame() {
        score = 0;
        lives = 3;
        win = false;
        gameOver = false;
        level = 1;
        paddle2 = new Paddle(WIDTH/4 - 60, HEIGHT - 50, 120, 15);
        paddle1 = new Paddle(WIDTH*3/4 - 60, HEIGHT - 50, 120, 15);
        paddle1.speed = MenuPanel.paddleSpeed;
        paddle2.speed = MenuPanel.paddleSpeed;
        ball = new Ball(0, 0, MenuPanel.ballSize, MenuPanel.ballSpeed);
        paddleLaunchIndex = 0;
        bricks = new ArrayList<>();
        createLevel(level);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Paddle movement
            if (leftPressed) paddle1.moveLeft();
            if (rightPressed) paddle1.moveRight(WIDTH);
            if (aPressed) paddle2.moveLeft();
            if (dPressed) paddle2.moveRight(WIDTH);

            paddle1.updateGlow();
            paddle2.updateGlow();

            // Ball follows paddle before launch
            if (!ball.inMotion) {
                Paddle launchPad = (paddleLaunchIndex == 0) ? paddle1 : paddle2;
                ball.x = launchPad.x + launchPad.width/2 - ball.diameter/2;
                ball.y = launchPad.y - ball.diameter - 1;
            }

            ball.move();

            // Ball vs walls
            if (ball.x <= 0 || ball.x + ball.diameter >= WIDTH) ball.dx *= -1;
            if (ball.y <= 0) ball.dy *= -1;

            // Ball vs paddles (chỉ xử lý va chạm với 1 paddle mỗi frame)
            boolean collided = false;
            if (ball.getRect().intersects(paddle1.getRect())) {
                ball.dy *= -1;
                paddle1.setGlow();
                collided = true;
            }
            if (!collided && ball.getRect().intersects(paddle2.getRect())) {
                ball.dy *= -1;
                paddle2.setGlow();
                collided = true;
            }

            // Ball vs bricks
            Rectangle nextBallRect = new Rectangle(
                (int)(ball.x + ball.dx),
                (int)(ball.y + ball.dy),
                ball.diameter,
                ball.diameter
            );

            for (Brick b : bricks) {
                if (!b.destroyed && nextBallRect.intersects(b.getRect())) {
                    Rectangle brickRect = b.getRect();

                    // Xác định hướng va chạm
                    boolean hitFromLeft = ball.x + ball.diameter <= brickRect.x && ball.x + ball.diameter + ball.dx > brickRect.x;
                    boolean hitFromRight = ball.x >= brickRect.x + brickRect.width && ball.x + ball.dx < brickRect.x + brickRect.width;
                    boolean hitFromTop = ball.y + ball.diameter <= brickRect.y && ball.y + ball.diameter + ball.dy > brickRect.y;
                    boolean hitFromBottom = ball.y >= brickRect.y + brickRect.height && ball.y + ball.dy < brickRect.y + brickRect.height;

                    // Đảo chiều vận tốc phù hợp
                    if (hitFromLeft || hitFromRight) {
                        ball.dx *= -1;
                    } else if (hitFromTop || hitFromBottom) {
                        ball.dy *= -1;
                    } else {
                        // Nếu không xác định được, đảo cả hai
                        ball.dx *= -1;
                        ball.dy *= -1;
                    }

                    b.hit(bricks); // Xử lý logic phá gạch
                    break; // Chỉ xử lý va chạm với 1 viên gạch mỗi frame
                }
            }

            // Ball out of bounds
            if (ball.y > HEIGHT) {
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                } else {
                    paddleLaunchIndex = 1 - paddleLaunchIndex; // Đổi paddle launch
                    ball.reset(0, 0); // Vị trí sẽ được cập nhật ở trên
                }
            }

            // Kiểm tra qua màn
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
                    paddleLaunchIndex = 1 - paddleLaunchIndex;
                    ball.reset(0, 0);
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(Assets.background, 0, 0, WIDTH, HEIGHT, null);
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);

        for (Brick b : bricks) {
            if (!b.destroyed) b.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, WIDTH - 100, 20);
        g.drawString("Level: " + level, WIDTH/2 - 40, 20);

        if (!ball.inMotion && !gameOver) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Bấm SPACE để bắt đầu", WIDTH/2 - 150, HEIGHT/2 + 100);
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
        if (k == KeyEvent.VK_LEFT) leftPressed = true;
        if (k == KeyEvent.VK_RIGHT) rightPressed = true;
        if (k == KeyEvent.VK_A) aPressed = true;
        if (k == KeyEvent.VK_D) dPressed = true;
        if (k == KeyEvent.VK_SPACE) ball.launch();
        if (k == KeyEvent.VK_R && gameOver) initGame();
         if (k == KeyEvent.VK_S) { // Cheat: chuyển vòng
            level++;
            if (level > 5)
                level = 1;
            createLevel(level);
            ball.reset(paddle1.x + paddle1.width / 2 - ball.diameter / 2, paddle1.y - ball.diameter - 2);
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT) leftPressed = false;
        if (k == KeyEvent.VK_RIGHT) rightPressed = false;
        if (k == KeyEvent.VK_A) aPressed = false;
        if (k == KeyEvent.VK_D) dPressed = false;
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    private void createLevel(int lvl) {
        bricks.clear();
        int rows = 5, cols = 10;
        int brickW = 70, brickH = 20;
        int offsetX = (WIDTH - cols * brickW) / 2;
        int offsetY = 50;

        switch (lvl) {
            case 1:
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < cols; c++)
                        bricks.add(new Brick(offsetX + c * brickW, offsetY + r * brickH, brickW - 2, brickH - 2, Brick.NORMAL));
                break;
            case 2:
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < cols; c++) {
                        int type = (r == 2 && c >= 3 && c <= 6) ? Brick.UNBREAKABLE : Brick.NORMAL;
                        bricks.add(new Brick(offsetX + c * brickW, offsetY + r * brickH, brickW - 2, brickH - 2, type));
                    }
                break;
            case 3:
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < cols; c++) {
                        int type = ( (r == 0 && (c == 0 || c == cols-1)) || (r == rows-1 && (c == 0 || c == cols-1)) )
                            ? Brick.EXPLOSIVE : Brick.NORMAL;
                        bricks.add(new Brick(offsetX + c * brickW, offsetY + r * brickH, brickW - 2, brickH - 2, type));
                    }
                break;
            case 4:
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < cols; c++) {
                        int type = (r == 0) ? Brick.STRONG : Brick.NORMAL;
                        bricks.add(new Brick(offsetX + c * brickW, offsetY + r * brickH, brickW - 2, brickH - 2, type));
                    }
                break;
            case 5:
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < cols; c++) {
                        int type;
                        if (r == 0) type = Brick.STRONG;
                        else if (r == 2 && c % 2 == 0) type = Brick.UNBREAKABLE;
                        else if ((r == 1 || r == 3) && (c == 2 || c == 7)) type = Brick.EXPLOSIVE;
                        else type = Brick.NORMAL;
                        bricks.add(new Brick(offsetX + c * brickW, offsetY + r * brickH, brickW - 2, brickH - 2, type));
                    }
                break;
            default:
                createLevel(1);
                break;
        }
    }
}