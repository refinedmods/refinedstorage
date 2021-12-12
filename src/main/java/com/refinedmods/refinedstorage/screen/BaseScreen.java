package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.grid.AlternativesScreen;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BaseScreen<T extends Container> extends ContainerScreen<T> {
    public static final int Z_LEVEL_ITEMS = 100;
    public static final int Z_LEVEL_TOOLTIPS = 500;
    public static final int Z_LEVEL_QTY = 300;

    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    private static final Map<Class, Queue<Consumer>> ACTIONS = new HashMap<>();

    private static final ITextComponent ALTERNATIVES_TEXT = new TranslationTextComponent("gui.refinedstorage.alternatives");

    private final List<SideButton> sideButtons = new ArrayList<>();

    private final Logger logger = LogManager.getLogger(getClass());

    private int sideButtonY;

    protected BaseScreen(T container, int xSize, int ySize, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);

        this.imageWidth = xSize;
        this.imageHeight = ySize;
    }

    private void runActions() {
        runActions(getClass());
        runActions(ContainerScreen.class);
    }

    private void runActions(Class clazz) {
        Queue<Consumer> queue = ACTIONS.get(clazz);

        if (queue != null && !queue.isEmpty()) {
            Consumer callback;
            while ((callback = queue.poll()) != null) {
                callback.accept(this);
            }
        }
    }

    @Override
    public void init() {
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        onPreInit();

        super.init();

        if (CraftingTweaksIntegration.isLoaded()) {
            buttons.removeIf(b -> !CraftingTweaksIntegration.isCraftingTweaksClass(b.getClass()));
            children.removeIf(c -> !CraftingTweaksIntegration.isCraftingTweaksClass(c.getClass()));
        } else {
            buttons.clear();
            children.clear();
        }

        sideButtonY = 6;
        sideButtons.clear();

        onPostInit(leftPos, topPos);

        runActions();
    }

    @Override
    public void removed() {
        super.removed();

        minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void tick() {
        super.tick();

        runActions();

        tick(leftPos, topPos);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);

        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float renderPartialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderBackground(matrixStack, leftPos, topPos, mouseX, mouseY);

        for (int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = menu.slots.get(i);

            if (slot.isActive() && slot instanceof FluidFilterSlot) {
                FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

                if (!stack.isEmpty()) {
                    FluidRenderer.INSTANCE.render(matrixStack, leftPos + slot.x, topPos + slot.y, stack);

                    if (((FluidFilterSlot) slot).isSizeAllowed()) {
                        renderQuantity(matrixStack, leftPos + slot.x, topPos + slot.y, API.instance().getQuantityFormatter().formatInBucketForm(stack.getAmount()), RenderSettings.INSTANCE.getSecondaryColor());

                        GL11.glDisable(GL11.GL_LIGHTING);
                    }
                }
            }
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        mouseX -= leftPos;
        mouseY -= topPos;

        renderForeground(matrixStack, mouseX, mouseY);

        for (Widget button : this.buttons) {
            if (button instanceof SideButton && button.isHovered()) {
                renderTooltip(matrixStack, mouseX, mouseY, ((SideButton) button).getTooltip());
            }
        }

        for (int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = menu.slots.get(i);

            if (slot.isActive() && slot instanceof FluidFilterSlot) {
                FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

                if (!stack.isEmpty() && RenderUtils.inBounds(slot.x, slot.y, 17, 17, mouseX, mouseY)) {
                    renderTooltip(matrixStack, mouseX, mouseY, stack.getDisplayName().getString());
                }
            }
        }
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        boolean valid = type != ClickType.QUICK_MOVE && minecraft.player.inventory.getCarried().isEmpty();

        if (valid && slot instanceof FilterSlot && slot.isActive() && ((FilterSlot) slot).isSizeAllowed()) {
            if (!slot.getItem().isEmpty()) {
                if (((FilterSlot) slot).isAlternativesAllowed() && hasControlDown()) {
                    minecraft.setScreen(new AlternativesScreen(
                        this,
                        minecraft.player,
                        ALTERNATIVES_TEXT,
                        slot.getItem(),
                        slot.getSlotIndex()
                    ));
                } else {
                    minecraft.setScreen(new ItemAmountScreen(
                        this,
                        minecraft.player,
                        slot.index,
                        slot.getItem(),
                        slot.getMaxStackSize(),
                        ((FilterSlot) slot).isAlternativesAllowed() ? (parent -> new AlternativesScreen(
                            parent,
                            minecraft.player,
                            ALTERNATIVES_TEXT,
                            slot.getItem(),
                            slot.getSlotIndex()
                        )) : null
                    ));
                }
            }
        } else if (valid && slot instanceof FluidFilterSlot && slot.isActive() && ((FluidFilterSlot) slot).isSizeAllowed()) {
            FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

            if (!stack.isEmpty()) {
                if (((FluidFilterSlot) slot).isAlternativesAllowed() && hasControlDown()) {
                    minecraft.setScreen(new AlternativesScreen(
                        this,
                        minecraft.player,
                        ALTERNATIVES_TEXT,
                        stack,
                        slot.getSlotIndex()
                    ));
                } else {
                    minecraft.setScreen(new FluidAmountScreen(
                        this,
                        minecraft.player,
                        slot.index,
                        stack,
                        ((FluidFilterSlot) slot).getFluidInventory().getMaxAmount(),
                        ((FluidFilterSlot) slot).isAlternativesAllowed() ? (parent -> new AlternativesScreen(
                            this,
                            minecraft.player,
                            ALTERNATIVES_TEXT,
                            stack,
                            slot.getSlotIndex()
                        )) : null
                    ));
                }
            } else {
                super.slotClicked(slot, slotId, mouseButton, type);
            }
        } else {
            super.slotClicked(slot, slotId, mouseButton, type);
        }
    }

    public CheckboxWidget addCheckBox(int x, int y, ITextComponent text, boolean checked, Consumer<CheckboxButton> onPress) {
        CheckboxWidget checkBox = new CheckboxWidget(x, y, text, checked, onPress);

        this.addButton(checkBox);

        return checkBox;
    }

    public Button addButton(int x, int y, int w, int h, ITextComponent text, boolean enabled, boolean visible, Button.IPressable onPress) {
        Button button = new Button(x, y, w, h, text, onPress);

        button.active = enabled;
        button.visible = visible;

        this.addButton(button);

        return button;
    }

    public void addSideButton(SideButton button) {
        button.x = leftPos - button.getWidth() - 2;
        button.y = topPos + sideButtonY;

        sideButtonY += button.getHeight() + 2;

        sideButtons.add(button);
        this.addButton(button);
    }

    public List<SideButton> getSideButtons() {
        return sideButtons;
    }

    public void bindTexture(String namespace, String filenameInTexturesFolder) {
        minecraft.getTextureManager().bind(TEXTURE_CACHE.computeIfAbsent(namespace + ":" + filenameInTexturesFolder, newId -> new ResourceLocation(namespace, "textures/" + filenameInTexturesFolder)));
    }

    public void renderItem(MatrixStack matrixStack, int x, int y, ItemStack stack) {
        renderItem(matrixStack, x, y, stack, false, null, 0);
    }

    public void renderItem(MatrixStack matrixStack, int x, int y, ItemStack stack, boolean overlay, @Nullable String text, int textColor) {
        try {
            setBlitOffset(Z_LEVEL_ITEMS);
            itemRenderer.blitOffset = Z_LEVEL_ITEMS;

            itemRenderer.renderGuiItem(stack, x, y);

            if (overlay) {
                itemRenderer.renderGuiItemDecorations(font, stack, x, y, "");
            }

            setBlitOffset(0);
            itemRenderer.blitOffset = 0;

            if (text != null) {
                renderQuantity(matrixStack, x, y, text, textColor);
            }
        } catch (Throwable t) {
            logger.warn("Couldn't render stack: {}", stack.getItem().getRegistryName());
        }
    }

    public void renderQuantity(MatrixStack matrixStack, int x, int y, String qty, int color) {
        boolean large = minecraft.isEnforceUnicode() || RS.CLIENT_CONFIG.getGrid().getLargeFont();

        matrixStack.pushPose();
        matrixStack.translate(x, y, Z_LEVEL_QTY);

        if (!large) {
            matrixStack.scale(0.5F, 0.5F, 1);
        }

        font.drawShadow(matrixStack, qty, (large ? 16 : 30) - font.width(qty), large ? 8 : 22, color);

        matrixStack.popPose();
    }

    public void renderString(MatrixStack matrixStack, int x, int y, String message) {
        renderString(matrixStack, x, y, message, RenderSettings.INSTANCE.getPrimaryColor());
    }

    public void renderString(MatrixStack matrixStack, int x, int y, String message, int color) {
        font.draw(matrixStack, message, x, y, color);
    }

    public void renderTooltip(MatrixStack matrixStack, int x, int y, String lines) {
        renderTooltip(matrixStack, ItemStack.EMPTY, x, y, lines);
    }

    public void renderTooltip(MatrixStack matrixStack, @Nonnull ItemStack stack, int x, int y, String lines) {
        renderTooltip(matrixStack, stack, x, y, Arrays.stream(lines.split("\n")).map(StringTextComponent::new).collect(Collectors.toList()));
    }

    public void renderTooltip(MatrixStack matrixStack, @Nonnull ItemStack stack, int x, int y, List<ITextComponent> lines) {
        GuiUtils.drawHoveringText(stack, matrixStack, lines, x, y, width, height, -1, font);
    }

    protected void onPreInit() {
        // NO OP
    }

    public static boolean isKeyDown(KeyBinding keybinding) {
        return !keybinding.isUnbound() && InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybinding.getKey().getValue()) &&
            keybinding.getKeyConflictContext().isActive() &&
            keybinding.getKeyModifier().isActive(keybinding.getKeyConflictContext());
    }

    public abstract void onPostInit(int x, int y);

    public abstract void tick(int x, int y);

    public abstract void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY);

    public abstract void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY);

    public static <T> void executeLater(Class<T> clazz, Consumer<T> callback) {
        ACTIONS.computeIfAbsent(clazz, key -> new ArrayDeque<>()).add(callback);
    }

    public static void executeLater(Consumer<ContainerScreen> callback) {
        executeLater(ContainerScreen.class, callback);
    }
}
