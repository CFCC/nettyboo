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
    private Network network;
    private GameScreen gameScreen;

    public ScreenConnection(Network network, GameScreen gameScreen) {
        this.network = network;
        this.gameScreen = gameScreen;
    }

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
            if(side.equalsIgnoreCase("left")) {
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
                if(this.left) {
                    this.gameScreen.addBallFromLeft(recievedBall);
                } else {
                    this.gameScreen.addBallFromRight(recievedBall);
                }
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
