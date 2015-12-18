package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileMachine extends TileBase implements INetworkTile {
	public static final String NBT_REDSTONE_MODE = "RedstoneMode";

	protected boolean connected = false;

	private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

	private int xController;
	private int yController;
	private int zController;

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

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote && isConnected()) {
			updateMachine();
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean isEnabled() {
		switch (redstoneMode) {
			case IGNORE:
				return true;
			case HIGH:
				return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			case LOW:
				return !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		}

		return false;
	}

	public RedstoneMode getRedstoneMode() {
		return redstoneMode;
	}

	public void setRedstoneMode(RedstoneMode mode) {
		this.redstoneMode = mode;
	}

	public TileController getController() {
		return (TileController) worldObj.getTileEntity(xController, yController, zController);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		connected = buf.readBoolean();

		if (connected) {
			xController = buf.readInt();
			yController = buf.readInt();
			zController = buf.readInt();
		}

		redstoneMode = RedstoneMode.getById(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(connected);

		if (connected) {
			buf.writeInt(xController);
			buf.writeInt(yController);
			buf.writeInt(zController);
		}

		buf.writeInt(redstoneMode.id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		redstoneMode = RedstoneMode.getById(nbt.getInteger(NBT_REDSTONE_MODE));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger(NBT_REDSTONE_MODE, redstoneMode.id);
	}

	public abstract int getEnergyUsage();

	public abstract void updateMachine();
}
