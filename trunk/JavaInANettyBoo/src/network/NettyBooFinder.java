package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

public class NettyBooFinder {
    private static final String MULTICAST_CQ = "JavaInANettyBoo CQ";
    private static final int CQ_PORT = 13246;
    private static final String MULTICAST_GROUP_ADDRESS = "225.0.0.27";
    private static final String CQ_RESPONSE = "I'm here!";
    private static final int RESPONSE_PORT = 12345;
    MulticastSocket cqMulticastSocket;

    DatagramSocket responseDatagramSocket;
    DatagramPacket datagramPacket;
    byte[] buffer;
    boolean killThread = false;

    public NettyBooFinder() {
        try {
            this.cqMulticastSocket = new MulticastSocket(CQ_PORT);
            this.cqMulticastSocket.joinGroup(InetAddress.getByName(MULTICAST_GROUP_ADDRESS));
            this.responseDatagramSocket = new DatagramSocket(RESPONSE_PORT);
            this.buffer = new byte[1024];
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    void findMoreNettyBoos(final List<String> ipList) {
        try {
            this.buffer = MULTICAST_CQ.getBytes();
            this.datagramPacket.setData(this.buffer);
            this.cqMulticastSocket.send(this.datagramPacket);
            new Thread(new Runnable() {
                public void run() {
                    /* Listen for responses */
                    try {
                        while(true) {
                            responseDatagramSocket.receive(datagramPacket);
                            ipList.add(datagramPacket.getAddress().toString());
                            if(killThread) {
                                return;
                            }
                        }
                    } catch (IOException e) {
                        System.err.println(e);
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                        killThread = true;
                    } catch (InterruptedException e) {
                        System.err.println(e);
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    /* send and recieve multicast packets */
    void listenForBroadcasts() {
        /*try {

            /*DatagramSocket responseSocket = new DatagramSocket(RESPONSE_PORT);*/

            new Thread(new Runnable() {
                public void run() {
                    try {
                        cqMulticastSocket.receive(datagramPacket);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /* Listen for breadcasts
                    try {
                        List<String> cqList = new ArrayList<String>();
                        byte[] buffer = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                        //multicastSocket.receive(datagramPacket);
                        if(datagramPacket.getData().toString().equals(MULTICAST_CQ)) {
                            buffer = CQ_RESPONSE.getBytes();
                            datagramPacket.getSocketAddress();
                            datagramPacket.setData(buffer);
                        }
                    } catch (IOException e) {
                        System.err.println(e);
                        e.printStackTrace();
                    }*/
                }
            }).start();


            /* Breadcast
            try {
                SocketAddress address = new InetSocketAddress("255.255.255.255", CQ_PORT);
                byte[] buffer = MULTICAST_CQ.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length,address);
                //multicastSocket.send(datagramPacket);

            } catch (SocketException e) {
                System.err.println(e);
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println(e);
                e.printStackTrace();
            }*/
        /*} catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }*/
    }
}
