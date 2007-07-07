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

    private MulticastSocket cqMulticastSocket;      /* for sending and recieving pings */
    private DatagramSocket responseListeningSocket;
    private boolean killThread = false;
    private long timeOfLastMulticastReception;
    private InetAddress lastMulticastSender;

    public NettyBooFinder() {
        try {
            this.cqMulticastSocket = new MulticastSocket(CQ_PORT);
            this.cqMulticastSocket.joinGroup(InetAddress.getByName(MULTICAST_GROUP_ADDRESS));
            this.responseListeningSocket = new DatagramSocket(RESPONSE_PORT);
            this.listenForBroadcasts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<InetAddress> getLocalIPAddresses() {
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
            byte[] pingBuffer = new byte[1024];
            final DatagramPacket pingingPacket = new DatagramPacket(pingBuffer, pingBuffer.length);
            pingingPacket.setPort(CQ_PORT);
            pingingPacket.setAddress(InetAddress.getByName("255.255.255.255"));
            pingingPacket.setData(MULTICAST_CQ.getBytes());
            this.cqMulticastSocket.send(pingingPacket);
            System.out.println("sent CQ...");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        /* Listen for responses */
                        System.out.println("listening for responses...");
                        while(true) {
                            responseListeningSocket.receive(pingingPacket);
                            String responseString = new String(pingingPacket.getData(), pingingPacket.getOffset(), pingingPacket.getLength());
                            System.out.println("recieved response: " + responseString);
                            if(responseString.equals(CQ_RESPONSE)) {
                                data.addElement(pingingPacket.getAddress().getAddress());
                            }
                            if(killThread) {
                                break;
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
                        Thread.sleep(500);
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
                System.out.println("listening for pings...");
                Set<InetAddress> ipList = getLocalIPAddresses();
                try {
                    while(true) {
                        byte[] listenBuffer = new byte[1024];
                        DatagramPacket listeningPacket = new DatagramPacket(listenBuffer, listenBuffer.length);
                        cqMulticastSocket.receive(listeningPacket);
                        String packetData = new String(listeningPacket.getData(), listeningPacket.getOffset(), listeningPacket.getLength());
                        System.out.println("recieved ping: " + packetData);
                        if(packetData.equals(MULTICAST_CQ) && !ipList.contains(listeningPacket.getAddress())) {
                            System.out.println("ping good");
                            timeOfLastMulticastReception = System.currentTimeMillis();
                            lastMulticastSender = listeningPacket.getAddress();
                            listeningPacket.setData(CQ_RESPONSE.getBytes());
                            DatagramSocket responseDatagramSocket;
                            responseDatagramSocket = new DatagramSocket();
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

    public long getTimeOfLastMulticastReception() {
        return timeOfLastMulticastReception;
    }

    public InetAddress getLastMulticastSender() {
        return lastMulticastSender;
    }
}