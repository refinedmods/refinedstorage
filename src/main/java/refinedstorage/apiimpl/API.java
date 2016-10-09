package refinedstorage.apiimpl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import refinedstorage.api.IRSAPI;
import refinedstorage.api.RSAPIInject;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import refinedstorage.api.solderer.ISoldererRegistry;
import refinedstorage.api.util.IComparer;
import refinedstorage.api.util.IFluidStackList;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry;
import refinedstorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import refinedstorage.apiimpl.solderer.SoldererRegistry;
import refinedstorage.apiimpl.util.Comparer;
import refinedstorage.apiimpl.util.FluidStackList;
import refinedstorage.apiimpl.util.ItemStackList;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Set;

public class API implements IRSAPI {
    private static final IRSAPI INSTANCE = new API();

    private IComparer comparer = new Comparer();
    private ISoldererRegistry soldererRegistry = new SoldererRegistry();
    private ICraftingTaskRegistry craftingTaskRegistry = new CraftingTaskRegistry();
    private ICraftingMonitorElementRegistry craftingMonitorElementRegistry = new CraftingMonitorElementRegistry();

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

    @Nonnull
    @Override
    public ICraftingMonitorElementRegistry getCraftingMonitorElementRegistry() {
        return craftingMonitorElementRegistry;
    }

    @Nonnull
    @Override
    public IItemStackList createItemStackList() {
        return new ItemStackList();
    }

    @Nonnull
    @Override
    public IFluidStackList createFluidStackList() {
        return new FluidStackList();
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
