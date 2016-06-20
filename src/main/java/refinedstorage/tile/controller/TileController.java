package refinedstorage.tile.controller;

import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.network.INetworkSlave;
import refinedstorage.api.network.NetworkMaster;
import refinedstorage.api.network.NetworkMasterRegistry;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerController;
import refinedstorage.tile.ISynchronizedContainer;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.ArrayList;
import java.util.List;

public class TileController extends TileBase implements IEnergyReceiver, ISynchronizedContainer, IRedstoneModeConfig {
    private NetworkMaster network;

    // Only used client side
    private List<ClientMachine> clientMachines = new ArrayList<ClientMachine>();
    private int energy;
    private int energyUsage;
    private EnumControllerType type;
    private RedstoneMode redstoneMode;

    public NetworkMaster getNetwork() {
        if (network == null) {
            network = NetworkMasterRegistry.get(pos, worldObj.provider.getDimension());
        }

        return network;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NetworkMaster.NBT_ENERGY, getNetwork() != null ? getNetwork().getEnergy().getEnergyStored() : 0);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        energy = tag.getInteger(NetworkMaster.NBT_ENERGY);

        super.readUpdate(tag);
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return getNetwork().getEnergy().receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return getNetwork().getEnergy().getEnergyStored();
    }

    public int getEnergyScaled(int i) {
        float stored = worldObj.isRemote ? energy : getNetwork().getEnergy().getEnergyStored();
        float max = NetworkMaster.ENERGY_CAPACITY;

        return (int) (stored / max * (float) i);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getNetwork().getEnergy().getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return worldObj.isRemote ? redstoneMode : getNetwork().getRedstoneMode();
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        getNetwork().setRedstoneMode(mode);
    }

    public List<ClientMachine> getClientMachines() {
        return clientMachines;
    }

    public int getEnergy() {
        return energy;
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    public EnumControllerType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.CONTROLLER) {
            this.type = (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? EnumControllerType.NORMAL : type;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        this.energy = buf.readInt();
        this.energyUsage = buf.readInt();
        this.redstoneMode = RedstoneMode.getById(buf.readInt());

        List<ClientMachine> machines = new ArrayList<ClientMachine>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            ClientMachine machine = new ClientMachine();
            machine.energyUsage = buf.readInt();
            machine.amount = buf.readInt();
            machine.stack = ByteBufUtils.readItemStack(buf);

            machines.add(machine);
        }

        this.clientMachines = machines;
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        buf.writeInt(getNetwork().getEnergy().getEnergyStored());
        buf.writeInt(getNetwork().getEnergyUsage());

        buf.writeInt(getNetwork().getRedstoneMode().id);

        List<ClientMachine> m = new ArrayList<ClientMachine>();

        for (INetworkSlave machine : getNetwork().getSlaves()) {
            if (machine.canUpdate()) {
                IBlockState state = worldObj.getBlockState(machine.getPosition());

                ClientMachine clientMachine = new ClientMachine();

                clientMachine.energyUsage = machine.getEnergyUsage();
                clientMachine.amount = 1;
                clientMachine.stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

                if (m.contains(clientMachine)) {
                    for (ClientMachine other : m) {
                        if (other.equals(clientMachine)) {
                            other.amount++;
                            break;
                        }
                    }
                } else {
                    m.add(clientMachine);
                }
            }
        }

        buf.writeInt(m.size());

        for (ClientMachine machine : m) {
            buf.writeInt(machine.energyUsage);
            buf.writeInt(machine.amount);
            ByteBufUtils.writeItemStack(buf, machine.stack);
        }
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerController.class;
    }
}
