package refinedstorage.util;

import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class NBTUtils {
    public static void writeBoolArray(NBTTagCompound tag, String name, boolean[] arr) {
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < arr.length; ++i) {
            list.appendTag(new NBTTagByte(arr[i] ? (byte) 1 : (byte) 0));
        }

        tag.setTag(name, list);
    }

    public static boolean[] readBoolArray(NBTTagCompound tag, String name) {
        NBTTagList list = tag.getTagList(name, Constants.NBT.TAG_COMPOUND);

        boolean[] arr = new boolean[list.tagCount()];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = ((NBTTagByte) list.get(i)).getByte() == 1 ? true : false;
        }

        return arr;
    }
}
