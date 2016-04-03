package refinedstorage.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBase extends ItemBlock {
    public ItemBlockBase(Block block, boolean subtypes) {
        super(block);

        setRegistryName(block.getRegistryName());

        if (subtypes) {
            setMaxDamage(0);
            setHasSubtypes(true);
        }
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes()) {
            return getUnlocalizedName() + "." + stack.getItemDamage();
        }

        return getUnlocalizedName();
    }
}
