package network;

import javax.swing.DefaultListModel;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class NettyBooFinder {
    private static final String MULTICAST_CQ = "JavaInANettyBoo CQ";
    private static final int CQ_PORT = 13246;
    private static final String MULTICAST_GROUP_ADDRESS = "225.0.0.27";
    private static final String CQ_RESPONSE = "I'm here!";
    private static final int RESPONSE_PORT = 12345;

    MulticastSocket cqMulticastSocket;
    DatagramSocket responseDatagramSocket;
    DatagramPacket pingingPacket;
    DatagramPacket listeningPacket;
    byte[] buffer;
    boolean killThread = false;

    public NettyBooFinder() {
        try {
            this.cqMulticastSocket = new MulticastSocket(CQ_PORT);
            this.cqMulticastSocket.joinGroup(InetAddress.getByName(MULTICAST_GROUP_ADDRESS));
            this.responseDatagramSocket = new DatagramSocket(RESPONSE_PORT);
            this.buffer = new byte[1024];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void findMoreNettyBoos(final DefaultListModel data) {
        try {
            this.buffer = MULTICAST_CQ.getBytes();
            this.pingingPacket.setData(this.buffer);
            this.cqMulticastSocket.send(this.pingingPacket);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        /* Listen for responses */
                        while(true) {
                            responseDatagramSocket.receive(pingingPacket);
                            if(pingingPacket.getData().toString().equals(CQ_RESPONSE)) {
                                data.addElement(pingingPacket.getAddress().toString());
                            }
                            if(killThread) {
                                return;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /* Murder-suicide thread */
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                        killThread = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void listenForBroadcasts() {
        /* start thread to listen for pings and respond to each */
        new Thread(new Runnable() {
            public void run() {
                try {
                    cqMulticastSocket.receive(listeningPacket);
                    if(listeningPacket.getData().toString().equals(MULTICAST_CQ)) {
                        listeningPacket.setData(CQ_RESPONSE.getBytes());
                        responseDatagramSocket.connect(listeningPacket.getSocketAddress());
                        responseDatagramSocket.send(listeningPacket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
