package refinedstorage.gui.grid;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;
import refinedstorage.RefinedStorage;
import refinedstorage.apiimpl.network.GridHandler;
import refinedstorage.container.ContainerCraftingSettings;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageGridCraftingStart;

import java.io.IOException;

public class GuiCraftingSettings extends GuiBase {
    private static final int DEFAULT_AMOUNT = 1;

    private GuiTextField amountField;
    private GuiGrid gui;
    private ClientStack stack;
    private GuiButton startButton;
    private GuiButton cancelButton;
    private GuiButton[] incrementButtons = new GuiButton[6];

    public GuiCraftingSettings(GuiGrid gui, EntityPlayer player, ClientStack stack) {
        super(new ContainerCraftingSettings(player, stack.getStack()), 172, 99);

        this.gui = gui;
        this.stack = stack;
    }

    @Override
    public void init(int x, int y) {
        startButton = addButton(x + 114, y + 33, 50, 20, t("misc.refinedstorage:start"));
        cancelButton = addButton(x + 114, y + 57, 50, 20, t("gui.cancel"));

        amountField = new GuiTextField(0, fontRendererObj, x + 7 + 1, y + 50 + 1, 69 - 6, fontRendererObj.FONT_HEIGHT);
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setText(String.valueOf(DEFAULT_AMOUNT));
        amountField.setTextColor(16777215);
        amountField.setCanLoseFocus(false);
        amountField.setFocused(true);

        int[] increments = new int[]{
            1, 10, 64,
            -1, -10, -64
        };

        for (int i = 0; i < 3; ++i) {
            incrementButtons[i] = addButton(x + 6 + (i * (30 + 3)), y + 20, 30, 20, "+" + increments[i]);
        }

        for (int i = 0; i < 3; ++i) {
            incrementButtons[3 + i] = addButton(x + 6 + (i * (30 + 3)), y + 72, 30, 20, String.valueOf(increments[3 + i]));
        }
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
        drawString(7, 7, t("container.crafting"));
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
        } else if (button.id == cancelButton.id) {
            close();
        } else {
            for (GuiButton incrementButton : incrementButtons) {
                if (incrementButton.id == button.id) {
                    Integer oldAmount = Ints.tryParse(amountField.getText());

                    if (oldAmount == null) {
                        oldAmount = 0;
                    }

                    int newAmount = Integer.parseInt(incrementButton.displayString);

                    newAmount = Math.min(Math.max(DEFAULT_AMOUNT, oldAmount + newAmount), GridHandler.MAX_CRAFTING_PER_REQUEST);

                    amountField.setText(String.valueOf(newAmount));

                    break;
                }
            }
        }
    }

    private void startRequest() {
        Integer quantity = Ints.tryParse(amountField.getText());

        if (quantity != null && quantity > 0 && quantity <= GridHandler.MAX_CRAFTING_PER_REQUEST) {
            RefinedStorage.INSTANCE.network.sendToServer(new MessageGridCraftingStart(stack.getId(), quantity));

            close();
        }
    }

    private void close() {
        FMLClientHandler.instance().showGuiScreen(gui);
    }
}
