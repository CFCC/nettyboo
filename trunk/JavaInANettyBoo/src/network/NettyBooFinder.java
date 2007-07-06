package network;

import javax.swing.DefaultListModel;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class NettyBooFinder {
    private static final String MULTICAST_CQ = "JavaInANettyBoo CQ";
    private static final int CQ_PORT = 13246;
    private static final String MULTICAST_GROUP_ADDRESS = "225.0.0.27";
    private static final String CQ_RESPONSE = "I'm here!";
    private static final int RESPONSE_PORT = 61802;

    MulticastSocket cqMulticastSocket;      /* for sending and recieving pings */
    DatagramSocket responseDatagramSocket;  /* for sending responses to pings */
    DatagramSocket responseListeningSocket; /* for recieving resposnes to pings */
    DatagramPacket pingingPacket;
    DatagramPacket listeningPacket;
    byte[] pingBuffer;
    byte[] listenBuffer;
    boolean killThread = false;

    public NettyBooFinder() {
        try {
            this.cqMulticastSocket = new MulticastSocket(CQ_PORT);
            this.cqMulticastSocket.joinGroup(InetAddress.getByName(MULTICAST_GROUP_ADDRESS));
            this.responseDatagramSocket = new DatagramSocket();
            this.responseListeningSocket = new DatagramSocket(RESPONSE_PORT);
            this.pingBuffer = new byte[1024];
            this.listenBuffer = new byte[1024];
            this.pingingPacket = new DatagramPacket(this.pingBuffer, this.pingBuffer.length);
            this.listeningPacket = new DatagramPacket(this.listenBuffer, this.listenBuffer.length);
            this.listenForBroadcasts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Set<InetAddress> getLocalIPAddresses() {
        Set<InetAddress> ipList = new HashSet<InetAddress>();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    ipList.add(inetAddressEnumeration.nextElement());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }

    public void findMoreNettyBoos(final DefaultListModel data) {
        try {
            this.pingingPacket.setPort(CQ_PORT);
            this.pingingPacket.setAddress(InetAddress.getByName("255.255.255.255"));
            this.pingingPacket.setData(MULTICAST_CQ.getBytes());
            this.cqMulticastSocket.send(this.pingingPacket);
            System.out.println("sent CQ...");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        /* Listen for responses */
                        System.out.println("listening for pings...");
                        while(true) {
                            responseListeningSocket.receive(pingingPacket);
                            String responseString = new String(pingingPacket.getData(), pingingPacket.getOffset(), pingingPacket.getLength());
                            System.out.println("recieved response: " + responseString);
                            if(responseString.equals(CQ_RESPONSE)) {
                                data.addElement(pingingPacket.getAddress().getHostAddress());
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
                        System.out.println("murder-suicide thread");
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
                System.out.println("broadcast listener started");
                Set<InetAddress> ipList = getLocalIPAddresses();
                try {
                    while(true) {
                        cqMulticastSocket.receive(listeningPacket);
                        String packetData = new String(listeningPacket.getData(), listeningPacket.getOffset(), listeningPacket.getLength());
                        System.out.println("recieved ping: " + packetData);
                        if(packetData.equals(MULTICAST_CQ) && !ipList.contains(listeningPacket.getAddress())) {
                            System.out.println("ping good");
                            listeningPacket.setData(CQ_RESPONSE.getBytes());
                            responseDatagramSocket.connect(listeningPacket.getAddress(), RESPONSE_PORT);
                            listeningPacket.setPort(RESPONSE_PORT);
                            responseDatagramSocket.send(listeningPacket);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
