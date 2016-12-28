package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerReaderWriter;
import com.raoulvdberge.refinedstorage.network.MessageReaderWriterChannelAdd;
import com.raoulvdberge.refinedstorage.network.MessageReaderWriterChannelRemove;
import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GuiReaderWriter extends GuiBase {
    public static List<String> CHANNELS = Collections.emptyList();

    private static final int VISIBLE_ROWS = 4;

    private static final int ITEM_WIDTH = 143;
    private static final int ITEM_HEIGHT = 18;

    private GuiButton add;
    private GuiButton remove;
    private GuiTextField name;
    private IReaderWriter readerWriter;

    private int itemSelected = -1;

    private int itemSelectedX = -1;
    private int itemSelectedY = -1;

    public GuiReaderWriter(ContainerReaderWriter container, IReaderWriter readerWriter) {
        super(container, 176, 209);

        this.readerWriter = readerWriter;
        this.scrollbar = new Scrollbar(157, 39, 12, 71);
    }

    @Override
    public void init(int x, int y) {
        add = addButton(x + 128, y + 15, 20, 20, "+");
        remove = addButton(x + 150, y + 15, 20, 20, "-");
        name = new GuiTextField(0, fontRendererObj, x + 8 + 1, y + 20 + 1, 107, fontRendererObj.FONT_HEIGHT);
        name.setEnableBackgroundDrawing(false);
        name.setVisible(true);
        name.setTextColor(16777215);
        name.setCanLoseFocus(true);
        name.setFocused(false);

        updateSelection(readerWriter.getChannelParameter().getValue());
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);

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

        name.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(readerWriter.getTitle()));
        drawString(7, 115, t("container.inventory"));

        int x = 8;
        int y = 39;

        int item = scrollbar.getOffset();

        for (int i = 0; i < VISIBLE_ROWS; ++i) {
            if (item < getChannels().size()) {
                if (item == itemSelected) {
                    itemSelectedX = x;
                    itemSelectedY = y;
                }

                float scale = 0.5f;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);

                drawString(calculateOffsetOnScale(x + 5, scale), calculateOffsetOnScale(y + 7, scale), getChannels().get(item));

                GlStateManager.popMatrix();

                y += ITEM_HEIGHT;
            }

            item++;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        name.mouseClicked(mouseX, mouseY, mouseButton);

        int itemSelectedOld = itemSelected;

        itemSelected = -1;

        if (mouseButton == 0 && inBounds(8, 39, 144, 73, mouseX - guiLeft, mouseY - guiTop)) {
            int item = scrollbar.getOffset();

            for (int i = 0; i < VISIBLE_ROWS; ++i) {
                int ix = 8;
                int iy = 39 + (i * ITEM_HEIGHT);

                if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && (item + i) < getChannels().size()) {
                    itemSelected = item + i;

                    TileDataManager.setParameter(readerWriter.getChannelParameter(), getChannels().get(itemSelected));
                }
            }
        }

        if (itemSelectedOld != -1 && itemSelected == -1) {
            TileDataManager.setParameter(readerWriter.getChannelParameter(), "");
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_DELETE) {
            sendRemove();
        } else if (name.isFocused() && keyCode == Keyboard.KEY_RETURN) {
            sendAdd();
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
            sendAdd();
        } else if (button == remove) {
            sendRemove();
        }
    }

    private void sendAdd() {
        String name = this.name.getText().trim();

        if (!name.isEmpty()) {
            RS.INSTANCE.network.sendToServer(new MessageReaderWriterChannelAdd(name));
        }
    }

    private void sendRemove() {
        String name = this.name.getText().trim();

        if (!name.isEmpty()) {
            RS.INSTANCE.network.sendToServer(new MessageReaderWriterChannelRemove(name));
        }
    }

    public void updateSelection(String channel) {
        this.itemSelected = getChannels().indexOf(channel);
        this.name.setText(itemSelected != -1 ? getChannels().get(itemSelected) : "");
    }

    private List<String> getChannels() {
        return readerWriter.canUpdate() ? CHANNELS : Collections.emptyList();
    }
}
