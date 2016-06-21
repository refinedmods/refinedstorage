package refinedstorage.gui;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerDummy;
import refinedstorage.network.MessageGridCraftingStart;
import refinedstorage.tile.controller.StorageHandler;

import java.io.IOException;

public class GuiCraftingSettings extends GuiBase {
    private GuiTextField amountField;
    private GuiGrid gridGui;
    private ItemStack stack;
    private GuiButton startButton;

    public GuiCraftingSettings(GuiGrid gridGui, ItemStack stack) {
        super(new ContainerDummy(), 143, 61);

        this.gridGui = gridGui;
        this.stack = stack;
    }

    @Override
    public void init(int x, int y) {
        startButton = addButton(x + 48, y + 35, 50, 20, t("misc.refinedstorage:start"));

        amountField = new GuiTextField(0, fontRendererObj, x + 39 + 1, y + 21 + 1, 69 - 6, fontRendererObj.FONT_HEIGHT);
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setText("1");
        amountField.setTextColor(16777215);
        amountField.setCanLoseFocus(false);
        amountField.setFocused(true);
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_settings.png");

        drawTexture(x, y, 0, 0, width, height);

        amountField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString((width - fontRendererObj.getStringWidth(t("container.crafting"))) / 2, 8, t("container.crafting"));
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && amountField.textboxKeyTyped(character, keyCode)) {
            // NO OP
        } else {
            if (keyCode == Keyboard.KEY_RETURN) {
                startRequest();
            } else if (keyCode == Keyboard.KEY_ESCAPE) {
                close();
            } else {
                super.keyTyped(character, keyCode);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == startButton.id) {
            startRequest();
        }
    }

    private void startRequest() {
        Integer quantity = Ints.tryParse(amountField.getText());

        if (quantity != null && quantity > 0 && quantity <= StorageHandler.MAX_CRAFTING_PER_REQUEST) {
            RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingStart(stack, quantity));

            close();
        }
    }

    private void close() {
        FMLClientHandler.instance().showGuiScreen(gridGui);
    }
}
