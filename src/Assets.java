import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Assets {
    public static Image background;
    public static Image paddle;
    public static Image ball;
    public static Image brick;
    public static Image paddleNormal;
    public static Image paddleGlow;

    public static void load() {
        try {
            background = ImageIO.read(new File("res/bg_with_frame.png"));
            paddleNormal = ImageIO.read(new File("res/Player.png")); 
            paddleGlow = ImageIO.read(new File("res/Player_flash.png"));
            ball = ImageIO.read(new File("res/Ball_small-blue.png"));
            brick = ImageIO.read(new File("res/Brick3_4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
