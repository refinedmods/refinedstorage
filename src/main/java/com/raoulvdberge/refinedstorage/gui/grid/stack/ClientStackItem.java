package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import io.netty.buffer.ByteBuf;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.List;

public class ClientStackItem implements IClientStack {
    private int hash;
    private ItemStack stack;
    private boolean craftable;
    private boolean outputFromPattern;

    public ClientStackItem(ByteBuf buf) {
        stack = new ItemStack(Item.getItemById(buf.readInt()), buf.readInt(), buf.readInt());
        stack.setTagCompound(ByteBufUtils.readTag(buf));
        hash = buf.readInt();
        craftable = buf.readBoolean();
        setOutputFromPattern(buf.readBoolean());
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public boolean isOutputFromPattern() {
        return outputFromPattern;
    }

    public void setOutputFromPattern(boolean outputFromPattern) {
        this.outputFromPattern = outputFromPattern;

        if (outputFromPattern) {
            stack.setCount(1);
        }
    }

    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public String getName() {
        return stack.getDisplayName();
    }

    @Override
    public String getModId() {
        return Item.REGISTRY.getNameForObject(stack.getItem()).getResourceDomain();
    }

    @Override
    public String getTooltip() {
        List<String> lines = stack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

        // From GuiScreen#renderToolTip
        for (int i = 0; i < lines.size(); ++i) {
            if (i == 0) {
                lines.set(i, stack.getRarity().rarityColor + lines.get(i));
            } else {
                lines.set(i, TextFormatting.GRAY + lines.get(i));
            }
        }

        return Strings.join(lines, "\n");
    }

    @Override
    public int getQuantity() {
        return stack.getCount();
    }

    private String getQuantityForDisplay(boolean advanced) {
        int qty = stack.getCount();

        if (outputFromPattern) {
            return I18n.format("gui.refinedstorage:grid.craft");
        } else if (advanced && qty > 1) {
            return String.valueOf(qty);
        } else if (qty == 1) {
            return null;
        }

        return RSUtils.formatQuantity(qty);
    }

    @Override
    public void draw(GuiBase gui, int x, int y, boolean isOverWithShift) {
        gui.drawItem(x, y, stack, true, getQuantityForDisplay(isOverWithShift));
    }

    @Override
    public Object getIngredient() {
        return stack;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IClientStack && ((ClientStackItem) obj).getHash() == hash;
    }
}
