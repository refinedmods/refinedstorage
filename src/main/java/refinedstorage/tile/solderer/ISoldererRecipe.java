package refinedstorage.tile.solderer;

import net.minecraft.item.ItemStack;

public interface ISoldererRecipe {
    public ItemStack getRow(int row);

    public ItemStack getResult();

    public int getDuration();
}
