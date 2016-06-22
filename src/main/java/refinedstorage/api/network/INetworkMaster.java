package refinedstorage.api.network;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;

import java.util.Iterator;
import java.util.List;

public interface INetworkMaster {
    World getWorld();

    void setWorld(World world);

    EnergyStorage getEnergy();

    int getEnergyUsage();

    BlockPos getPosition();

    boolean canRun();

    void update();

    Iterator<INetworkSlave> getSlaves();

    void addSlave(BlockPos slave);

    void removeSlave(BlockPos slave);

    IGridHandler getGridHandler();

    IWirelessGridHandler getWirelessGridHandler();

    List<ItemStack> getItems();

    List<ICraftingTask> getCraftingTasks();

    void addCraftingTask(ICraftingTask task);

    void addCraftingTaskAsLast(ICraftingTask task);

    ICraftingTask createCraftingTask(ICraftingPattern pattern);

    void cancelCraftingTask(ICraftingTask task);

    List<ICraftingPattern> getPatterns();

    List<ICraftingPattern> getPattern(ItemStack pattern, int flags);

    ICraftingPattern getPatternWithBestScore(ItemStack pattern);

    ICraftingPattern getPatternWithBestScore(ItemStack pattern, int flags);

    void updateItemsWithClient();

    void updateItemsWithClient(EntityPlayerMP player);

    ItemStack push(ItemStack stack, int size, boolean simulate);

    ItemStack take(ItemStack stack, int size);

    ItemStack take(ItemStack stack, int size, int flags);

    ItemStack getItem(ItemStack stack, int flags);

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);
}
