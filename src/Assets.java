import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Assets {
    public static Image background;
    public static Image paddle;
    public static Image ball;
    public static Image brick;

    // gọi hàm này 1 lần khi khởi động game
    public static void load() {
        try {
            background = ImageIO.read(new File("res/Background1.png"));
            paddle     = ImageIO.read(new File("res/Player.png")); //paddle
            ball       = ImageIO.read(new File("res/Ball_small-blue.png"));
            brick      = ImageIO.read(new File("res/Brick3_4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
