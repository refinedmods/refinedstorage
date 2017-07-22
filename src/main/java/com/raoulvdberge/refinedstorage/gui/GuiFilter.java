package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.container.ContainerFilter;
import com.raoulvdberge.refinedstorage.integration.forestry.IntegrationForestry;
import com.raoulvdberge.refinedstorage.item.ItemFilter;
import com.raoulvdberge.refinedstorage.network.MessageFilterUpdate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;

public class GuiFilter extends GuiBase {
    private int compare;
    private int mode;
    private boolean modFilter;
    private String name;

    private GuiCheckBox compareDamage;
    private GuiCheckBox compareNBT;
    private GuiCheckBox compareOredict;
    private GuiCheckBox compareForestry;
    private GuiCheckBox toggleModFilter;
    private GuiButton toggleMode;
    private GuiTextField nameField;

    public GuiFilter(ContainerFilter container) {
        super(container, 176, 231);

        this.compare = ItemFilter.getCompare(container.getStack());
        this.mode = ItemFilter.getMode(container.getStack());
        this.modFilter = ItemFilter.isModFilter(container.getStack());
        this.name = ItemFilter.getName(container.getStack());
    }

    @Override
    public void init(int x, int y) {
        compareDamage = addCheckBox(x + 7, y + 77, t("gui.refinedstorage:filter.compare_damage"), (compare & IComparer.COMPARE_DAMAGE) == IComparer.COMPARE_DAMAGE);
        compareNBT = addCheckBox(x + 7 + compareDamage.getButtonWidth() + 4, y + 77, t("gui.refinedstorage:filter.compare_nbt"), (compare & IComparer.COMPARE_NBT) == IComparer.COMPARE_NBT);
        compareOredict = addCheckBox(x + 7 + compareDamage.getButtonWidth() + 4 + compareNBT.getButtonWidth() + 4, y + 77, t("gui.refinedstorage:filter.compare_oredict"), (compare & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT);
        if(IntegrationForestry.isLoaded()) {
            compareForestry = addCheckBox(0, y + 71 + 34, t("gui.refinedstorage:filter.compare_forestry"), (compare & IComparer.COMPARE_FORESTRY) == IComparer.COMPARE_FORESTRY);
            toggleModFilter = addCheckBox(0, y + 71 + 21, t("gui.refinedstorage:filter.mod_filter"), modFilter);
        } else {
            toggleModFilter = addCheckBox(0, y + 71 + 25, t("gui.refinedstorage:filter.mod_filter"), modFilter);
		}
        toggleMode = addButton(x + 7, y + 71 + 21, 0, 20, "");
        updateModeButton(mode);
        nameField = new GuiTextField(0, fontRenderer, x + 34, y + 121, 137 - 6, fontRenderer.FONT_HEIGHT);
        nameField.setText(name);
        nameField.setEnableBackgroundDrawing(false);
        nameField.setVisible(true);
        nameField.setCanLoseFocus(true);
        nameField.setFocused(false);
        nameField.setTextColor(16777215);
    }

    private void updateModeButton(int mode) {
        String text = mode == IFilter.MODE_WHITELIST ? t("sidebutton.refinedstorage:mode.whitelist") : t("sidebutton.refinedstorage:mode.blacklist");

        toggleMode.setWidth(fontRenderer.getStringWidth(text) + 12);
        toggleMode.displayString = text;
        if(IntegrationForestry.isLoaded()) {
            compareForestry.x = toggleMode.x + toggleMode.getButtonWidth() + 4;
        }
        toggleModFilter.x = toggleMode.x + toggleMode.getButtonWidth() + 4;
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/filter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        nameField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:filter"));
        drawString(7, 137, t("container.inventory"));
    }

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
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == compareDamage) {
            compare ^= IComparer.COMPARE_DAMAGE;
        } else if (button == compareNBT) {
            compare ^= IComparer.COMPARE_NBT;
        } else if (button == compareOredict) {
            compare ^= IComparer.COMPARE_OREDICT;
        } else if (button == compareForestry) {
            compare ^= (IComparer.COMPARE_FORESTRY | IntegrationForestry.Tag.GEN.getFlag() | IntegrationForestry.Tag.IS_ANALYZED.getFlag());
        } else if (button == toggleMode) {
            mode = mode == IFilter.MODE_WHITELIST ? IFilter.MODE_BLACKLIST : IFilter.MODE_WHITELIST;

            updateModeButton(mode);
        } else if (button == toggleModFilter) {
            modFilter = !modFilter;
        }

        sendUpdate();
    }

    private void sendUpdate() {
        RS.INSTANCE.network.sendToServer(new MessageFilterUpdate(compare, mode, modFilter, nameField.getText()));
    }
}
