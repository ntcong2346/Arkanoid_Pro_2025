package powerup;

import entity.Paddle;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Power-up that enables paddle to automatically shoot dual lasers from left and right edges.
 */
public class LaserPaddlePowerUp extends PowerUp {
    /** Duration of laser power-up in milliseconds. */
    private static final int DURATION_MS = 4000;

    /** Type identifier for this power-up. */
    private static final String TYPE = "laser";

    /** Timer for continuous shooting. */
    private Timer shootTimer;

    public LaserPaddlePowerUp(int x, int y) {
        super(x, y, 25, 25, TYPE, DURATION_MS);
        this.shootTimer = null;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        if (!paddle.isLaserActive()) {
            paddle.setLaserActive(true);
            startContinuousShooting(paddle);  // Bắt đầu bắn liên tục
        }
        resetTimer(paddle);  // Reset timer hiệu lực
    }

    @Override
    public void removeEffect(Paddle paddle) {
        paddle.setLaserActive(false);
        stopShooting();  // Dừng bắn khi hết hiệu lực
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawRect((int) x - 1, (int) y - 1, width + 1, height + 1);
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y, width, height);
        g.setColor(Color.YELLOW);
        int symbolWidth = 6;  // Ước lượng chiều rộng "⚡"
        int centerX = (int) x + (width - symbolWidth) / 2;  // Căn giữa
        g.drawString("⚡", centerX, (int) y + 16);
    }

    private void startContinuousShooting(Paddle paddle) {
        if (shootTimer != null) {
            shootTimer.cancel();  // Hủy timer cũ
        }
        shootTimer = new Timer();
        shootTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (paddle.isLaserActive()) {
                    PowerUpManager.shootDualLasers(paddle);  // Gọi qua PowerUpManager
                }
            }
        }, 0, 1000);  // Bắn mỗi 0.5s
    }

    private void resetTimer(Paddle paddle) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (paddle.isLaserActive()) {
                    removeEffect(paddle);
                }
            }
        }, DURATION_MS);
    }

    private void stopShooting() {
        if (shootTimer != null) {
            shootTimer.cancel();
            shootTimer = null;
        }
    }
}