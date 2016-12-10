package com.raoulvdberge.refinedstorage.gui.grid;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingSettings;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.stack.ClientStackItem;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreview;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiCraftingStart extends GuiBase {
    private static final int DEFAULT_AMOUNT = 1;

    protected GuiTextField amountField;
    private GuiBase parent;
    private ClientStackItem stack;
    private GuiButton startButton;
    private GuiButton cancelButton;
    private GuiButton[] incrementButtons = new GuiButton[6];

    public GuiCraftingStart(GuiBase parent, ClientStackItem stack, Container container, int w, int h) {
        super(container, w, h);

        this.parent = parent;
        this.stack = stack;
    }

    public GuiCraftingStart(GuiGrid parent, EntityPlayer player, ClientStackItem stack) {
        this(parent, stack, new ContainerCraftingSettings(player, stack.getStack()), 172, 99);
    }

    protected String getStartButtonText() {
        return t("misc.refinedstorage:start");
    }

    protected String getTitle() {
        return t("container.crafting");
    }

    protected String getTexture() {
        return "gui/crafting_settings.png";
    }

    protected int[] getIncrements() {
        return new int[]{
            1, 10, 64,
            -1, -10, -64
        };
    }

    protected int getAmount() {
        return DEFAULT_AMOUNT;
    }

    protected Tuple<Integer, Integer> getAmountPos() {
        return new Tuple<>(7 + 1, 50 + 1);
    }

    protected Tuple<Integer, Integer> getIncrementButtonPos(int x, int y) {
        return new Tuple<>(6 + (x * (30 + 3)), y + (y == 0 ? 20 : 72));
    }

    protected Tuple<Integer, Integer> getStartCancelPos() {
        return new Tuple<>(114, 33);
    }

    protected boolean canAmountGoNegative() {
        return false;
    }

    @Override
    public void init(int x, int y) {
        Tuple<Integer, Integer> pos = getStartCancelPos();

        startButton = addButton(x + pos.getFirst(), y + pos.getSecond(), 50, 20, getStartButtonText());
        cancelButton = addButton(x + pos.getFirst(), y + pos.getSecond() + 24, 50, 20, t("gui.cancel"));

        amountField = new GuiTextField(0, fontRendererObj, x + getAmountPos().getFirst(), y + getAmountPos().getSecond(), 69 - 6, fontRendererObj.FONT_HEIGHT);
        amountField.setEnableBackgroundDrawing(false);
        amountField.setVisible(true);
        amountField.setText(String.valueOf(getAmount()));
        amountField.setTextColor(16777215);
        amountField.setCanLoseFocus(false);
        amountField.setFocused(true);

        int[] increments = getIncrements();

        for (int i = 0; i < 3; ++i) {
            pos = getIncrementButtonPos(i, 0);

            incrementButtons[i] = addButton(x + pos.getFirst(), y + pos.getSecond(), 30, 20, "+" + increments[i]);
        }

        for (int i = 0; i < 3; ++i) {
            pos = getIncrementButtonPos(i, 1);

            incrementButtons[3 + i] = addButton(x + pos.getFirst(), y + pos.getSecond(), 30, 20, String.valueOf(increments[3 + i]));
        }
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(getTexture());

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        amountField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, getTitle());
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

                    if (!canAmountGoNegative()) {
                        newAmount = Math.max(DEFAULT_AMOUNT, ((oldAmount == 1 && newAmount != 1) ? 0 : oldAmount) + newAmount);
                    } else {
                        newAmount = oldAmount + newAmount;
                    }

                    amountField.setText(String.valueOf(newAmount));

                    break;
                }
            }
        }
    }

    protected void startRequest() {
        Integer quantity = Ints.tryParse(amountField.getText());

        if (quantity != null && quantity > 0) {
            RS.INSTANCE.network.sendToServer(new MessageGridCraftingPreview(stack.getHash(), quantity));

            startButton.enabled = false;
        }
    }

    protected void close() {
        FMLClientHandler.instance().showGuiScreen(parent);
    }

    public GuiBase getParent() {
        return parent;
    }
}
