package refinedstorage.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import refinedstorage.RefinedStorage;
import refinedstorage.network.MessageTileContainerUpdate;
import refinedstorage.network.MessageTileUpdate;

public abstract class TileBase extends TileEntity implements ITickable {
    public static final String NBT_DIRECTION = "Direction";
    public static final String NBT_ENERGY = "Energy";

    public static final int UPDATE_RANGE = 64;

    private EnumFacing direction = EnumFacing.NORTH;

    protected int ticks;

    @Override
    public void update() {
        ticks++;

        if (!worldObj.isRemote) {
            if (this instanceof INetworkTile) {
                TargetPoint target = new TargetPoint(worldObj.provider.getDimensionType().getId(), pos.getX(), pos.getY(), pos.getZ(), UPDATE_RANGE);

                RefinedStorage.NETWORK.sendToAllAround(new MessageTileUpdate(this), target);

                for (EntityPlayer player : worldObj.playerEntities) {
                    if (((INetworkTile) this).getContainer() == player.openContainer.getClass()) {
                        RefinedStorage.NETWORK.sendTo(new MessageTileContainerUpdate(this), (EntityPlayerMP) player);
                    }
                }
            }
        }
    }

    public void setDirection(EnumFacing direction) {
        this.direction = direction;
    }

    public EnumFacing getDirection() {
        return direction;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        direction = EnumFacing.getFront(nbt.getInteger(NBT_DIRECTION));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_DIRECTION, direction.ordinal());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger(NBT_DIRECTION, direction.ordinal());

        return new SPacketUpdateTileEntity(pos, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        direction = EnumFacing.getFront(packet.getNbtCompound().getInteger(NBT_DIRECTION));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public IInventory getDroppedInventory() {
        return null;
    }
}
