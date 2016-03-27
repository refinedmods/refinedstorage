package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;

public interface ISoldererRecipe {
    ItemStack getRow(int row);

    ItemStack getResult();

    int getDuration();
}
