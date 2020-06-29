package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.container.FilterContainer;
import com.refinedmods.refinedstorage.item.FilterItem;
import com.refinedmods.refinedstorage.network.FilterUpdateMessage;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.FilterTypeSideButton;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;

public class FilterScreen extends BaseScreen<FilterContainer> {
    private final ItemStack stack;

    private int compare;
    private int mode;
    private boolean modFilter;
    private final String name;
    private int type;

    private CheckboxWidget modFilterCheckBox;
    private Button modeButton;
    private TextFieldWidget nameField;

    public FilterScreen(FilterContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 231, inventory, title);

        this.stack = container.getStack();

        this.compare = FilterItem.getCompare(container.getStack());
        this.mode = FilterItem.getMode(container.getStack());
        this.modFilter = FilterItem.isModFilter(container.getStack());
        this.name = FilterItem.getName(container.getStack());
        this.type = FilterItem.getType(container.getStack());
    }

    @Override
    public void onPostInit(int x, int y) {
        addCheckBox(x + 7, y + 77, I18n.format("gui.refinedstorage.filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT, btn -> {
            compare ^= IComparer.COMPARE_NBT;

            sendUpdate();
        });

        modFilterCheckBox = addCheckBox(0, y + 71 + 25, I18n.format("gui.refinedstorage.filter.mod_filter"), modFilter, btn -> {
            modFilter = !modFilter;

            sendUpdate();
        });

        modeButton = addButton(x + 7, y + 71 + 21, 0, 20, "", true, true, btn -> {
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
        nameField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        nameField.setResponder(name -> sendUpdate());

        addButton(nameField);

        addSideButton(new FilterTypeSideButton(this));
    }

    private void updateModeButton(int mode) {
        String text = mode == IFilter.MODE_WHITELIST ? I18n.format("sidebutton.refinedstorage.mode.whitelist") : I18n.format("sidebutton.refinedstorage.mode.blacklist");

        modeButton.setWidth(font.getStringWidth(text) + 12);
        modeButton.setMessage(text);
        modFilterCheckBox.x = modeButton.x + modeButton.getWidth() + 4;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeScreen();

            return true;
        }

        if (nameField.keyPressed(key, scanCode, modifiers) || nameField.canWrite()) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
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
        RS.NETWORK_HANDLER.sendToServer(new FilterUpdateMessage(compare, mode, modFilter, nameField.getText(), type));
    }
}
