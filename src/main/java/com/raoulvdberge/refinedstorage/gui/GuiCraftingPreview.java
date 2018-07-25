package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementError;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.gui.control.Scrollbar;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingStart;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiCraftingPreview extends GuiBase {
    public class CraftingPreviewElementDrawers extends ElementDrawers {
        private IElementDrawer<Integer> overlayDrawer = (x, y, colour) -> {
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();

            drawRect(x, y, x + 73, y + 29, colour);
        };

        @Override
        public IElementDrawer<Integer> getOverlayDrawer() {
            return overlayDrawer;
        }
    }

    private static final int VISIBLE_ROWS = 5;

    private List<ICraftingPreviewElement> stacks;
    private GuiScreen parent;

    private int hash;
    private int quantity;

    private GuiButton startButton;
    private GuiButton cancelButton;

    private ItemStack hoveringStack;
    private FluidStack hoveringFluid;

    private IElementDrawers drawers = new CraftingPreviewElementDrawers();

    private boolean fluids;

    public GuiCraftingPreview(GuiScreen parent, List<ICraftingPreviewElement> stacks, int hash, int quantity, boolean fluids) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 254, 201);

        this.stacks = new ArrayList<>(stacks);
        this.parent = parent;

        this.hash = hash;
        this.quantity = quantity;
        this.fluids = fluids;

        this.scrollbar = new Scrollbar(235, 20, 12, 149);
    }

    @Override
    public void init(int x, int y) {
        cancelButton = addButton(x + 55, y + 201 - 20 - 7, 50, 20, t("gui.cancel"));
        startButton = addButton(x + 129, y + 201 - 20 - 7, 50, 20, t("misc.refinedstorage:start"));
        startButton.enabled = stacks.stream().noneMatch(ICraftingPreviewElement::hasMissing) && getErrorType() == null;
    }

    @Override
    public void update(int x, int y) {
        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
            scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
        }

        if (startButton != null && !startButton.enabled && isCtrlKeyDown() && isShiftKeyDown() && getErrorType() == null) {
            startButton.enabled = true;
        }
    }

    @Nullable
    private CraftingTaskErrorType getErrorType() {
        if (stacks.size() == 1 && stacks.get(0) instanceof CraftingPreviewElementError) {
            return ((CraftingPreviewElementError) stacks.get(0)).getType();
        }

        return null;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_preview.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        if (getErrorType() != null) {
            drawRect(x + 7, y + 20, x + 228, y + 169, 0xFFDBDBDB);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafting_preview"));

        int x = 7;
        int y = 15;

        float scale = fontRenderer.getUnicodeFlag() ? 1F : 0.5F;

        if (getErrorType() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1);

            drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 11, scale), t("gui.refinedstorage:crafting_preview.error"));

            switch (getErrorType()) {
                case RECURSIVE: {
                    drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), t("gui.refinedstorage:crafting_preview.error.recursive.0"));
                    drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), t("gui.refinedstorage:crafting_preview.error.recursive.1"));
                    drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 41, scale), t("gui.refinedstorage:crafting_preview.error.recursive.2"));
                    drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 51, scale), t("gui.refinedstorage:crafting_preview.error.recursive.3"));

                    drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 61, scale), t("gui.refinedstorage:crafting_preview.error.recursive.4"));

                    GlStateManager.popMatrix();

                    ICraftingPattern pattern = ItemPattern.getPatternFromCache(parent.mc.world, (ItemStack) stacks.get(0).getElement());

                    int yy = 83;
                    for (ItemStack output : pattern.getOutputs()) {
                        if (output != null) {
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(scale, scale, 1);
                            drawString(RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy + 6, scale), output.getDisplayName());
                            GlStateManager.popMatrix();

                            RenderHelper.enableGUIStandardItemLighting();
                            GlStateManager.enableDepth();
                            drawItem(x + 5, yy, output);
                            RenderHelper.disableStandardItemLighting();

                            yy += 17;
                        }
                    }

                    break;
                }
                case TOO_COMPLEX: {
                    drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), t("gui.refinedstorage:crafting_preview.error.too_complex.0"));
                    drawString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), t("gui.refinedstorage:crafting_preview.error.too_complex.1"));

                    GlStateManager.popMatrix();

                    break;
                }
            }
        } else {
            int slot = scrollbar != null ? (scrollbar.getOffset() * 3) : 0;

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableDepth();

            this.hoveringStack = null;
            this.hoveringFluid = null;

            for (int i = 0; i < 3 * 5; ++i) {
                if (slot < stacks.size()) {
                    ICraftingPreviewElement stack = stacks.get(slot);

                    stack.draw(x, y + 5, drawers);

                    if (inBounds(x + 5, y + 7, 16, 16, mouseX, mouseY)) {
                        this.hoveringStack = stack.getId().equals(CraftingPreviewElementItemStack.ID) ? (ItemStack) stack.getElement() : null;

                        if (this.hoveringStack == null) {
                            this.hoveringFluid = stack.getId().equals(CraftingPreviewElementFluidStack.ID) ? (FluidStack) stack.getElement() : null;
                        }
                    }
                }

                if ((i + 1) % 3 == 0) {
                    x = 7;
                    y += 30;
                } else {
                    x += 74;
                }

                slot++;
            }

            if (!startButton.enabled && inBounds(startButton.x - guiLeft, startButton.y - guiTop, startButton.width, startButton.height, mouseX, mouseY)) {
                drawTooltip(mouseX, mouseY, t("gui.refinedstorage:crafting_preview.force_start"));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (hoveringStack != null) {
            drawTooltip(hoveringStack, mouseX, mouseY, hoveringStack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
        } else if (hoveringFluid != null) {
            drawTooltip(mouseX, mouseY, hoveringFluid.getLocalizedName());
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN && startButton.enabled) {
            startRequest();
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            close();
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == startButton.id) {
            startRequest();
        } else if (button.id == cancelButton.id) {
            close();
        }
    }

    private void startRequest() {
        RS.INSTANCE.network.sendToServer(new MessageGridCraftingStart(hash, quantity, fluids));

        close();
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) stacks.size() / 3F));
    }

    private void close() {
        FMLClientHandler.instance().showGuiScreen(parent);
    }
}
