package network;

public interface FinderListener {
	public void NettyBooFound(NettyBoo nettyBoo);
	public void NettyBooLost(NettyBoo nettyBoo);
	void NettyBooUpdated(NettyBoo nettyBoo);
}
