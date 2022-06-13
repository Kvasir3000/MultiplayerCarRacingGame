package server;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Bonus {
    private Timer bonusTimer;
    private int tickTime;
    private int x, y;
    private final int RADIUS = 20;

    private final int INNER_CIRCLE_RADIUS = 190;
    private final int OUTER_CIRCLE_RADIUS = 300;

    private boolean picked = false;
    private boolean generated = false;

    private final int MIN_TIME = 10000;
    private final int MAX_TIME = 15000;



    public Bonus() {
        tickTime = (int)generateRandomNumber(MIN_TIME, MAX_TIME);
        bonusTimer = new Timer(
                tickTime,
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateBonusTimer();
                    }
                }
        );
        bonusTimer.start();
    }

    private void updateBonusTimer() {
        if (!picked) {
            generateBonus();
            picked = false;
            tickTime = (int) generateRandomNumber(MIN_TIME, MAX_TIME);
            bonusTimer.setDelay(tickTime);
        }
    }

    private void generateBonus() {
        // Generate bonus inside the track area by using polar coordinates
        // Rho is random number that lies between the values of radius of inner and outer circles
        int rho = (int)generateRandomNumber(INNER_CIRCLE_RADIUS, OUTER_CIRCLE_RADIUS);
        double theta = generateRandomNumber(0, 360);

        x = (int)(rho * Math.cos(theta * Math.PI / 180));
        y = (int)(rho * Math.sin(theta * Math.PI / 180));

        //Translate coordinates to the screen space
        x += 450;
        y += 350;
        generated = true;

    }

    private double generateRandomNumber(int min, int max) {
        return Math.floor(Math.random() * (max - min + 1) + min);
    }

    public String getCoordinates() {
        return ",BONUS " + x + "_" + y;
    }

    public void setPicked(boolean flag) {
        picked = flag;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean getGeneratedFlag()  { return generated; }
    public boolean getPicked() { return picked; }
}
