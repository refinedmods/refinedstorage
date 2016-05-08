package refinedstorage.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;

public class NBTUtils {
    public static void writeBoolArray(NBTTagCompound tag, String name, boolean[] arr) {
        int[] intArr = new int[arr.length];

        for (int i = 0; i < intArr.length; ++i) {
            intArr[i] = arr[i] ? 1 : 0;
        }

        tag.setTag(name, new NBTTagIntArray(intArr));
    }

    public static boolean[] readBoolArray(NBTTagCompound tag, String name) {
        int[] intArr = tag.getIntArray(name);

        boolean arr[] = new boolean[intArr.length];

        for (int i = 0; i < intArr.length; ++i) {
            arr[i] = intArr[i] == 1 ? true : false;
        }

        return arr;
    }
}
