package com.raoulvdberge.refinedstorage.apiimpl;

import com.raoulvdberge.refinedstorage.api.IRSAPI;
import com.raoulvdberge.refinedstorage.api.RSAPIInject;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRegistry;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterChannel;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.solderer.SoldererRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.util.Comparer;
import com.raoulvdberge.refinedstorage.apiimpl.util.StackListFluid;
import com.raoulvdberge.refinedstorage.apiimpl.util.StackListItem;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNode;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

public class API implements IRSAPI {
    private static final IRSAPI INSTANCE = new API();

    private IComparer comparer = new Comparer();
    private ISoldererRegistry soldererRegistry = new SoldererRegistry();
    private ICraftingTaskRegistry craftingTaskRegistry = new CraftingTaskRegistry();
    private ICraftingMonitorElementRegistry craftingMonitorElementRegistry = new CraftingMonitorElementRegistry();
    private ICraftingPreviewElementRegistry craftingPreviewElementRegistry = new CraftingPreviewElementRegistry();
    private IReaderWriterHandlerRegistry readerWriterHandlerRegistry = new ReaderWriterHandlerRegistry();
    private Set<BiPredicate<TileEntity, EnumFacing>> connectableConditions = new HashSet<>();

    public static IRSAPI instance() {
        return INSTANCE;
    }

    public static void deliver(ASMDataTable asmDataTable) {
        String annotationClassName = RSAPIInject.class.getCanonicalName();

        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(annotationClassName);

        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                Class clazz = Class.forName(asmData.getClassName());
                Field field = clazz.getField(asmData.getObjectName());

                if (field.getType() == IRSAPI.class) {
                    field.set(null, INSTANCE);
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set: {}" + asmData.getClassName() + "." + asmData.getObjectName(), e);
            }
        }
    }

    @Nonnull
    @Override
    public IComparer getComparer() {
        return comparer;
    }

    @Override
    @Nonnull
    public ISoldererRegistry getSoldererRegistry() {
        return soldererRegistry;
    }

    @Override
    @Nonnull
    public ICraftingTaskRegistry getCraftingTaskRegistry() {
        return craftingTaskRegistry;
    }

    @Override
    @Nonnull
    public ICraftingMonitorElementRegistry getCraftingMonitorElementRegistry() {
        return craftingMonitorElementRegistry;
    }

    @Override
    @Nonnull
    public ICraftingPreviewElementRegistry getCraftingPreviewElementRegistry() {
        return craftingPreviewElementRegistry;
    }

    @Nonnull
    @Override
    public IReaderWriterHandlerRegistry getReaderWriterHandlerRegistry() {
        return readerWriterHandlerRegistry;
    }

    @Nonnull
    @Override
    public IReaderWriterChannel createReaderWriterChannel(String name, INetworkMaster network) {
        return new ReaderWriterChannel(name, network);
    }

    @Nonnull
    @Override
    public IStackList<ItemStack> createItemStackList() {
        return new StackListItem();
    }

    @Override
    @Nonnull
    public IStackList<FluidStack> createFluidStackList() {
        return new StackListFluid();
    }

    @Override
    @Nonnull
    public ICraftingMonitorElementList createCraftingMonitorElementList() {
        return new CraftingMonitorElementList();
    }

    @Override
    public void discoverNode(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));

            if (tile != null && tile.hasCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, facing.getOpposite())) {
                INetworkNode node = tile.getCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, facing.getOpposite());

                if (node.getNetwork() != null) {
                    node.getNetwork().getNodeGraph().rebuild();

                    return;
                }
            }
        }
    }

    @Override
    public int getItemStackHashCode(ItemStack stack) {
        return stack.getItem().hashCode() * (stack.getItemDamage() + 1) * (stack.hasTagCompound() ? stack.getTagCompound().hashCode() : 1);
    }

    @Override
    public int getFluidStackHashCode(FluidStack stack) {
        return stack.getFluid().hashCode() * (stack.tag != null ? stack.tag.hashCode() : 1);
    }
}
