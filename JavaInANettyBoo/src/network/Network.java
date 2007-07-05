package network;

import animation.Ball;
import animation.GameScreen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"InfiniteLoopStatement", "UnusedDeclaration"})

public class Network {
    private ScreenConnection leftScreen;
    private ScreenConnection rightScreen;
    private List<String> ipList = new ArrayList<String>();
    private GameScreen gameScreen;

    public Network(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.leftScreen = new ScreenConnection(this, gameScreen);
        this.rightScreen = new ScreenConnection(this, gameScreen);
        this.startServerThread();
    }

    /* -----make a client connection to a server */
    public void connectToServer(String side, String ipAddress) {
        if(ipAddress != null) {
            ScreenConnection remoteScreen = new ScreenConnection(this, gameScreen);
            remoteScreen.attemptServerConnection(side, ipAddress);
            if(remoteScreen.isLeft()) {
                this.leftScreen = remoteScreen;
            } else {
                this.rightScreen = remoteScreen;
            }
        }
    }

    /* -----start listenForClients listening */
    void startServerThread() {
        new Thread(new Runnable() {
            Socket client;
            ServerSocket serverListeningSocket;

            public void run() {
                try {
                    this.serverListeningSocket = new ServerSocket(2000);
                    System.out.println("Server listening...");
                    while(true) {
                        Socket clientSocket = this.serverListeningSocket.accept();
                        ScreenConnection remoteScreen = new ScreenConnection(Network.this, gameScreen);
                        remoteScreen.attemptClientConnection(clientSocket);
                        if(remoteScreen.isLeft()) {
                            leftScreen.disconnect();
                            leftScreen = remoteScreen;
                        } else {
                            rightScreen.disconnect();
                            rightScreen = remoteScreen;
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }).start();
    }

    public boolean isLeftConnected() {
        return this.leftScreen.isConnected();
    }

    public boolean isRightConnected() {
        return this.rightScreen.isConnected();
    }

    public void sendToLeftScreen(Ball ball) {
        this.leftScreen.sendScreenObject(ball);
    }

    public void sendToRightScreen(Ball ball) {
        this.rightScreen.sendScreenObject(ball);
    }

}