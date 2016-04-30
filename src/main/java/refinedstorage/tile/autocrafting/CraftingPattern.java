package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;

public class CraftingPattern {
    private ItemStack result;
    private ItemStack[] ingredients;
    private int speed;

    public CraftingPattern(ItemStack result, ItemStack[] ingredients, int speed) {
        this.result = result;
        this.ingredients = ingredients;
        this.speed = speed;
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    public int getSpeed() {
        return speed;
    }
}
