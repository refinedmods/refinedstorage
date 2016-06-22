package refinedstorage.api.network;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.autocrafting.CraftingPattern;
import refinedstorage.autocrafting.task.ICraftingTask;
import refinedstorage.tile.config.RedstoneMode;

import java.util.Iterator;
import java.util.List;

public interface INetworkMaster {
    RedstoneMode getRedstoneMode();

    void setRedstoneMode(RedstoneMode mode);

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

    ICraftingTask createCraftingTask(CraftingPattern pattern);

    void cancelCraftingTask(ICraftingTask task);

    List<CraftingPattern> getPatterns();

    List<CraftingPattern> getPattern(ItemStack pattern, int flags);

    CraftingPattern getPatternWithBestScore(ItemStack pattern);

    CraftingPattern getPatternWithBestScore(ItemStack pattern, int flags);

    void updateItemsWithClient();

    void updateItemsWithClient(EntityPlayerMP player);

    ItemStack push(ItemStack stack, int size, boolean simulate);

    ItemStack take(ItemStack stack, int size);

    ItemStack take(ItemStack stack, int size, int flags);

    ItemStack getItem(ItemStack stack, int flags);

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);
}
