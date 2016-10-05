package refinedstorage.api.autocrafting.task;

import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.api.autocrafting.ICraftingPattern;

import java.util.Deque;
import java.util.List;

public interface ICraftingTask {
    String NBT_QUANTITY = "Quantity";
    String NBT_PATTERN_ID = "PatternID";
    String NBT_PATTERN_STACK = "PatternStack";
    String NBT_PATTERN_CONTAINER = "PatternContainer";

    void calculate();

    void onCancelled();

    boolean update();

    int getQuantity();

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    ICraftingPattern getPattern();

    Deque<ItemStack> getToTake();

    Multimap<Item, ItemStack> getToCraft();

    Multimap<Item, ItemStack> getMissing();

    List<IProcessable> getToProcess();
}
