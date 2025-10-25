package powerup;

import java.util.Random;

/**
 * Factory for creating random power-ups with appropriate drop probabilities.
 */
public final class PowerUpFactory {
    private static final Random RANDOM = new Random();
    private static final int WIDE_PADDLE_CHANCE = 60;
    private static final int EXTRA_LIFE_CHANCE = 80;
    private static final int LIFE_DOWN_CHANCE = 90;
    private static final int LASER_CHANCE = 85;  // 5% chance

    private PowerUpFactory() {}

    /**
     * Creates a random power-up at the given position.
     * @param x X-coordinate of power-up
     * @param y Y-coordinate of power-up
     * @return PowerUp instance or null (20% chance)
     */
    public static PowerUp createRandom(int x, int y) {
        int roll = RANDOM.nextInt(100);
        if (roll < WIDE_PADDLE_CHANCE) {
            return new WidePaddlePowerUp(x, y);
        }
        if (roll < EXTRA_LIFE_CHANCE) {
            return new ExtraLifePowerUp(x, y);
        }
        if (roll < LIFE_DOWN_CHANCE) {
            return new LifeDownPowerUp(x, y);
        }
        if (roll < LASER_CHANCE) {
            return new LaserPaddlePowerUp(x, y);
        }
        return null;
    }
}