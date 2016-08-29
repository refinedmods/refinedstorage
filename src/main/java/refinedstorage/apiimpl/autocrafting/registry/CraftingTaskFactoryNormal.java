package refinedstorage.apiimpl.autocrafting.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.apiimpl.autocrafting.task.CraftingTaskNormal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftingTaskFactoryNormal implements ICraftingTaskFactory {
    public static final String ID = "normal";

    @Override
    @Nonnull
    public ICraftingTask create(@Nullable NBTTagCompound tag, ICraftingPattern pattern) {
        CraftingTaskNormal task = new CraftingTaskNormal(pattern);

        if (tag != null) {
            task.setChildrenCreated(CraftingTaskNormal.readBooleanArray(tag, CraftingTaskNormal.NBT_CHILDREN));
            task.setSatisfied(CraftingTaskNormal.readBooleanArray(tag, CraftingTaskNormal.NBT_SATISFIED));
            task.setChecked(CraftingTaskNormal.readBooleanArray(tag, CraftingTaskNormal.NBT_CHECKED));

            List<ItemStack> took = new ArrayList<>();

            NBTTagList tookTag = tag.getTagList(CraftingTaskNormal.NBT_TOOK, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tookTag.tagCount(); ++i) {
                ItemStack stack = ItemStack.loadItemStackFromNBT(tookTag.getCompoundTagAt(i));

                if (stack != null) {
                    took.add(stack);
                }
            }

            task.setTook(took);

            task.readChildNBT(tag);
        }

        return task;
    }
}