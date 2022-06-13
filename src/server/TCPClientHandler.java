package server;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class TCPClientHandler implements Runnable {
    private Socket clientSocket = null;

    private BufferedReader inputStream;
    private DataOutputStream outputStream;

    private static LinkedList<Car> carList;
    private int clientIdx = 0;
    private int enemyClientIdx = 0;

    private String carCollision;

    private String message;
    private String[] instructions;
    private String[] blocks;

    private static Bonus bonus;

    private final int MAX_ROUNDS = 3;

    private static boolean isAlive;

    public TCPClientHandler(Socket server) {
        clientSocket = server;
        initInputStream();
        initOutputStream();
        addCarToTheList();
        clientIdx = carList.size();
        enemyClientIdx  = (clientIdx == 1)? 2 : 1;
        carCollision = "FALSE";
        isAlive = true;
    }

    private void initInputStream() {
        try {
            inputStream = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );
        } catch (IOException exception) {
            System.out.println("Failed to initialise TCPClient's input stream, exception: " + exception);
        }
    }

    private void initOutputStream() {
        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException exception) {
            System.out.println("Failed to initialise client's output stream, exception: " + exception);
        }
    }

    private void addCarToTheList() {
        int x, y;
        if (carList == null) {
            carList = new LinkedList<>();
            x = 130;
        } else {
            x = 200;
            System.out.println("Test " + bonus);
            bonus = new Bonus();
            System.out.println("Bonus initiated");
            System.out.println(bonus);
        }
        System.out.println(bonus);
        y = 380;
        carList.add(new Car(x ,y));
    }

    private void sendMessage(String message) {
        try {
            outputStream.writeBytes(message);
        } catch (IOException exception) {
            System.out.println("Failed to send message: " + message + ", exception: " + exception);
        }
    }

    private void readMessage() {
        try {
            message = inputStream.readLine();
        } catch (IOException exception) {
            System.out.println("Failed to read the message from client " + clientIdx + ", exception " + exception);
        }
    }

    public void run() {
        sendClientCarInitData();
        while (true) {
            readMessage();
            handleMessage();
            sendCarsData();
        }
    }

    private void handleMessage() {
        splitMessageToInstructions();
        for (String instruction : instructions) {
            splitInstructionToBlocks(instruction);
            switch (blocks[0]) {
                case "COORDINATES":
                    setCarCoordinates(blocks[1]);
                    checkCarsCollision();
                    checkBonusCollision();
                    carList.get(clientIdx - 1).updateCheckpoints();
                    break;
                case "IMAGE_IDX":
                    setCarImageIdx(blocks[1]);
                    break;
                default:
                    break;
            }
        }
    }

    private void splitMessageToInstructions() {
        instructions = message.split(",");
    }

    private void splitInstructionToBlocks(String instruction) {
        blocks = instruction.split(" ");
    }

    private void sendCarsData() {
        message = getClientCarRound() + getWinner() + getCollisionStatus() + getBonusCoordinates() +
                 getClientBonusStatus() + getEnemyCarData() + "\n";
        sendMessage(message);
    }

    private String getClientCarRound() {
        return  "CLIENT_ROUND " + carList.get(clientIdx - 1).getRound();
    }

    private synchronized String getWinner() {
        if (carList.size() > 1) {
            if (carList.get(clientIdx - 1).getRound() == MAX_ROUNDS) {
                isAlive = false;
                return ",WINNER CLIENT";
            } else if (carList.get(enemyClientIdx - 1).getRound() == MAX_ROUNDS) {
                isAlive = false;
                return ",WINNER ENEMY";
            }
        }
        return "";
    }

    private String getCollisionStatus() {
        return ",ENEMY_COLLISION " + carCollision;
    }

    private synchronized String getBonusCoordinates() {
        if (
                bonus != null && !carList.get(enemyClientIdx - 1).getBonus() &&
                !carList.get(clientIdx - 1).getBonus()
        ) {
            return bonus.getCoordinates();
        }
        return  "";
    }

    private String getClientBonusStatus() { return (carList.get(clientIdx - 1).getBonus())? ",CLIENT_BONUS" : ""; }

    private String getEnemyCarData() {
        if (carList.size() > 1) {
            return getEnemyCoordinates() + getEnemyImageIdx() + getEnemyRound() + getEnemyBonusStatus();
        }
        return "";
    }

    private String getEnemyCoordinates() {
        return ",ENEMY_COORDINATES " + carList.get(enemyClientIdx - 1).getXCoordinate() + "_" +
                carList.get(enemyClientIdx - 1).getYCoordinate();
    }

    private String getEnemyImageIdx() {
        return ",ENEMY_IMAGE_IDX " + carList.get(enemyClientIdx - 1).getImageIdx();
    }

    private String getEnemyRound() {
        return  ",ENEMY_ROUND " + carList.get(enemyClientIdx - 1).getRound();
    }

    private String getEnemyBonusStatus() { return (carList.get(enemyClientIdx - 1).getBonus())? ",ENEMY_BONUS" : ""; }

    private void sendClientCarInitData() {
        sendMessage("CAR_IDX " + clientIdx + ",CLIENT_COORDINATES " + carList.get(clientIdx - 1).getXCoordinate() +
                "_" + carList.get(clientIdx - 1).getYCoordinate() + "\n"
        );
    }

    private void setCarImageIdx(String idx) {
        carList.get(clientIdx - 1).setImageIdx(Integer.parseInt(idx));
    }

    private synchronized void setCarCoordinates(String coordinates) {
        String[] coordinatesArr = coordinates.split("_");
        carList.get(clientIdx - 1).setXCoordinate(Integer.parseInt(coordinatesArr[0]));
        carList.get(clientIdx - 1).setYCoordinate(Integer.parseInt(coordinatesArr[1]));
    }

    private synchronized void checkCarsCollision() {
        if (carList.size() > 1) {
            carList.get(0).calculateCenterPoint();
            carList.get(1).calculateCenterPoint();
            double xDiff = carList.get(0).getXCenterPoint() - carList.get(1).getXCenterPoint();
            double yDiff = carList.get(0).getYCenterPoint() - carList.get(1).getYCenterPoint();
            double distance = Math.sqrt((Math.pow(xDiff, 2) + Math.pow(yDiff, 2)));
            if (distance <= 2 * client.Car.COLLISION_CIRCLE_RADIUS - 10) {
                carCollision = "TRUE";
                isAlive = false;
            }
        }
    }

    private synchronized void checkBonusCollision() {
        if (bonus != null && bonus.getGeneratedFlag()) {
            double xDiff = carList.get(clientIdx - 1).getXCenterPoint() - bonus.getX();
            double yDiff = carList.get(clientIdx - 1).getYCenterPoint() - bonus.getY();
            double distance = Math.sqrt((Math.pow(xDiff, 2) + Math.pow(yDiff, 2)));
            if (distance <= 2 * client.Car.COLLISION_CIRCLE_RADIUS - 10) {
                carList.get(clientIdx - 1).setHasBonus(true);
                bonus.setPicked(true);
            }
        }
    }

    public boolean isAlive() { return isAlive; }
}
