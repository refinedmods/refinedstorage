package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class CraftingMonitorElementRoot extends CraftingMonitorElementItemRender {
    public static final String ID = "root";

    private int id;
    private ItemStack output;
    private int quantity;

    public CraftingMonitorElementRoot(int id, ItemStack output, int quantity) {
        this.id = id;
        this.output = output;
        this.quantity = quantity;
    }

    @Override
    public int getTaskId() {
        return id;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(id);
        ByteBufUtils.writeItemStack(buf, output);
        buf.writeInt(quantity);
    }

    @Override
    protected ItemStack getItem() {
        return output;
    }

    @Override
    protected int getQuantity() {
        return quantity;
    }
}
