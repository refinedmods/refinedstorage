package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.container.ContainerFilter;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonFilterType;
import com.raoulvdberge.refinedstorage.item.FilterItem;
import com.raoulvdberge.refinedstorage.network.MessageFilterUpdate;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiFilter extends GuiBase<ContainerFilter> {
    private ItemStack stack;

    private int compare;
    private int mode;
    private boolean modFilter;
    private String name;
    private int type;

    private GuiCheckBox toggleModFilter;
    private Button toggleMode;
    private TextFieldWidget nameField;

    public GuiFilter(ContainerFilter container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 231, inventory, title);

        this.stack = container.getStack();

        this.compare = FilterItem.getCompare(container.getStack());
        this.mode = FilterItem.getMode(container.getStack());
        this.modFilter = FilterItem.isModFilter(container.getStack());
        this.name = FilterItem.getName(container.getStack());
        this.type = FilterItem.getType(container.getStack());
    }

    @Override
    public void init(int x, int y) {
        addCheckBox(x + 7, y + 77, I18n.format("gui.refinedstorage.filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT, btn -> {
            compare ^= IComparer.COMPARE_NBT;

            sendUpdate();
        });

        toggleModFilter = addCheckBox(0, y + 71 + 25, I18n.format("gui.refinedstorage.filter.mod_filter"), modFilter, btn -> {
            modFilter = !modFilter;

            sendUpdate();
        });

        toggleMode = addButton(x + 7, y + 71 + 21, 0, 20, "", true, true, btn -> {
            mode = mode == IFilter.MODE_WHITELIST ? IFilter.MODE_BLACKLIST : IFilter.MODE_WHITELIST;

            updateModeButton(mode);

            sendUpdate();
        });

        updateModeButton(mode);

        nameField = new TextFieldWidget(font, x + 34, y + 121, 137 - 6, font.FONT_HEIGHT, "");
        nameField.setText(name);
        nameField.setEnableBackgroundDrawing(false);
        nameField.setVisible(true);
        nameField.setCanLoseFocus(true);
        nameField.setFocused2(false);
        nameField.setTextColor(16777215);
        nameField.func_212954_a(name -> sendUpdate());

        addButton(nameField);

        addSideButton(new SideButtonFilterType(this));
    }

    private void updateModeButton(int mode) {
        String text = mode == IFilter.MODE_WHITELIST ? I18n.format("sidebutton.refinedstorage.mode.whitelist") : I18n.format("sidebutton.refinedstorage.mode.blacklist");

        toggleMode.setWidth(font.getStringWidth(text) + 12);
        toggleMode.setMessage(text);
        toggleModFilter.x = toggleMode.x + toggleMode.getWidth() + 4;
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/filter.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());
        renderString(7, 137, I18n.format("container.inventory"));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;

        FilterItem.setType(stack, type);
    }

    public void sendUpdate() {
        RS.NETWORK_HANDLER.sendToServer(new MessageFilterUpdate(compare, mode, modFilter, nameField.getText(), type));
    }
}
