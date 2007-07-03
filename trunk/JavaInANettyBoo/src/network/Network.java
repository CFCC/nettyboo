package network;

import java.util.Scanner;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: Jul 2, 2007
 * Time: 2:49:42 PM
 * To change this template use File | Settings | File Templates.
 */


public class Network{
    String leftIP;
    String rightIP;
    Socket leftSocket;
    Socket rightSocket;
    ServerSocket serverListeningSocket;

    public Network() {
        this.server();
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Left computer IP?");
        this.leftIP = keyboard.next();
        System.out.println("Right computer IP?");
        this.rightIP = keyboard.next();
        if(!this.leftIP.equalsIgnoreCase("none")) {
            try {
                this.leftSocket = new Socket(this.leftIP, 2000);
                System.out.println(this.leftIP + " Connected as left screen.");
                BufferedReader fromServer;
                PrintStream toServer;
                fromServer = new BufferedReader(new InputStreamReader(this.leftSocket.getInputStream()));
                toServer = new PrintStream(this.leftSocket.getOutputStream());
                System.out.println("I say: \"I got candy. Get in the van. I want you on my left\"");
                toServer.println("I got candy. Get in the van. I want you on my left");
                String serverResponse = fromServer.readLine();
                System.out.println("Server says: \"" + serverResponse + "\"");
                if(serverResponse.equals("Sorry, Charlie. I got enough flapjacks on the stack.")) {
                    this.leftIP = "none";
                    this.leftSocket.close();
                }
            } catch (IOException e) {
                System.err.println("CBTF: Can't connect to left screen server.\n" + e);
            }
        } else {
            System.out.println("No left screen.");
        }
        if(!this.rightIP.equalsIgnoreCase("none")) {
            try {
                this.rightSocket = new Socket(this.rightIP, 2000);
                System.out.println(this.rightIP + " Connected as right screen.");
                BufferedReader fromServer;
                PrintStream toServer;
                fromServer = new BufferedReader(new InputStreamReader(rightSocket.getInputStream()));
                toServer = new PrintStream(rightSocket.getOutputStream());
                System.out.println("I say: \"I got candy. Get in the van. I want you on my right\"");
                toServer.println("I got candy. Get in the van. I want you on my right");
                String serverResponse = fromServer.readLine();
                System.out.println("Server says: \"" + serverResponse + "\"");
                if(serverResponse.equals("Sorry, Charlie. I got enough flapjacks on the stack.")) {
                    this.rightIP = "none";
                    this.rightSocket.close();
                }
            } catch (IOException e) {
                System.err.println("CBTF: Can't connect to right screen server.\n" + e);
            }
        } else {
            System.out.println("No right screen.");
        }
    }

    void server() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    serverListeningSocket = new ServerSocket(2000);
                } catch(IOException e) {
                    System.err.println("CBTF: Can't start server.\n" + e);
                }
                System.out.println("Server socket listening...");
                while(true) {
                    try {
                        Socket clientSocket = serverListeningSocket.accept();
                        Connection connection = new Connection(clientSocket);
                    } catch (IOException e) {
                        System.err.println("CBTF: Server listening loop failed.\n" + e);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        Network david = new Network();
    }

    class Connection extends Thread {
        Socket client;
        BufferedReader fromClient;
        PrintStream toClient;

        public Connection(Socket client) {
            this.client = client;
            try {
                fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
                toClient = new PrintStream(client.getOutputStream());
            } catch (IOException e) {
                System.err.println("CBTF: Server can't set up socket streams.\n" + e);
            }
            this.start();
        }

        public void run() {
            try {
                String clientString = fromClient.readLine();
                if(clientString.startsWith("I got candy. Get in the van.")) {
                    if(clientString.endsWith("left")) {
                        if(rightIP.equals("none")) {
                            toClient.println("roger Roger. What's your vector Victor?");
                            rightSocket = client;
                            rightIP = client.getInetAddress().toString();
                        } else {
                            toClient.println("Sorry, Charlie. I got enough flapjacks on the stack.");
                            client.close();
                        }
                    } else {
                        if(leftIP.equals("none")) {
                            toClient.println("roger Roger. What's your vector Victor?");
                            leftSocket = client;
                            leftIP = client.getInetAddress().toString();
                        } else {
                            toClient.println("Sorry, Charlie. I got enough flapjacks on the stack.");
                            client.close();
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("CBTF: Server can't read from client\n" + e);
            }
        }
    }
}

