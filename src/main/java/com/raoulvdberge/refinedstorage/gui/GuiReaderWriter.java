package com.raoulvdberge.refinedstorage.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiReaderWriter;
import com.raoulvdberge.refinedstorage.container.ContainerReaderWriter;
import com.raoulvdberge.refinedstorage.gui.control.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;

import java.util.Collections;
import java.util.List;

public class GuiReaderWriter extends GuiBase<ContainerReaderWriter> {
    private static final int VISIBLE_ROWS = 4;

    private static final int ITEM_WIDTH = 143;
    private static final int ITEM_HEIGHT = 18;

    private List<String> channels = Collections.emptyList();
    private String currentChannelToSet;

    private Button add;
    private Button remove;
    private TextFieldWidget name;
    private IGuiReaderWriter readerWriter;

    private int itemSelected = -1;
    private int itemSelectedX = -1;
    private int itemSelectedY = -1;

    public GuiReaderWriter(ContainerReaderWriter container, IGuiReaderWriter readerWriter, PlayerInventory inventory) {
        super(container, 176, 209, inventory, null);

        this.readerWriter = readerWriter;
        this.scrollbar = new Scrollbar(157, 39, 12, 71);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, readerWriter.getRedstoneModeParameter()));

        add = addButton(x + 128, y + 15, 20, 20, "+");
        remove = addButton(x + 150, y + 15, 20, 20, "-");
        name = new TextFieldWidget(font, x + 8 + 1, y + 20 + 1, 107, font.FONT_HEIGHT, "");
        name.setEnableBackgroundDrawing(false);
        name.setVisible(true);
        name.setTextColor(16777215);
        name.setCanLoseFocus(true);
        name.setFocused2(false);
    }

    private List<String> getChannels() {
        return readerWriter.isActive() ? channels : Collections.emptyList();
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;

        // In case we get the current channel packet earlier than our channel list.
        if (currentChannelToSet != null) {
            setCurrentChannel(currentChannelToSet);
        }
    }

    @Override
    public void update(int x, int y) {
        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
            scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
        }

        if (itemSelected >= getChannels().size()) {
            itemSelected = -1;
        }
    }

    private int getRows() {
        return getChannels().size();
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/readerwriter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        if (itemSelectedX != -1 &&
            itemSelectedY != -1 &&
            itemSelected >= 0 &&
            itemSelected < getChannels().size()) {
            drawTexture(x + itemSelectedX, y + itemSelectedY, 0, 216, ITEM_WIDTH, ITEM_HEIGHT);
        }

        name.renderButton(0, 0, 0); // TODO is still needed with the new widget stuffs?
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(readerWriter.getTitle()));
        drawString(7, 115, t("container.inventory"));

        int x = 8;
        int y = 39;

        int item = scrollbar != null ? scrollbar.getOffset() : 0;

        float scale = /*TODO fontRenderer.getUnicodeFlag() ? 1F :*/ 0.5F;

        for (int i = 0; i < VISIBLE_ROWS; ++i) {
            if (item < getChannels().size()) {
                if (item == itemSelected) {
                    itemSelectedX = x;
                    itemSelectedY = y;
                }

                GlStateManager.pushMatrix();
                GlStateManager.scalef(scale, scale, 1);

                drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 7, scale), getChannels().get(item));

                GlStateManager.popMatrix();

                y += ITEM_HEIGHT;
            }

            item++;
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if (name.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if (inBounds(8, 39, 144, 73, mouseX - guiLeft, mouseY - guiTop)) {
            if (mouseButton == 0) {
                int item = scrollbar != null ? scrollbar.getOffset() : 0;

                for (int i = 0; i < VISIBLE_ROWS; ++i) {
                    int ix = 8;
                    int iy = 39 + (i * ITEM_HEIGHT);

                    if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && (item + i) < getChannels().size()) {
                        itemSelected = item + i;

                        TileDataManager.setParameter(readerWriter.getChannelParameter(), getChannels().get(itemSelected));
                    }
                }
            } else if (itemSelected != -1) {
                TileDataManager.setParameter(readerWriter.getChannelParameter(), "");
            }

            return true;
        }

        return false;
    }

    /* TODO
    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_DELETE) {
            onRemove();
        } else if (name.isFocused() && keyCode == Keyboard.KEY_RETURN) {
            onAdd();
        } else if (!checkHotbarKeys(keyCode) && name.textboxKeyTyped(character, keyCode)) {
            // NO OP
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == add) {
            onAdd();
        } else if (button == remove) {
            onRemove();
        }
    }*/

    private void onAdd() {
        String name = this.name.getText().trim();

        if (!name.isEmpty()) {
            // TODO RS.INSTANCE.network.sendToServer(new MessageReaderWriterChannelAdd(name));
        }
    }

    private void onRemove() {
        String name = this.name.getText().trim();

        if (!name.isEmpty()) {
            // TODO RS.INSTANCE.network.sendToServer(new MessageReaderWriterChannelRemove(name));
        }
    }

    public void setCurrentChannel(String channel) {
        this.itemSelected = getChannels().indexOf(channel);
        this.name.setText(itemSelected != -1 ? getChannels().get(itemSelected) : "");
        this.currentChannelToSet = channel;
    }
}
