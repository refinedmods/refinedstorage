package refinedstorage.apiimpl;

import refinedstorage.api.IAPI;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import refinedstorage.api.solderer.ISoldererRegistry;
import refinedstorage.api.util.IItemStackList;
import refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementRegistry;
import refinedstorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import refinedstorage.apiimpl.solderer.SoldererRegistry;
import refinedstorage.apiimpl.util.ItemStackList;

import javax.annotation.Nonnull;

public class API implements IAPI {
    public static final IAPI INSTANCE = new API();

    private ISoldererRegistry soldererRegistry = new SoldererRegistry();
    private ICraftingTaskRegistry craftingTaskRegistry = new CraftingTaskRegistry();
    private ICraftingMonitorElementRegistry craftingMonitorElementRegistry = new CraftingMonitorElementRegistry();

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
}
