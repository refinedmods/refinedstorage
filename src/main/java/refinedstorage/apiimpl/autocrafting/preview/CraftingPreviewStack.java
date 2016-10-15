package refinedstorage.apiimpl.autocrafting.preview;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class CraftingPreviewStack {
    private ItemStack stack;
    private int available;
    private boolean missing;
    private int toCraft;
    // if missing is true then toCraft is the missing amount

    public CraftingPreviewStack(ItemStack stack) {
        this.stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
    }

    public CraftingPreviewStack(ItemStack stack, int available, boolean missing, int toCraft) {
        this.stack = stack;
        this.available = available;
        this.missing = missing;
        this.toCraft = toCraft;
    }

    public void writeToByteBuf(ByteBuf buf) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.getMetadata());
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    public static CraftingPreviewStack fromByteBuf(ByteBuf buf) {
        Item item = Item.getItemById(buf.readInt());
        int meta = buf.readInt();
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        return new CraftingPreviewStack(new ItemStack(item, 1, meta), available, missing, toCraft);
    }

    public ItemStack getStack() {
        return stack;
    }

    public void addAvailable(int amount) {
        this.available += amount;
    }

    public int getAvailable() {
        return available;
    }

    public void addToCraft(int amount) {
        this.toCraft += amount;
    }

    public int getToCraft() {
        return this.toCraft;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public boolean hasMissing() {
        return missing;
    }
}
