package com.raoulvdberge.refinedstorage.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementError;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.render.CraftingPreviewElementDrawers;
import com.raoulvdberge.refinedstorage.gui.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GuiCraftingPreview extends GuiBase {
    private static final int VISIBLE_ROWS = 5;

    private List<ICraftingPreviewElement> stacks;
    private Screen parent;

    private ScrollbarWidget scrollbar;

    private int hash;
    private int quantity;

    private Button startButton;
    private Button cancelButton;

    private ItemStack hoveringStack;
    private FluidStack hoveringFluid;

    private IElementDrawers drawers = new CraftingPreviewElementDrawers(this, font);

    private boolean fluids;

    public GuiCraftingPreview(Screen parent, List<ICraftingPreviewElement> stacks, int hash, int quantity, boolean fluids) {
        super(new Container(null, 0) {
            @Override
            public boolean canInteractWith(PlayerEntity playerIn) {
                return false;
            }
        }, 254, 201, null, null);

        this.stacks = new ArrayList<>(stacks);
        this.parent = parent;

        this.hash = hash;
        this.quantity = quantity;
        this.fluids = fluids;

        this.scrollbar = new ScrollbarWidget(235, 20, 12, 149);
    }

    @Override
    public void init(int x, int y) {
        cancelButton = addButton(x + 55, y + 201 - 20 - 7, 50, 20, I18n.format("gui.cancel"), true, true, btn -> {
        });

        startButton = addButton(x + 129, y + 201 - 20 - 7, 50, 20, I18n.format("misc.refinedstorage:start"), true, true, btn -> {
        });

        startButton.active = stacks.stream().noneMatch(ICraftingPreviewElement::hasMissing) && getErrorType() == null;
    }

    @Override
    public void tick(int x, int y) {
        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
            scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
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
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/crafting_preview.png");

        blit(x, y, 0, 0, xSize, ySize);

        if (getErrorType() != null) {
            fill(x + 7, y + 20, x + 228, y + 169, 0xFFDBDBDB);
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:crafting_preview"));

        int x = 7;
        int y = 15;

        float scale = /* TODO font.getUnicodeFlag() ? 1F :*/ 0.5F;

        if (getErrorType() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(scale, scale, 1);

            renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 11, scale), I18n.format("gui.refinedstorage:crafting_preview.error"));

            switch (getErrorType()) {
                case RECURSIVE: {
                    renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), I18n.format("gui.refinedstorage:crafting_preview.error.recursive.0"));
                    renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), I18n.format("gui.refinedstorage:crafting_preview.error.recursive.1"));
                    renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 41, scale), I18n.format("gui.refinedstorage:crafting_preview.error.recursive.2"));
                    renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 51, scale), I18n.format("gui.refinedstorage:crafting_preview.error.recursive.3"));

                    renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 61, scale), I18n.format("gui.refinedstorage:crafting_preview.error.recursive.4"));

                    GlStateManager.popMatrix();

                    ICraftingPattern pattern = /* TODO ItemPattern.getPatternFromCache(parent.getMinecraft().world, (ItemStack) stacks.get(0).getElement())*/null;

                    int yy = 83;
                    for (ItemStack output : pattern.getOutputs()) {
                        if (output != null) {
                            GlStateManager.pushMatrix();
                            GlStateManager.scalef(scale, scale, 1);
                            renderString(RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy + 6, scale), output.getDisplayName().getFormattedText()); // TODO getFOrmattedText
                            GlStateManager.popMatrix();

                            RenderHelper.enableGUIStandardItemLighting();
                            GlStateManager.enableDepthTest();
                            renderItem(x + 5, yy, output);
                            RenderHelper.disableStandardItemLighting();

                            yy += 17;
                        }
                    }

                    break;
                }
                case TOO_COMPLEX: {
                    renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), I18n.format("gui.refinedstorage:crafting_preview.error.too_complex.0"));
                    renderString(RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), I18n.format("gui.refinedstorage:crafting_preview.error.too_complex.1"));

                    GlStateManager.popMatrix();

                    break;
                }
            }
        } else {
            int slot = scrollbar != null ? (scrollbar.getOffset() * 3) : 0;

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableDepthTest();

            this.hoveringStack = null;
            this.hoveringFluid = null;

            for (int i = 0; i < 3 * 5; ++i) {
                if (slot < stacks.size()) {
                    ICraftingPreviewElement stack = stacks.get(slot);

                    stack.draw(x, y + 5, drawers);

                    if (RenderUtils.inBounds(x + 5, y + 7, 16, 16, mouseX, mouseY)) {
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
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (hoveringStack != null) {
            renderTooltip(hoveringStack, mouseX, mouseY, hoveringStack.getTooltip(Minecraft.getInstance().player, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
        } else if (hoveringFluid != null) {
            renderTooltip(mouseX, mouseY, hoveringFluid.getDisplayName().getFormattedText()); // TODO gft
        }
    }

    /* TODO
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
    }*/

    private void startRequest() {
        // TODO RS.INSTANCE.network.sendToServer(new MessageGridCraftingStart(hash, quantity, fluids));

        close();
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) stacks.size() / 3F));
    }

    private void close() {
        // TODO FMLClientHandler.instance().showGuiScreen(parent);
    }
}
