package client;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class GameWindow extends JFrame implements KeyListener {
    private Game game;
    private TCPClient client;
    public GameWindow()
    {
        super("Racing game");
        game = new Game();
        setSize(910, 730);
        setLayout(null);
        add(game);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setVisible(true);
        setResizable(false);
        client = new TCPClient("localhost", 5000,  game);
        Thread clientThread = new Thread(client);
        clientThread.start();
    }

    public void keyPressed(KeyEvent e) {
        game.addKeyToList(e.getKeyCode());
    }
    public void keyReleased(KeyEvent e) {
        game.deleteKeyFromList(e.getKeyCode());
    };
    public void keyTyped(KeyEvent e) {};
}