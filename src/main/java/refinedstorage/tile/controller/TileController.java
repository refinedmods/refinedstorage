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
import refinedstorage.api.storagenet.StorageNetwork;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerController;
import refinedstorage.tile.ISynchronizedContainer;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.ArrayList;
import java.util.List;

public class TileController extends TileBase implements IEnergyReceiver, ISynchronizedContainer, IRedstoneModeConfig {
    private StorageNetwork network;

    // Only used client side
    private List<ClientMachine> clientMachines = new ArrayList<ClientMachine>();
    private int energy;
    private int energyUsage;
    private EnumControllerType type;
    private RedstoneMode redstoneMode;

    public void setNetwork(StorageNetwork network) {
        this.network = network;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(StorageNetwork.NBT_ENERGY, network.getEnergy().getEnergyStored());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        energy = tag.getInteger(StorageNetwork.NBT_ENERGY);

        super.readUpdate(tag);
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return network.getEnergy().receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return network.getEnergy().getEnergyStored();
    }

    public int getEnergyScaled(int i) {
        float stored = worldObj.isRemote ? energy : network.getEnergy().getEnergyStored();
        float max = StorageNetwork.ENERGY_CAPACITY;

        return (int) (stored / max * (float) i);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return network.getEnergy().getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return worldObj.isRemote ? redstoneMode : network.getRedstoneMode();
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        network.setRedstoneMode(mode);
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
        buf.writeInt(network.getEnergy().getEnergyStored());
        buf.writeInt(network.getEnergyUsage());

        buf.writeInt(network.getRedstoneMode().id);

        List<ClientMachine> m = new ArrayList<ClientMachine>();

        for (TileMachine machine : network.getMachines()) {
            if (machine.canUpdate()) {
                IBlockState state = worldObj.getBlockState(machine.getPos());

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
