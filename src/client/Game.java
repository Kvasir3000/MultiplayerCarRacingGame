package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class Game extends JPanel {
    private Image roadImage;
    private Image[] explosions = new Image[32];

    private boolean gameIsRunning = true;

    Timer updateScreenThread;

    int explosionSpriteIdx = 0;

    public static final int  RACETRACK_INNER_CIRCLE_RADIUS = 172;
    public static final int  RACETRACK_EXTERNAL_CIRCLE_RADIUS = 325;
    public static final int  RACETRACK_CENTER_X = 450;
    public static final int  RACETRACK_CENTER_Y = 350;

    private final int FINAL_ROUND = 3;

    LinkedList<Integer> inputKeys;
    private LinkedList<Car> carList;

    private Bonus bonus;
    private boolean bonusExists = false;

    private int clientIdx;

    Game() {
        inputKeys = new LinkedList<>();
        roadImage = AssetManager.LoadRoadImage();
        setSize(roadImage.getWidth(null),roadImage.getHeight(null));
        AssetManager.LoadExplosionSprites(explosions);
        setVisible(true);
        bonus = new Bonus();
    }

    public void initClientCar(int idx) {
        carList = new LinkedList<>();
        carList.add(new Car(idx));
        this.clientIdx = idx;
        initEnemyCar();
    }

    private void initEnemyCar() {
        int enemyIdx = (clientIdx == 1)? 2 : 1;
        carList.add(new Car(enemyIdx));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(roadImage, 0, 0, null);
        if (gameIsRunning) {
            drawCars(g);
            drawPlayersInfo(g);
            drawBonus(g);
        }
        else if (explosionSpriteIdx < 32) {
            drawExplosion(g);
        } else {
            endGameCarCrashed();
        }
    }

    public void processInput() {
        for (Integer key : inputKeys) {
            carList.get(0).processInput(key);
        }
    }

    private void drawCars(Graphics g) {
        for (Car car : carList) {
            g.drawImage(
                    car.getCarImage(),
                    car.getXCoordinate(),
                    car.getYCoordinate(),
                    null
            );
        }
        if (carList.size() > 0)
            carList.get(0).updateCarPosition();
    }

    private void drawBonus(Graphics g) {
        if (bonus != null && bonusExists) {
            g.drawImage(bonus.getImage(), bonus.getX() - 20, bonus.getY() - 20, null);
            bonus.updateImgIdx();
        }
    }


    private void drawExplosion(Graphics g) {
        for (Car car : carList){
            g.drawImage(
                    explosions[explosionSpriteIdx],
                    car.getXCoordinate() - 30,
                    car.getYCoordinate() - 30,
                    null
            );
        }
        try {
            Thread.sleep(25);
        } catch (Exception e) {
            System.out.println(e);
        }
        explosionSpriteIdx++;
    }

    public void addKeyToList(Integer key) {
        if (!inputKeys.contains(key)) {
            inputKeys.add(key);
        }
    }

    public void deleteKeyFromList(Integer key) {
        inputKeys.remove(key);
    }

    public void startScreenThread() {
        updateScreenThread = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop();
            }
        });
        updateScreenThread.start();
    }

    private void gameLoop() {
        if (carList.get(1).getXCoordinate() == 0){
            showWaitMessage();
        }
        else {
            processInput();
            repaint();
        }
    }

    private void showWaitMessage() {
        Graphics g = getGraphics();
        while (carList.get(1).getXCoordinate() == 0) {
            g.setFont(new Font("Serif", Font.BOLD, 24));
            g.drawString("Please wait for another player", 300, 350);
        }
    }

    private void drawPlayersInfo(Graphics g) {
        if (carList.size() > 1) {
            drawInfoUI(g, 20, 1);
            drawInfoUI(g, 80, 0);
        }
    }

    private void drawInfoUI(Graphics g, int y, int idx) {
        g.setColor(new Color(255, 0, 0, 128));
        g.fillRect(10, y, 100,  40);
        g.setColor(Color.BLACK);
        g.drawRect(10,y, 100,  40);
        g.setFont(new Font("Serif", Font.BOLD, 12));
        g.drawString((idx == 0)? "Green car" : "Blue car", 40,y + 15);
        g.drawString("Round " + carList.get(idx).getRound() + " \\ " + FINAL_ROUND, 15, y + 35);
        if (carList.get(0).getBonus()){
            g.drawString("Bonus", 80, y + 15);
        }
    }

    private void endGameCarCrashed() {
        updateScreenThread.stop();
        JOptionPane.showMessageDialog(this, "Cars crashed, game over");
        System.exit(0);
    }

    public void endGameShowWinner(String winner) {
        String message = (winner.equals("ENEMY"))? "Loooooser" : "You won";
        updateScreenThread.stop();
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    public void setClientCarPosition(int x, int y) {
        carList.get(0).setXCoordinate(x);
        carList.get(0).setYCoordinate(y);
    }

    public void setEnemyCarPosition(int x, int y) {
        carList.get(1).setXCoordinate(x);
        carList.get(1).setYCoordinate(y);
    }

    public void setEnemyCarImageIdx(int imageIdx) {
        carList.get(1).setImage(imageIdx);
    }

    public void setEnemyCollision(String collision) {
        gameIsRunning = (collision.equals("TRUE"))? false : true;
    }

    public void setClientRound(String round) { carList.get(0).setRound(Integer.parseInt(round)); }

    public void setEnemyRound(String round) {
        carList.get(1).setRound(Integer.parseInt(round));
    }

    public void setBonusX(String x) {
        bonusExists = true;
        bonus.setX(Integer.parseInt(x));
    }
    public void setBonusY(String y) {
        bonus.setY(Integer.parseInt(y));
    }

    public void setClientBonus() {
        carList.get(0).setBonus();
        bonusExists = false;
    }

    public int[] getClientCarPosition() {
        int[] coordinates =  {
                carList.get(0).getXCoordinate(),
                carList.get(0).getYCoordinate()
        };
        return coordinates;
    }

    public int getClientCarImageIdx() {
        return carList.get(0).getImageIdx();
    }

    public void resetBonus() { bonusExists = false; }
}