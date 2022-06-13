package client;
import java.awt.*;

public class Car extends Thread {
    private Image currentImage;
    private int imageIdx = 0;

    private int xCoordinate, yCoordinate = -100;

    private int playerIdx;

    private Image[] carImages;

    private final float MAX_SPEED = 10;

    public static final int COLLISION_CIRCLE_RADIUS = 25;

    private float xCenterCoordinate = 0;
    private float yCenterCoordinate = 0;

    private float dx = 0;
    private float dy = 1;

    private float speed = 0;
    private int round = 0;

    private boolean bonus;

    public Car(int idx) {
        carImages = new Image[16];
        playerIdx = idx;
        AssetManager.LoadCarImages(carImages, playerIdx);
        currentImage = carImages[0];
    }

    public void processInput(int key) {
        if (key == 'd' || key == 'D' ) { // d or right arrow is pressed
            turnRight();
        } else if (key == 'a' || key == 'A') {  // a or left arrow is pressed
           turnLeft();
        } else if ((key == 'w' || key == 'W') && speed < MAX_SPEED) { // w or up arrow is pressed
            speed += 0.2;
        } else if ((key == 's' || key == 'S') && speed > -MAX_SPEED) { // s or down arrow is pressed
            speed -= 0.2;
        } else if (key == ' ' && bonus) {
            System.out.println("Bonus is used");
            bonus = false;
        }
        currentImage = carImages[imageIdx];
    }

    private void turnRight() {
        dx =  (imageIdx < 4 || imageIdx > 11) ? dx + 0.25f : dx - 0.25f;
        dy =  (imageIdx < 8) ? dy - 0.25f : dy + 0.25f;
        imageIdx = (imageIdx == carImages.length - 1) ? 0 : ++imageIdx;
        applyFriction(0.2f);
    }

    private void turnLeft() {
        dx = (imageIdx > 4 && imageIdx < 13) ? dx + 0.25f : dx - 0.25f;
        dy = (imageIdx > 8 || imageIdx == 0) ? dy - 0.25f : dy + 0.25f;
        imageIdx = (imageIdx == 0) ? carImages.length - 1 : --imageIdx;
        applyFriction(0.2f);
    }

    private boolean detectRacetrackCollision() {
        calculateCenterPoint();
        double xDiff = xCenterCoordinate - Game.RACETRACK_CENTER_X;
        double yDiff = yCenterCoordinate - Game.RACETRACK_CENTER_Y;
        double distance = Math.sqrt((Math.pow(xDiff, 2) + Math.pow(yDiff, 2)));
        boolean collided = distance <= COLLISION_CIRCLE_RADIUS + Game.RACETRACK_INNER_CIRCLE_RADIUS;
        if (!collided) {
            collided = distance >= (Game.RACETRACK_EXTERNAL_CIRCLE_RADIUS - COLLISION_CIRCLE_RADIUS) + 10;
        }
        return collided;
    }

    public void updateCarPosition() {
        int tempX = xCoordinate;
        int tempY = yCoordinate;

        xCoordinate +=  speed * dx;
        yCoordinate -=  speed * dy;

        if (detectRacetrackCollision()) {
            xCoordinate = tempX;
            yCoordinate = tempY;
            speed = 0;
        }
        applyFriction(0.01f);
    }

    private void applyFriction(float friction) {
        if (speed - friction > 0) speed -= friction;
    }

    public void calculateCenterPoint() {
        xCenterCoordinate = (xCoordinate + COLLISION_CIRCLE_RADIUS);
        yCenterCoordinate = (yCoordinate + COLLISION_CIRCLE_RADIUS);
    }

    public void setXCoordinate(int x) { xCoordinate = x; }
    public void setYCoordinate(int y) { yCoordinate = y; }
    public void setImage(int imageIdx) { currentImage = carImages[imageIdx]; }
    public void setRound(int round) { this.round = round; }
    public void setBonus() { bonus = true; }


    public int getXCoordinate() { return xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }
    public int getImageIdx() { return imageIdx; }
    public Image getCarImage() { return currentImage; }
    public int getRound() { return round; }
    public boolean getBonus() { return bonus; }
}
