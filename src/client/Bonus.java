package client;

import java.awt.Image;

public class Bonus {
    private int x;
    private int y;

    private int currentImageIdx = 0;
    private int temp = 0;
    private Image[] bonusImages = new Image[6];

    public Bonus() {
        AssetManager.LoadBonusImages(bonusImages);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Image getImage() {
        return bonusImages[currentImageIdx];
    }

    public void updateImgIdx() {
        temp++;
        if (temp == 2) {
            currentImageIdx = (currentImageIdx == 5) ? 0 : currentImageIdx + 1;
            temp = 0;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
