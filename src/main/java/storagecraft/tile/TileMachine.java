package storagecraft.tile;

import io.netty.buffer.ByteBuf;

public abstract class TileMachine extends TileSC implements INetworkTile {
	protected boolean connected = false;

	public void onConnected(TileController controller) {
		connected = true;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void onDisconnected() {
		connected = false;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean isConnected() {
		return connected;
	}

	public abstract int getEnergyUsage();

	@Override
	public void fromBytes(ByteBuf buf) {
		connected = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(connected);
	}
}
