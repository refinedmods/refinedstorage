package refinedstorage.tile;

import mcmultipart.capabilities.ISlottedCapabilityProvider;
import mcmultipart.capabilities.MultipartCapabilityHelper;
import mcmultipart.microblock.IMicroblock;
import mcmultipart.microblock.IMicroblockContainerTile;
import mcmultipart.microblock.MicroblockContainer;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.PartSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.network.NetworkUtils;

public abstract class TileMultipartNode extends TileNode implements IMicroblockContainerTile, ISlottedCapabilityProvider {
    private MicroblockContainer container;

    @Override
    public World getWorldIn() {
        return getWorld();
    }

    @Override
    public BlockPos getPosIn() {
        return getPos();
    }

    @Override
    public MicroblockContainer getMicroblockContainer() {
        if (container == null) {
            container = new MicroblockContainer(this);

            container.getPartContainer().setListener(new IMultipartContainer.IMultipartContainerListener() {
                @Override
                public void onAddPartPre(IMultipart part) {
                }

                @Override
                public void onAddPartPost(IMultipart part) {
                    onMicroblocksChanged();
                }

                @Override
                public void onRemovePartPre(IMultipart part) {
                }

                @Override
                public void onRemovePartPost(IMultipart part) {
                    onMicroblocksChanged();
                }
            });
        }

        return container;
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return true;
    }

    @Override
    public void onMicroblocksChanged() {
        markDirty();

        if (getNetwork() != null) {
            NetworkUtils.rebuildGraph(getNetwork());
        } else if (worldObj != null) {
            RefinedStorageBlocks.CABLE.attemptConnect(worldObj, pos);
        }
    }

    public static boolean hasBlockingMicroblock(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileMultipartNode) {
            for (IMicroblock microblock : ((TileMultipartNode) tile).getMicroblockContainer().getParts()) {
                if (isBlockingMicroblock(microblock, direction)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isBlockingMicroblock(IMicroblock microblock, EnumFacing direction) {
        if (!(microblock instanceof IMicroblock.IFaceMicroblock)) {
            return false;
        }

        IMicroblock.IFaceMicroblock faceMicroblock = (IMicroblock.IFaceMicroblock) microblock;

        return faceMicroblock.getFace() == direction && !faceMicroblock.isFaceHollow();
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return !hasBlockingMicroblock(worldObj, pos, direction) && !hasBlockingMicroblock(worldObj, pos.offset(direction), direction.getOpposite());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (super.hasCapability(capability, facing)) {
            return true;
        }

        return MultipartCapabilityHelper.hasCapability(container, capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        T impl = super.getCapability(capability, facing);

        if (impl != null) {
            return impl;
        }

        return MultipartCapabilityHelper.getCapability(container, capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, PartSlot slot, EnumFacing facing) {
        return container.hasCapability(capability, slot, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, PartSlot slot, EnumFacing facing) {
        return container.getCapability(capability, slot, facing);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        for (IMultipart part : getMicroblockContainer().getParts()) {
            part.onLoaded();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();

        for (IMultipart part : getMicroblockContainer().getParts()) {
            part.onUnloaded();
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        getMicroblockContainer().getPartContainer().writeToNBT(tag);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        getMicroblockContainer().getPartContainer().readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        getMicroblockContainer().getPartContainer().writeToNBT(tag);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        getMicroblockContainer().getPartContainer().readFromNBT(tag);

        super.readUpdate(tag);
    }

    @Override
    public boolean canRenderBreaking() {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bounds = super.getRenderBoundingBox().offset(-getPosIn().getX(), -getPosIn().getY(), -getPosIn().getZ());

        for (IMultipart part : getMicroblockContainer().getParts()) {
            AxisAlignedBB bb = part.getRenderBoundingBox();
            if (bb != null) {
                bounds = bounds.union(bb);
            }
        }

        return bounds.offset(getPosIn().getX(), getPosIn().getY(), getPosIn().getZ());
    }
}