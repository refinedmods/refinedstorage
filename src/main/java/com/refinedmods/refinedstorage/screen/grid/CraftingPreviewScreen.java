package com.refinedmods.refinedstorage.screen.grid;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.FluidCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ItemCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.render.CraftingPreviewElementDrawers;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartRequestMessage;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CraftingPreviewScreen extends BaseScreen<Container> {
    private static final int VISIBLE_ROWS = 5;

    private final List<ICraftingPreviewElement<?>> stacks;
    private final Screen parent;
    private final ResourceLocation factoryId;

    private final ScrollbarWidget scrollbar;

    private final UUID id;
    private final int quantity;
    private final boolean fluids;

    private ItemStack hoveringStack;
    private FluidStack hoveringFluid;

    private final IElementDrawers drawers = new CraftingPreviewElementDrawers(this, font);

    public CraftingPreviewScreen(Screen parent, ResourceLocation factoryId, List<ICraftingPreviewElement<?>> stacks, UUID id, int quantity, boolean fluids, ITextComponent title) {
        super(new Container(null, 0) {
            @Override
            public boolean canInteractWith(@Nonnull PlayerEntity player) {
                return false;
            }
        }, 254, 201, null, title);

        this.stacks = new ArrayList<>(stacks);
        this.parent = parent;
        this.factoryId = factoryId;

        this.id = id;
        this.quantity = quantity;
        this.fluids = fluids;

        this.scrollbar = new ScrollbarWidget(this, 235, 20, 12, 149);
    }

    @Override
    public void onPostInit(int x, int y) {
        addButton(x + 55, y + 201 - 20 - 7, 50, 20, new TranslationTextComponent("gui.cancel"), true, true, btn -> close());

        Button startButton = addButton(x + 129, y + 201 - 20 - 7, 50, 20, new TranslationTextComponent("misc.refinedstorage.start"), true, true, btn -> startRequest());
        startButton.active = stacks.stream().noneMatch(ICraftingPreviewElement::hasMissing) && getErrorType() == null;
    }

    @Override
    public void tick(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
    }

    @Nullable
    private CraftingTaskErrorType getErrorType() {
        if (stacks.size() == 1 && stacks.get(0) instanceof ErrorCraftingPreviewElement) {
            return ((ErrorCraftingPreviewElement) stacks.get(0)).getType();
        }

        return null;
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/crafting_preview.png");

        blit(matrixStack, x, y, 0, 0, xSize, ySize);

        if (getErrorType() != null) {
            fill(matrixStack, x + 7, y + 20, x + 228, y + 169, 0xFFDBDBDB);
        }

        scrollbar.render(matrixStack);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());

        int x = 7;
        int y = 15;

        float scale = Minecraft.getInstance().getForceUnicodeFont() ? 1F : 0.5F;

        if (getErrorType() != null) {
            RenderSystem.pushMatrix();
            RenderSystem.scalef(scale, scale, 1);

            renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 11, scale), I18n.format("gui.refinedstorage.crafting_preview.error"));

            switch (getErrorType()) {
                case RECURSIVE: {
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.0"));
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.1"));
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 41, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.2"));
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 51, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.3"));

                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 61, scale), I18n.format("gui.refinedstorage.crafting_preview.error.recursive.4"));

                    RenderSystem.popMatrix();

                    ICraftingPattern pattern = PatternItem.fromCache(parent.getMinecraft().world, (ItemStack) stacks.get(0).getElement());

                    int yy = 83;
                    for (ItemStack output : pattern.getOutputs()) {
                        if (output != null) {
                            RenderSystem.pushMatrix();
                            RenderSystem.scalef(scale, scale, 1);
                            renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 25, scale), RenderUtils.getOffsetOnScale(yy + 6, scale), output.getDisplayName().getString());
                            RenderSystem.popMatrix();

                            RenderHelper.setupGui3DDiffuseLighting();
                            RenderSystem.enableDepthTest();
                            renderItem(matrixStack, x + 5, yy, output);
                            RenderHelper.disableStandardItemLighting();

                            yy += 17;
                        }
                    }

                    break;
                }
                case TOO_COMPLEX: {
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 21, scale), I18n.format("gui.refinedstorage.crafting_preview.error.too_complex.0"));
                    renderString(matrixStack, RenderUtils.getOffsetOnScale(x + 5, scale), RenderUtils.getOffsetOnScale(y + 31, scale), I18n.format("gui.refinedstorage.crafting_preview.error.too_complex.1"));

                    RenderSystem.popMatrix();

                    break;
                }
            }
        } else {
            int slot = scrollbar != null ? (scrollbar.getOffset() * 3) : 0;

            RenderHelper.setupGui3DDiffuseLighting();
            RenderSystem.enableDepthTest();

            this.hoveringStack = null;
            this.hoveringFluid = null;

            for (int i = 0; i < 3 * 5; ++i) {
                if (slot < stacks.size()) {
                    ICraftingPreviewElement stack = stacks.get(slot);

                    stack.draw(x, y + 5, drawers);

                    if (RenderUtils.inBounds(x + 5, y + 7, 16, 16, mouseX, mouseY)) {
                        this.hoveringStack = stack.getId().equals(ItemCraftingPreviewElement.ID) ? (ItemStack) stack.getElement() : null;

                        if (this.hoveringStack == null) {
                            this.hoveringFluid = stack.getId().equals(FluidCraftingPreviewElement.ID) ? (FluidStack) stack.getElement() : null;
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (hoveringStack != null) {
            renderTooltip(
                matrixStack,
                hoveringStack,
                mouseX,
                mouseY,
                hoveringStack.getTooltip(
                    Minecraft.getInstance().player,
                    Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL
                )
            );
        } else if (hoveringFluid != null) {
            renderTooltip(matrixStack, mouseX, mouseY, hoveringFluid.getDisplayName().getString());
        }
    }

    @Override
    public void mouseMoved(double mx, double my) {
        scrollbar.mouseMoved(mx, my);

        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return scrollbar.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        return this.scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            startRequest();

            return true;
        }

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close();

            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    private void startRequest() {
        RS.NETWORK_HANDLER.sendToServer(new GridCraftingStartRequestMessage(id, quantity, fluids));

        close();
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) stacks.size() / 3F));
    }

    private void close() {
        minecraft.displayGuiScreen(parent);
    }
}
