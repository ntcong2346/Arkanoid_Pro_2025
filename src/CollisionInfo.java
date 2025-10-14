import java.awt.*;
import java.util.ArrayList;

public class CollisionInfo {
    private Ball ball;
    private Paddle paddle;
    private ArrayList<Brick> bricks;

    public CollisionInfo(Ball ball, Paddle paddle, ArrayList<Brick> bricks) {
        this.ball = ball;
        this.paddle = paddle;
        this.bricks = bricks;
    }

    public int updateCollision() {
        handleWallCollision();
        handlePaddleCollision();
        return handleBrickCollision();
    }

    public int updateCollisionCoop(Paddle paddle1, Paddle paddle2) {
        handleWallCollision();
        handlePaddleCollisionCoop(paddle1, paddle2);
        return handleBrickCollision();
    }

    /**
     * Ball & wall collision
     */
    private void handleWallCollision() {
        if (ball.getX() <= 0 || ball.getX() + ball.getRadius() >= GamePanel.WIDTH)
            ball.bounceHorizontal();
        if (ball.getY() - ball.getRadius() <= 0)
            ball.bounceVertical();
    }

    /**
     * Ball & paddle collision
     */
    private void handlePaddleCollision() {
        if (ball.getRect().intersects(paddle.getRect())) {
            ball.bounceVertical();
            paddle.setGlow(); // Kích hoạt hiệu ứng phát sáng
        }
    }

    /**
     * Ball & paddle collision for coop mode w/ 2 paddles
     */
    private void handlePaddleCollisionCoop(Paddle paddle1, Paddle paddle2) {
        boolean collided = false;
        if (ball.getRect().intersects(paddle1.getRect())) {
            ball.bounceVertical();
            paddle1.setGlow();
            collided = true;
        }
        if (!collided && ball.getRect().intersects(paddle2.getRect())) {
            ball.bounceVertical();
            paddle2.setGlow();
            collided = true;
        }
    }

    /**
     * Ball & brick collision
     */
    private int handleBrickCollision() {
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
                return b.hit(bricks);
            }
        }
        return 0;
    }
}
