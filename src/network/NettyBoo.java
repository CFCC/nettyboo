package network;

import javax.swing.Timer;
import java.net.InetAddress;


public class NettyBoo{
	private InetAddress address;
	private String hostName;
	private Timer timeoutTimer;

	public NettyBoo(InetAddress address, String hostName, Timer timeoutTimer) {
		this.address = address;
		this.hostName = hostName;
		this.timeoutTimer = timeoutTimer;
	}
	public InetAddress getAddress() {
		return address;
	}
	public String getHostName() {
		return hostName;
	}
	public Timer getTimeoutTimer() {
		return timeoutTimer;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
}
