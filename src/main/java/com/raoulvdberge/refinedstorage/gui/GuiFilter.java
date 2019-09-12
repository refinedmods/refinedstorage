package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.container.ContainerFilter;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonFilterType;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiFilter extends GuiBase<ContainerFilter> {
    private ItemStack stack;

    private int compare;
    private int mode;
    private boolean modFilter;
    private String name;
    private int type;

    private GuiCheckBox compareNbt;
    private GuiCheckBox toggleModFilter;
    private Button toggleMode;
    private TextFieldWidget nameField;

    public GuiFilter(ContainerFilter container, PlayerInventory inventory) {
        super(container, 176, 231, inventory, null);

        this.stack = container.getStack();

        /* TODO this.compare = ItemFilter.getCompare(container.getStack());
        this.mode = ItemFilter.getMode(container.getStack());
        this.modFilter = ItemFilter.isModFilter(container.getStack());
        this.name = ItemFilter.getName(container.getStack());
        this.type = ItemFilter.getType(container.getStack());*/
    }

    @Override
    public void init(int x, int y) {
        compareNbt = addCheckBox(x + 7, y + 77, t("gui.refinedstorage:filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT);

        toggleModFilter = addCheckBox(0, y + 71 + 25, t("gui.refinedstorage:filter.mod_filter"), modFilter);
        toggleMode = addButton(x + 7, y + 71 + 21, 0, 20, "");

        updateModeButton(mode);

        nameField = new TextFieldWidget(font, x + 34, y + 121, 137 - 6, font.FONT_HEIGHT, "");
        nameField.setText(name);
        nameField.setEnableBackgroundDrawing(false);
        nameField.setVisible(true);
        nameField.setCanLoseFocus(true);
        nameField.setFocused2(false);
        nameField.setTextColor(16777215);

        addSideButton(new SideButtonFilterType(this));
    }

    private void updateModeButton(int mode) {
        String text = mode == IFilter.MODE_WHITELIST ? t("sidebutton.refinedstorage:mode.whitelist") : t("sidebutton.refinedstorage:mode.blacklist");

        toggleMode.setWidth(font.getStringWidth(text) + 12);
        toggleMode.setMessage(text);
        toggleModFilter.x = toggleMode.x + toggleMode.getWidth() + 4;
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/filter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        nameField.renderButton(0, 0, 0);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:filter"));
        drawString(7, 137, t("container.inventory"));
    }

    /* TODO
    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && nameField.textboxKeyTyped(character, keyCode)) {
            sendUpdate();
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        nameField.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    protected void actionPerformed(Button button) throws IOException {
        super.actionPerformed(button);

        if (button == compareDamage) {
            compare ^= IComparer.COMPARE_DAMAGE;
        } else if (button == compareNbt) {
            compare ^= IComparer.COMPARE_NBT;
        } else if (button == toggleMode) {
            mode = mode == IFilter.MODE_WHITELIST ? IFilter.MODE_BLACKLIST : IFilter.MODE_WHITELIST;

            updateModeButton(mode);
        } else if (button == toggleModFilter) {
            modFilter = !modFilter;
        }

        sendUpdate();
    }*/

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;

        // TODO ItemFilter.setType(stack, type);
    }

    public void sendUpdate() {
        // TODO RS.INSTANCE.network.sendToServer(new MessageFilterUpdate(compare, mode, modFilter, nameField.getText(), type));
    }
}
