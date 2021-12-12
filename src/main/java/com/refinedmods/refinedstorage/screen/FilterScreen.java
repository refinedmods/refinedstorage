package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

        this.stack = container.getFilterItem();

        this.compare = FilterItem.getCompare(container.getFilterItem());
        this.mode = FilterItem.getMode(container.getFilterItem());
        this.modFilter = FilterItem.isModFilter(container.getFilterItem());
        this.name = FilterItem.getFilterName(container.getFilterItem());
        this.type = FilterItem.getType(container.getFilterItem());
    }

    @Override
    public void onPostInit(int x, int y) {
        addCheckBox(x + 7, y + 77, new TranslationTextComponent("gui.refinedstorage.filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT, btn -> {
            compare ^= IComparer.COMPARE_NBT;

            sendUpdate();
        });

        modFilterCheckBox = addCheckBox(0, y + 71 + 25, new TranslationTextComponent("gui.refinedstorage.filter.mod_filter"), modFilter, btn -> {
            modFilter = !modFilter;

            sendUpdate();
        });

        modeButton = addButton(x + 7, y + 71 + 21, 0, 20, new StringTextComponent(""), true, true, btn -> {
            mode = mode == IFilter.MODE_WHITELIST ? IFilter.MODE_BLACKLIST : IFilter.MODE_WHITELIST;

            updateModeButton(mode);

            sendUpdate();
        });

        updateModeButton(mode);

        nameField = new TextFieldWidget(font, x + 34, y + 121, 137 - 6, font.lineHeight, new StringTextComponent(""));
        nameField.setValue(name);
        nameField.setBordered(false);
        nameField.setVisible(true);
        nameField.setCanLoseFocus(true);
        nameField.setFocus(false);
        nameField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        nameField.setResponder(content -> sendUpdate());

        addButton(nameField);

        addSideButton(new FilterTypeSideButton(this));
    }

    private void updateModeButton(int mode) {
        ITextComponent text = mode == IFilter.MODE_WHITELIST
            ? new TranslationTextComponent("sidebutton.refinedstorage.mode.whitelist")
            : new TranslationTextComponent("sidebutton.refinedstorage.mode.blacklist");

        modeButton.setWidth(font.width(text.getString()) + 12);
        modeButton.setMessage(text);
        modFilterCheckBox.x = modeButton.x + modeButton.getWidth() + 4;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeContainer();

            return true;
        }

        if (nameField.keyPressed(key, scanCode, modifiers) || nameField.canConsumeInput()) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/filter.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 137, I18n.get("container.inventory"));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;

        FilterItem.setType(stack, type);
    }

    public void sendUpdate() {
        RS.NETWORK_HANDLER.sendToServer(new FilterUpdateMessage(compare, mode, modFilter, nameField.getValue(), type));
    }
}
