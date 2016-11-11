package com.raoulvdberge.refinedstorage.apiimpl;

import com.raoulvdberge.refinedstorage.api.IRSAPI;
import com.raoulvdberge.refinedstorage.api.RSAPIInject;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRegistry;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterChannel;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterHandlerRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.solderer.SoldererRegistry;
import com.raoulvdberge.refinedstorage.apiimpl.util.Comparer;
import com.raoulvdberge.refinedstorage.apiimpl.util.FluidStackList;
import com.raoulvdberge.refinedstorage.apiimpl.util.ItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Set;

public class API implements IRSAPI {
    private static final IRSAPI INSTANCE = new API();

    private IComparer comparer = new Comparer();
    private ISoldererRegistry soldererRegistry = new SoldererRegistry();
    private ICraftingTaskRegistry craftingTaskRegistry = new CraftingTaskRegistry();
    private ICraftingMonitorElementRegistry craftingMonitorElementRegistry = new CraftingMonitorElementRegistry();
    private ICraftingPreviewElementRegistry craftingPreviewElementRegistry = new CraftingPreviewElementRegistry();
    private IReaderWriterHandlerRegistry readerWriterHandlerRegistry = new ReaderWriterHandlerRegistry();

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
    public IItemStackList createItemStackList() {
        return new ItemStackList();
    }

    @Override
    @Nonnull
    public IFluidStackList createFluidStackList() {
        return new FluidStackList();
    }

    @Override
    @Nonnull
    public ICraftingMonitorElementList createCraftingMonitorElementList() {
        return new CraftingMonitorElementList();
    }

    @Override
    public int getItemStackHashCode(ItemStack stack) {
        return stack.getItem().hashCode() * (stack.getItemDamage() + 1) * (stack.hasTagCompound() ? stack.getTagCompound().hashCode() : 1);
    }

    @Override
    public int getFluidStackHashCode(FluidStack stack) {
        return stack.getFluid().hashCode() * (stack.tag != null ? stack.tag.hashCode() : 1);
    }

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
}
