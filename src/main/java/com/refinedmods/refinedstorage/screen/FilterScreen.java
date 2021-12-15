package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.container.FilterContainerMenu;
import com.refinedmods.refinedstorage.item.FilterItem;
import com.refinedmods.refinedstorage.network.FilterUpdateMessage;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.FilterTypeSideButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class FilterScreen extends BaseScreen<FilterContainerMenu> {
    private final ItemStack stack;
    private final String name;
    private int compare;
    private int mode;
    private boolean modFilter;
    private int type;

    private CheckboxWidget modFilterCheckBox;
    private Button modeButton;
    private EditBox nameField;

    public FilterScreen(FilterContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, 176, 231, inventory, title);

        this.stack = containerMenu.getFilterItem();

        this.compare = FilterItem.getCompare(containerMenu.getFilterItem());
        this.mode = FilterItem.getMode(containerMenu.getFilterItem());
        this.modFilter = FilterItem.isModFilter(containerMenu.getFilterItem());
        this.name = FilterItem.getFilterName(containerMenu.getFilterItem());
        this.type = FilterItem.getType(containerMenu.getFilterItem());
    }

    @Override
    public void onPostInit(int x, int y) {
        addCheckBox(x + 7, y + 77, new TranslatableComponent("gui.refinedstorage.filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT, btn -> {
            compare ^= IComparer.COMPARE_NBT;

            sendUpdate();
        });

        modFilterCheckBox = addCheckBox(0, y + 71 + 25, new TranslatableComponent("gui.refinedstorage.filter.mod_filter"), modFilter, btn -> {
            modFilter = !modFilter;

            sendUpdate();
        });

        modeButton = addButton(x + 7, y + 71 + 21, 0, 20, new TextComponent(""), true, true, btn -> {
            mode = mode == IFilter.MODE_WHITELIST ? IFilter.MODE_BLACKLIST : IFilter.MODE_WHITELIST;

            updateModeButton(mode);

            sendUpdate();
        });

        updateModeButton(mode);

        nameField = new EditBox(font, x + 34, y + 121, 137 - 6, font.lineHeight, new TextComponent(""));
        nameField.setValue(name);
        nameField.setBordered(false);
        nameField.setVisible(true);
        nameField.setCanLoseFocus(true);
        nameField.setFocus(false);
        nameField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
        nameField.setResponder(content -> sendUpdate());

        addRenderableWidget(nameField);

        addSideButton(new FilterTypeSideButton(this));
    }

    private void updateModeButton(int mode) {
        Component text = mode == IFilter.MODE_WHITELIST
            ? new TranslatableComponent("sidebutton.refinedstorage.mode.whitelist")
            : new TranslatableComponent("sidebutton.refinedstorage.mode.blacklist");

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
    public void renderBackground(PoseStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/filter.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack matrixStack, int mouseX, int mouseY) {
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
