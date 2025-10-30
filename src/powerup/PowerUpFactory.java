package powerup;

import java.util.Random;

/**
 * Factory for creating random power-ups with appropriate drop probabilities.
 */
public final class PowerUpFactory {
    private static final Random RANDOM = new Random();

    private PowerUpFactory() {}

    /**
     * Creates a random power-up at the given position.
     * @param x X-coordinate of power-up
     * @param y Y-coordinate of power-up
     * @return PowerUp instance
     */
    public static PowerUp createRandom(int x, int y) {
        int roll = RANDOM.nextInt(100);
        if (roll < 24) {
            return new WidePaddlePowerUp(x, y);
        }
        if (roll < 49) {
            return new ExtraLifePowerUp(x, y);
        }
        if (roll < 74) {
            return new LifeDownPowerUp(x, y);
        }
        return new LaserPaddlePowerUp(x, y);
    }
}