package powerup;

import entity.Paddle;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Power-up that reduces player lives when collected.
 */
public class LifeDownPowerUp extends PowerUp {
    private static final int DURATION_MS = 0;  // Instant effect
    private static final String TYPE = "life_down";

    public LifeDownPowerUp(int x, int y) {
        super(x, y, 25, 25, TYPE, DURATION_MS);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        // Không dùng paddle mà dùng PowerUpManager để giảm lives
        PowerUpManager.addLife(-1);  // Giảm 1 mạng
    }

    @Override
    public void removeEffect(Paddle paddle) {
        // Không cần remove vì hiệu ứng tức thời
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.DARK_GRAY);  // Màu xám đen để biểu thị tiêu cực
        g.fillRect((int) x, (int) y, width, height);
        g.setColor(Color.RED);  // Chữ đỏ để cảnh báo
        g.drawString("✖", (int) x + 8, (int) y + 16);  // Ký hiệu "X" cho giảm mạng
    }
}