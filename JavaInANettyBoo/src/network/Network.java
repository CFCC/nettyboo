package network;

import animation.Ball;
import animation.GameScreen;
import animation.ScreenObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

@SuppressWarnings({"InfiniteLoopStatement", "UnusedDeclaration"})

public class Network {
    private static final String REQUEST_CONNECTION = "Requesting connection on ";
    private static final String LEFT_SCREEN = "left";
    private static final String RIGHT_SCREEN = "right";
    private static final String ACCEPTED_CONNECTION = "Accepted";
    private static final String REFUSED_CONNECTION = "Refused";

    ScreenConnection leftScreen;
    ScreenConnection rightScreen;
    GameScreen gameScreen;

    public Network(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.startServerThread();
        this.connectToServer();
    }

    /* -----prompt for user input of IP addresses and connect to listenForClients */
    void connectToServer() {
        this.leftScreen = new ScreenConnection();
        this.rightScreen = new ScreenConnection();
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Left computer IP address?");
        String ipAddress = keyboard.next();
        leftScreen.attemptServerConnection(LEFT_SCREEN, ipAddress);
        System.out.println("Right computer IP address?");
        ipAddress = keyboard.next();
        rightScreen.attemptServerConnection(RIGHT_SCREEN, ipAddress);
        if (!leftScreen.isConnected()) {
            System.out.println("No left screen.");
        }
        if (!rightScreen.isConnected()) {
            System.out.println("No right screen.");
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
                    System.out.println("Listening...");
                    while(true) {
                        Socket clientSocket = this.serverListeningSocket.accept();
                        ScreenConnection remoteScreen = new ScreenConnection();
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

    /* -----class to represent each other screen */
    class ScreenConnection extends Thread {
        private Socket socket;
        private BufferedReader fromScreen;
        private PrintStream toScreen;
        private ObjectOutputStream ballOutputStream;
        private ObjectInputStream ballInputStream;
        private boolean connected;
        private boolean left;
        private boolean killThread;

        public boolean isConnected() {
            return connected;
        }

        public boolean isLeft() {
            return left;
        }

        /* attempt to connect to another program's client */
        boolean attemptClientConnection(Socket socket) {
            try {
                this.socket = socket;
                this.fromScreen = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                this.toScreen = new PrintStream(this.socket.getOutputStream());
                this.ballOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                this.ballInputStream = new ObjectInputStream(this.socket.getInputStream());
                String clientString = fromScreen.readLine();
                if (clientString.startsWith(REQUEST_CONNECTION)) {
                    System.out.println("Accepted connection to " + socket.getInetAddress().toString());
                    toScreen.println(ACCEPTED_CONNECTION);
                    this.connected = true;
                    this.start();
                    return true;
                }
            } catch (IOException e) {
                System.err.println(e);
            }
            return true;
        }

        /* attempt to connect to another program's listenForClients */
        boolean attemptServerConnection(String side, String ipAddress) {
            if (!ipAddress.equalsIgnoreCase("none")) {
                try {
                    this.socket = new Socket(ipAddress, 2000);
                    this.fromScreen = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                    this.toScreen = new PrintStream(this.socket.getOutputStream());
                    this.toScreen.println(REQUEST_CONNECTION + side);
                    if (this.fromScreen.readLine().equals(REFUSED_CONNECTION)) {
                        System.out.println("Connection to server refused.");
                        this.socket.close();
                        this.connected = false;
                        return false;
                    } else {
                        this.connected = true;
                        System.out.println("Connection to server accepted.");
                        System.out.println(ipAddress + " Connected as " + side + " screen.");
                        this.start();
                        return true;
                    }
                } catch (IOException e) {
                    System.err.println(e);
                    this.connected = false;
                    return false;
                }
            } else {
                return false;
            }
        }

        /* close a connection to the screen */
        void disconnect() {
            if(this.connected) {
                try {
                    this.socket.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
                this.killThread = true;
            }
        }

        public void sendBall(ScreenObject screenObject) {
            try {
                ballOutputStream.writeObject(screenObject);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        public void run() {
            while (true) {
                try {
                    /* check for balls being sent */
                    Ball recievedBall = (Ball) ballInputStream.readObject();
                    gameScreen.addBall(recievedBall);
                } catch (IOException e) {
                    this.disconnect();
                    System.err.println(e);
                } catch (ClassNotFoundException e) {
                    this.disconnect();
                    System.err.println(e);
                }
                if(this.killThread) {
                    return;
                }
            }
        }
    }
}