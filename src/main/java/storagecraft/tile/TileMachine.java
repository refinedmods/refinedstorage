package storagecraft.tile;

import io.netty.buffer.ByteBuf;

public abstract class TileMachine extends TileSC implements INetworkTile {
	protected boolean connected = false;
	private int xController, yController, zController;

	public void onConnected(TileController controller) {
		this.connected = true;
		this.xController = controller.xCoord;
		this.yController = controller.yCoord;
		this.zController = controller.zCoord;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void onDisconnected() {
		this.connected = false;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean isConnected() {
		return connected;
	}

	public TileController getController() {
		return (TileController) worldObj.getTileEntity(xController, yController, zController);
	}

	public abstract int getEnergyUsage();

	@Override
	public void fromBytes(ByteBuf buf) {
		connected = buf.readBoolean();

		if (connected) {
			xController = buf.readInt();
			yController = buf.readInt();
			zController = buf.readInt();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(connected);

		if (connected) {
			buf.writeInt(xController);
			buf.writeInt(yController);
			buf.writeInt(zController);
		}
	}
}
