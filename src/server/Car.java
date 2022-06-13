package server;

public class Car  {
    private int imageIdx = 0;


    private int xCoordinate, yCoordinate;
    private static int playerIdxStatic = 0;
    private int playerIdx;


    public static final int COLLISION_CIRCLE_RADIUS = 25;

    private float xCenterCoordinate = 0;
    private float yCenterCoordinate = 0;

    private float speed = 0;

    private int round = 0;
    private boolean hasBonus;
    Boolean leftCheckpoint, topCheckpoint, rightCheckpoint, bottomCheckpoint;

    public Car(int x, int y) {
        playerIdxStatic++;
        playerIdx = playerIdxStatic;
        xCoordinate = x;
        yCoordinate = y;
        leftCheckpoint = topCheckpoint = rightCheckpoint = bottomCheckpoint = false;
    }

    public void calculateCenterPoint() {
        xCenterCoordinate = (xCoordinate + COLLISION_CIRCLE_RADIUS);
        yCenterCoordinate = (yCoordinate + COLLISION_CIRCLE_RADIUS);
    }

    public void updateCheckpoints() {
        if (!topCheckpoint) {
            registerTopCheckpoint();
        }
        else if (!rightCheckpoint) {
            registerRightCheckpoint();
        }
        else if (!bottomCheckpoint) {
            registerBottomCheckpoint();
        }
        else if (!leftCheckpoint) {
            registerLeftCheckpoint();
        }
    }

    private void registerTopCheckpoint() {
        if (yCoordinate >= 25 && yCoordinate <= 169 && xCoordinate >= 440) {
            topCheckpoint = true;
        }
    }

    private void registerRightCheckpoint() {
        if (xCoordinate >= 630 && xCoordinate <= 780 && yCoordinate >= 365) {
            rightCheckpoint = true;
        }
    }

    private void registerLeftCheckpoint() {
        if (xCoordinate >= 124 && xCoordinate <= 263 && yCoordinate <= 310) {
            leftCheckpoint = true;
            completeRound();
        }
    }

    private void registerBottomCheckpoint() {
        if (yCoordinate >= 532 && yCoordinate <= 676 && xCoordinate <= 440) {
            bottomCheckpoint = true;
            System.out.println("Bottom checkpoint");
        }
    }

    private void completeRound() {
        round++;
        resetCheckpoints();
        System.out.println(round);
    }

    private void resetCheckpoints() {
        leftCheckpoint = topCheckpoint = bottomCheckpoint = rightCheckpoint = false;
    }

    public void setXCoordinate(int x) { xCoordinate = x; }
    public void setYCoordinate(int y) { yCoordinate = y; }
    public void setImageIdx(int idx) { imageIdx = idx; }
    public void setHasBonus(boolean hasBonus) { this.hasBonus = hasBonus; }

    public int getXCoordinate() { return xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }
    public int getImageIdx() { return imageIdx; }
    public double getXCenterPoint() { return xCenterCoordinate; }
    public double getYCenterPoint() { return yCenterCoordinate; }
    public int getRound() { return round; }
    public boolean getBonus() { return hasBonus; }
}
