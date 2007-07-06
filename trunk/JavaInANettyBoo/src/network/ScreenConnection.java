package network;

import animation.Ball;
import animation.GameScreen;
import animation.ScreenObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ScreenConnection extends Thread {
    private static final String REQUEST_CONNECTION = "Requesting connection on ";
    private static final String ACCEPTED_CONNECTION = "Accepted";
    private static final String REFUSED_CONNECTION = "Refused";

    private Socket socket;
    private ObjectOutputStream serialOutputStream;
    private ObjectInputStream serialInputStream;
    private boolean connected;
    private boolean left;
    private boolean killThread;
    private GameScreen gameScreen;
    public static final int TCP_PORT = 55209;

    public ScreenConnection(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public boolean isConnected() {
        return connected;
    }

    public Boolean isLeft() {
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
                this.left = clientString.endsWith("right");
                System.out.println("Accepted connection to " + socket.getInetAddress().toString());
                this.serialOutputStream.writeObject(ACCEPTED_CONNECTION);
                this.connected = true;
                this.start();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    /* attempt to connect to another program's listenForClients */
    boolean attemptServerConnection(String side, String ipAddress) {
        try {
            this.left = side.equalsIgnoreCase("left");
            this.socket = new Socket(ipAddress, TCP_PORT);
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
            e.printStackTrace();
            this.connected = false;
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            this.connected = false;
            return false;
        }
    }

    /* close a connection to the screen */
    void disconnect() {
        if(this.connected) {
            this.killThread = true;
            this.connected = false;
        }
    }

    public void sendScreenObject(ScreenObject screenObject) {
        try {
            serialOutputStream.writeObject(screenObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                /* check for balls being sent */
                Ball recievedBall = (Ball) serialInputStream.readObject();
                if(this.left) {
                    this.gameScreen.addBallFromLeft(recievedBall);
                } else {
                    this.gameScreen.addBallFromRight(recievedBall);
                }
            } catch (IOException e) {
                this.disconnect();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                this.disconnect();
                e.printStackTrace();
            }
            if(this.killThread) {
                return;
            }
        }
    }
}
