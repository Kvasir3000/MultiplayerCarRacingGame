package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class TCPServer {
    private final int MAX_CLIENTS = 2;
    private int activeClients = 0;

    private ServerSocket service = null;
    private Socket server = null;
    TCPClientHandler[] clientHandlers;
    LinkedList<Thread> clientsThreads;

    public TCPServer() {
        clientHandlers = new TCPClientHandler[2];
        clientsThreads = new LinkedList<>();
        initServiceSocket();
        initServerSockets();
    }

    private void initServiceSocket() {
        try {
            service = new ServerSocket(5000);
        } catch (IOException exception) {
            System.out.println("Failed to initialised service socket " + exception);
        }
    }

    private void initServerSockets() {
        try {
            do {
                server = service.accept();
                clientHandlers[activeClients] = new TCPClientHandler(server);
                Thread t = new Thread(clientHandlers[activeClients]);
                t.start();
                clientsThreads.add(t);
                activeClients++;
            } while (activeClients < MAX_CLIENTS);
        } catch (IOException exception) {
            System.out.println("Failed to initialise server socket " + exception);
        }
    }

    public void run() {
        boolean run = true;
        while (run) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                System.out.println(exception);
            }
            run = checkActiveClients();

        }
    }

    private boolean checkActiveClients() {
        boolean active = true;
        for (TCPClientHandler client : clientHandlers) {
            if (!client.isAlive()) {
                active = false;
                break;
            }
        }
        return active;
    }

    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.run();
    }
}
