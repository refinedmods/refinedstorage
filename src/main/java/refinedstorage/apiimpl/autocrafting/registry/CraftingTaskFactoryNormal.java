package refinedstorage.apiimpl.autocrafting.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.task.CraftingTask;
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
    public ICraftingTask create(World world, @Nullable NBTTagCompound tag, ICraftingPattern pattern) {
        CraftingTaskNormal task = new CraftingTaskNormal(pattern);

        if (tag != null) {
            task.setChildrenCreated(CraftingTask.readBooleanArray(tag, CraftingTask.NBT_CHILDREN_CREATED));
            task.setSatisfied(CraftingTask.readBooleanArray(tag, CraftingTask.NBT_SATISFIED));
            task.setChecked(CraftingTask.readBooleanArray(tag, CraftingTask.NBT_CHECKED));

            List<ItemStack> took = new ArrayList<>();

            NBTTagList tookTag = tag.getTagList(CraftingTask.NBT_TOOK, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tookTag.tagCount(); ++i) {
                ItemStack stack = ItemStack.loadItemStackFromNBT(tookTag.getCompoundTagAt(i));

                if (stack != null) {
                    took.add(stack);
                }
            }

            task.setTook(took);

            for (int i = 0; i < 9; ++i) {
                String id = String.format(CraftingTaskNormal.NBT_TOOK_SLOT, i);

                if (tag.hasKey(id)) {
                    task.getTookSlots()[i] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(id));
                }
            }

            task.readChildNBT(world, tag);
        }

        return task;
    }
}