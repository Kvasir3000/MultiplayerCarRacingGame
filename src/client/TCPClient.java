package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient implements Runnable {

    private Socket clientSocket = null;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;

    private String message;
    private String[] instructions;
    private String[] blocks;

    private Game game;

    public TCPClient(String host, int port, Game game) {
        this.game = game;
        initSocket(host, port);
        initInputStream();
        initOutputStream();
    }

    private void initSocket(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
            System.out.println("Connected");
        } catch (UnknownHostException exception){
            System.out.println("Failed to initialise client, exception: " + exception);
        } catch (IOException exception) {
            System.out.println("Failed to initialise client, exception: " + exception);
        }
    }

    private void initInputStream() {
        try {
            inputStream = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );
        } catch (IOException exception) {
            System.out.println("Failed to initialise client's input stream, exception: " + exception);
        }
    }

    private void initOutputStream() {
        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException exception) {
            System.out.println("Failed to initialise client's output stream, exception: " + exception);
        }
    }

    private void getMessage() {
         try {
             message = inputStream.readLine();
         } catch (IOException exception) {
             System.out.println("Failed to read the message: " + message + ", exception " + exception);
         }
    }

    private void sendMessage(String message) {
        try {
            outputStream.writeBytes(message);
        } catch (IOException exception) {
            System.out.println("Failed to send message: " + message + ", exception " + exception);
        }
    };

    public void run() {
        getMessage();
        handleMessage();
        while(true) {
            sendClientData();
            getMessage();
            handleMessage();
        }
    }

    public void handleMessage() {
        splitMessageToInstructions();
        for (String instruction : instructions) {
            System.out.println(message);
            splitInstructionToBlocks(instruction);
            switch (blocks[0]) {
                case "CLIENT_ROUND":
                    setClientRound();
                    break;
                case "CLIENT_COORDINATES":
                    setClientCoordinates();
                    break;
                case "CAR_IDX":
                    setCarIdx();
                    break;
                case "WINNER":
                    setWinner();
                    break;
                case "BONUS":
                    setBonusCoordinates();
                    break;
                case "CLIENT_BONUS":
                    setClientBonus();
                    break;
                case "ENEMY_ROUND":
                    setEnemyRound();
                    break;
                case "ENEMY_COORDINATES":
                    setEnemyCoordinates();
                    break;
                case "ENEMY_IMAGE_IDX":
                    setEnemyImageIdx();
                    break;
                case "ENEMY_COLLISION":
                    setEnemyCollision();
                    break;
                case "ENEMY_BONUS":
                    resetBonus();
                default:
                    break;
            }
        }
    }

    private void setCarIdx() {
        game.initClientCar(Integer.parseInt(blocks[1]));
    }

    private void splitMessageToInstructions() {
        instructions = message.split(",");
    }

    private void splitInstructionToBlocks(String instruction) {
        blocks = instruction.split(" ");
    }

    private void setClientCoordinates() {
        blocks = blocks[1].split("_");
        game.setClientCarPosition(
                Integer.parseInt(blocks[0]),
                Integer.parseInt(blocks[1])
        );
        game.startScreenThread();
    }

    private void setBonusCoordinates() {
        blocks = blocks[1].split("_");
        game.setBonusX(blocks[0]);
        game.setBonusY(blocks[1]);
    }

    private synchronized void setEnemyCoordinates() {
        blocks = blocks[1].split("_");
        game.setEnemyCarPosition(
                Integer.parseInt(blocks[0]),
                Integer.parseInt(blocks[1])
        );
    }

    private void setEnemyImageIdx() {
        game.setEnemyCarImageIdx(Integer.parseInt(blocks[1]));
    }

    private void setEnemyCollision() {
        game.setEnemyCollision(blocks[1]);
    }

    private void sendClientData() {
      message = getClientCoordinates() + getClientImageIdx();
      sendMessage(message);
    }

    private String getClientCoordinates() {
        int[] coordinates = game.getClientCarPosition();
        return "COORDINATES " + coordinates[0] + "_" + coordinates[1];
    }

    private String getClientImageIdx() {
        int imageIdx = game.getClientCarImageIdx();
        return ",IMAGE_IDX " + imageIdx + "\n";
    }

    private void setWinner() {
        game.endGameShowWinner(blocks[1]);
    }

    private void setClientRound() {
        game.setClientRound(blocks[1]);
    }

    private void setEnemyRound() {
        game.setEnemyRound(blocks[1]);
    }

    private void setClientBonus() { game.setClientBonus(); }

    private void resetBonus() { game.resetBonus(); }
}
