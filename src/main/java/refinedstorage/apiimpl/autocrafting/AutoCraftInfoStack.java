package refinedstorage.apiimpl.autocrafting;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoCraftInfoStack {
    private ItemStack stack;
    private int needed;
    private int stock;
    private int extras;
    private boolean cantCraft;

    public AutoCraftInfoStack(ItemStack stack, int needed, int stock) {
        this.stack = stack;
        this.needed = needed;
        this.stock = stock;
        this.extras = 0;
        this.cantCraft = false;
    }

    public void writeToByteBuf(ByteBuf buf) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.getMetadata());
        buf.writeInt(needed);
        buf.writeInt(stock);
        buf.writeInt(extras);
        buf.writeBoolean(cantCraft);
    }

    public static AutoCraftInfoStack fromByteBuf(ByteBuf buf) {
        Item item = Item.getItemById(buf.readInt());
        int meta = buf.readInt();
        int toCraft = buf.readInt();
        int available = buf.readInt();
        AutoCraftInfoStack stack = new AutoCraftInfoStack(new ItemStack(item, 1, meta), toCraft, available);
        stack.extras = buf.readInt();
        stack.cantCraft = buf.readBoolean();
        return stack;
    }

    public void addNeeded(int quantity) {
        this.needed += quantity;
    }

    public void addExtras(int quantity) {
        this.extras += quantity;
    }

    public int getAvailable() {
        return this.stock + this.extras - this.needed;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getStock() {
        return stock;
    }

    public int getNeeded() {
        return needed;
    }

    public boolean needsCrafting() {
        return this.needed > this.stock;
    }

    public boolean cantCraft() {
        return this.cantCraft;
    }

    public int getToCraft() {
        return this.needed - this.stock;
    }

    public void setCantCraft(boolean cantCraft) {
        this.cantCraft = cantCraft;
    }

    @Override
    public String toString() {
        return stack.toString() + ", needed=" + needed + ", stock=" + stock + ", extras=" + extras + ", canCraft=" + !cantCraft;
    }


}
