package client;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

abstract public class AssetManager {
    private static final String roadImagePath = "src/client/roadImage/road2.png";
    private static String carImagesPath = "src/client/carImages/Player";
    private static String explosionsPath = "src/client/explosions/explosion";
    private static String bonusPath  = "src/client/bonusImages/bonus";

    static void LoadCarImages(Image[] carImages, int playerIdx) {
        String path = carImagesPath + ((playerIdx == 1)? "1" : "2") + "/car";
        String imageIdxStr;
        for (int imageIdx = 1; imageIdx <= carImages.length; imageIdx++){
            imageIdxStr = Integer.toString(imageIdx)  + ".png";
            try {
                carImages[imageIdx - 1] = ImageIO.read(new File(path + imageIdxStr));
            } catch (IOException exception) {
                System.out.println("Failed to load car image " + imageIdxStr);
            }
        }
    }

    static Image LoadRoadImage() {
        Image roadImage = null;
        try {
            roadImage = ImageIO.read(new File(roadImagePath));
        } catch (Exception e) {
            System.out.println("Failed to load road image");
        }
        return roadImage;
    }

    static void LoadExplosionSprites(Image[] explosions) {
        String path;
        for (int spriteIdx = 1; spriteIdx < 33; spriteIdx++) {
            path = explosionsPath + Integer.toString(spriteIdx) + ".png";
            try {
                explosions[spriteIdx - 1] = ImageIO.read(new File(path));
            } catch (IOException exception) {
                System.out.println("Failed to load explosion sprite " + spriteIdx);
            }
        }
    }

    static void LoadBonusImages(Image[] bonus) {
        String path;
        for (int imageIdx = 1; imageIdx < 6; imageIdx++) {
            path = bonusPath + imageIdx + ".png";
            try {
                bonus[imageIdx - 1] = ImageIO.read(new File(path)).getScaledInstance(
                        20,
                        20,
                        Image.SCALE_SMOOTH
                );
            } catch (IOException exception) {
                System.out.println("Failed to load bonus image " + path);
            }
        }
    }
}
