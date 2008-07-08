package network;

import animation.Ball;
import animation.GameScreen;
import animation.ConnectionHint;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.Color;

@SuppressWarnings({"InfiniteLoopStatement"})

public class Network {
	private ScreenConnection leftScreen;
	private ScreenConnection rightScreen;
	private GameScreen gameScreen;
	private NettyBooFinder nettyBooFinder;


	public Network(final GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		this.leftScreen = new ScreenConnection(gameScreen);
		this.rightScreen = new ScreenConnection(gameScreen);
		this.nettyBooFinder = new NettyBooFinder();

		this.startServerThread();
	}

	/* -----make a client connection to a server */
	public void connectToServer(boolean isLeft, String ipAddress) {
		if (ipAddress != null) {
			ScreenConnection remoteScreen = new ScreenConnection(gameScreen);
			remoteScreen.attemptServerConnection(isLeft, ipAddress);
			if (remoteScreen.isLeft()) {
				this.leftScreen = remoteScreen;
			} else {
				this.rightScreen = remoteScreen;
			}
		}
	}
	public NettyBooFinder getNettyBooFinder() {
		return nettyBooFinder;
	}
	public void disconnect(boolean isLeft) {
		if (isLeft) {
			leftScreen.disconnect();
		} else {
			rightScreen.disconnect();
		}
	}
	/* -----start listenForClients listening */
	void startServerThread() {
		new Thread(new Runnable() {
			Socket client;
			ServerSocket serverListeningSocket;

			public void run() {
				try {
					this.serverListeningSocket = new ServerSocket(ScreenConnection.TCP_PORT);
					while (true) {
						Socket clientSocket = this.serverListeningSocket.accept();
						ScreenConnection remoteScreen = new ScreenConnection(gameScreen);
						remoteScreen.attemptClientConnection(clientSocket);
						if (remoteScreen.isLeft()) {
							leftScreen.disconnect();
							leftScreen = remoteScreen;
						} else {
							rightScreen.disconnect();
							rightScreen = remoteScreen;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
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