package network;

import animation.Ball;
import animation.GameScreen;
import animation.ScreenObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings({"InfiniteLoopStatement", "UnusedDeclaration"})

public class Network {
    private static final String REQUEST_CONNECTION = "Requesting connection on ";
    private static final String LEFT_SCREEN = "left";
    private static final String RIGHT_SCREEN = "right";
    private static final String ACCEPTED_CONNECTION = "Accepted";
    private static final String REFUSED_CONNECTION = "Refused";

    private ScreenConnection leftScreen;
    private ScreenConnection rightScreen;
    private GameScreen gameScreen;

    public Network(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.leftScreen = new ScreenConnection();
        this.rightScreen = new ScreenConnection();
        this.startServerThread();
    }

    /* -----make a client connection to a server */
    public void connectToServer(String side, String ipAddress) {
        if(ipAddress != null) {
            ScreenConnection remoteScreen = new ScreenConnection();
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

    /* -----class to represent each other screen */
    class ScreenConnection extends Thread {
        private Socket socket;
        private ObjectOutputStream serialOutputStream;
        private ObjectInputStream serialInputStream;
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
                this.serialOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                this.serialInputStream = new ObjectInputStream(this.socket.getInputStream());
                String clientString = (String)this.serialInputStream.readObject();
                if (clientString.startsWith(REQUEST_CONNECTION)) {
                    System.out.println("Accepted connection to " + socket.getInetAddress().toString());
                    this.serialOutputStream.writeObject(ACCEPTED_CONNECTION);
                    this.connected = true;
                    this.start();
                    return true;
                }
            } catch (IOException e) {
                System.err.println(e);
            } catch (ClassNotFoundException e) {
                System.err.println(e);
            }
            return true;
        }

        /* attempt to connect to another program's listenForClients */
        boolean attemptServerConnection(String side, String ipAddress) {
            try {
                if(side.equals("left")) {
                    this.left = true;
                }
                this.socket = new Socket(ipAddress, 2000);
                this.serialOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                this.serialInputStream = new ObjectInputStream(this.socket.getInputStream());

                this.serialOutputStream.writeObject(REQUEST_CONNECTION + side);

                if (REFUSED_CONNECTION.equals(this.serialInputStream.readObject())) {
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
            } catch (ClassNotFoundException e) {
                System.err.println(e);
                this.connected = false;
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

        public void sendScreenObject(ScreenObject screenObject) {
            try {
                serialOutputStream.writeObject(screenObject);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        public void run() {
            while (true) {
                try {
                    /* check for balls being sent */
                    Ball recievedBall = (Ball) serialInputStream.readObject();
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