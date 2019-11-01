package com.raoulvdberge.refinedstorage.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.integration.craftingtweaks.CraftingTweaksIntegration;
import com.raoulvdberge.refinedstorage.render.FluidRenderer;
import com.raoulvdberge.refinedstorage.screen.widget.CheckBoxWidget;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButton;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public abstract class BaseScreen<T extends Container> extends ContainerScreen<T> {
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    private static final Map<Class, Queue<Consumer>> ACTIONS = new HashMap<>();

    private Logger logger = LogManager.getLogger(getClass());

    private int sideButtonY;

    public BaseScreen(T container, int xSize, int ySize, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);

        this.xSize = xSize;
        this.ySize = ySize;
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
        minecraft.keyboardListener.enableRepeatEvents(true);

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

        onPostInit(guiLeft, guiTop);

        runActions();
    }

    @Override
    public void onClose() {
        super.onClose();

        minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public void tick() {
        super.tick();

        runActions();

        tick(guiLeft, guiTop);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();

        super.render(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        renderBackground(guiLeft, guiTop, mouseX, mouseY);

        for (int i = 0; i < this.container.inventorySlots.size(); ++i) {
            Slot slot = container.inventorySlots.get(i);

            if (slot.isEnabled() && slot instanceof FluidFilterSlot) {
                FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

                if (!stack.isEmpty()) {
                    FluidRenderer.INSTANCE.render(guiLeft + slot.xPos, guiTop + slot.yPos, stack);

                    if (((FluidFilterSlot) slot).isSizeAllowed()) {
                        renderQuantity(guiLeft + slot.xPos, guiTop + slot.yPos, API.instance().getQuantityFormatter().formatInBucketForm(stack.getAmount()));

                        GL11.glDisable(GL11.GL_LIGHTING);
                    }
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        mouseX -= guiLeft;
        mouseY -= guiTop;

        renderForeground(mouseX, mouseY);

        for (int i = 0; i < this.buttons.size(); ++i) {
            Widget button = buttons.get(i);

            if (button instanceof SideButton && button.isHovered()) {
                renderTooltip(mouseX, mouseY, ((SideButton) button).getTooltip());
            }
        }

        for (int i = 0; i < this.container.inventorySlots.size(); ++i) {
            Slot slot = container.inventorySlots.get(i);

            if (slot.isEnabled() && slot instanceof FluidFilterSlot) {
                FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

                if (!stack.isEmpty() && RenderUtils.inBounds(slot.xPos, slot.yPos, 17, 17, mouseX, mouseY)) {
                    renderTooltip(mouseX, mouseY, stack.getDisplayName().getFormattedText());
                }
            }
        }
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
        boolean valid = type != ClickType.QUICK_MOVE && minecraft.player.inventory.getItemStack().isEmpty();

        if (valid && slot instanceof FilterSlot && slot.isEnabled() && ((FilterSlot) slot).isSizeAllowed()) {
            if (!slot.getStack().isEmpty()) {
                minecraft.displayGuiScreen(new AmountScreen(
                    this,
                    minecraft.player,
                    slot.slotNumber,
                    slot.getStack(),
                    slot.getSlotStackLimit()
                ));
            }
        } else if (valid && slot instanceof FluidFilterSlot && slot.isEnabled() && ((FluidFilterSlot) slot).isSizeAllowed()) {
            FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

            if (!stack.isEmpty()) {
                minecraft.displayGuiScreen(new FluidAmountScreen(
                    this,
                    minecraft.player,
                    slot.slotNumber,
                    stack,
                    ((FluidFilterSlot) slot).getFluidInventory().getMaxAmount()
                ));
            } else {
                super.handleMouseClick(slot, slotId, mouseButton, type);
            }
        } else {
            super.handleMouseClick(slot, slotId, mouseButton, type);
        }
    }

    public GuiCheckBox addCheckBox(int x, int y, String text, boolean checked, Button.IPressable onPress) {
        CheckBoxWidget checkBox = new CheckBoxWidget(x, y, text, checked, onPress);

        this.addButton(checkBox);

        return checkBox;
    }

    public Button addButton(int x, int y, int w, int h, String text, boolean enabled, boolean visible, Button.IPressable onPress) {
        Button button = new Button(x, y, w, h, text, onPress);

        button.active = enabled;
        button.visible = visible;

        this.addButton(button);

        return button;
    }

    public SideButton addSideButton(SideButton button) {
        button.x = guiLeft + -SideButton.WIDTH - 2;
        button.y = guiTop + sideButtonY;

        sideButtonY += SideButton.HEIGHT + 2;

        this.addButton(button);

        return button;
    }

    public void bindTexture(String namespace, String filenameInTexturesFolder) {
        minecraft.getTextureManager().bindTexture(TEXTURE_CACHE.computeIfAbsent(namespace + ":" + filenameInTexturesFolder, (newId) -> new ResourceLocation(namespace, "textures/" + filenameInTexturesFolder)));
    }

    public void renderItem(int x, int y, ItemStack stack) {
        renderItem(x, y, stack, false, null);
    }

    public void renderItem(int x, int y, ItemStack stack, boolean overlay, @Nullable String text) {
        try {
            itemRenderer.zLevel = 200.0F;

            itemRenderer.renderItemIntoGUI(stack, x, y);

            if (overlay) {
                this.itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, "");
            }

            if (text != null) {
                renderQuantity(x, y, text);
            }

            itemRenderer.zLevel = 0.0F;
        } catch (Throwable t) {
            logger.warn("Couldn't render stack: " + stack.getItem().toString(), t);
        }
    }

    public void renderQuantity(int x, int y, String qty) {
        boolean large = minecraft.getForceUnicodeFont() || RS.CLIENT_CONFIG.getGrid().getLargeFont();

        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 1);

        if (!large) {
            GlStateManager.scalef(0.5f, 0.5f, 1);
        }

        GlStateManager.disableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableDepthTest();

        font.drawStringWithShadow(qty, (large ? 16 : 30) - font.getStringWidth(qty), large ? 8 : 22, 16777215);

        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void renderString(int x, int y, String message) {
        renderString(x, y, message, 4210752);
    }

    public void renderString(int x, int y, String message, int color) {
        GlStateManager.disableLighting();
        font.drawString(message, x, y, color);
        GlStateManager.enableLighting();
    }

    public void renderTooltip(int x, int y, String lines) {
        renderTooltip(ItemStack.EMPTY, x, y, lines);
    }

    public void renderTooltip(@Nonnull ItemStack stack, int x, int y, String lines) {
        renderTooltip(stack, x, y, Arrays.asList(lines.split("\n")));
    }

    public void renderTooltip(@Nonnull ItemStack stack, int x, int y, List<String> lines) {
        GlStateManager.disableLighting();
        GuiUtils.drawHoveringText(stack, lines, x, y, width - guiLeft, height, -1, font);
        GlStateManager.enableLighting();
    }

    protected void onPreInit() {
        // NO OP
    }

    public static boolean isKeyDown(KeyBinding keybinding) {
        return InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), keybinding.getKey().getKeyCode()) &&
            keybinding.getKeyConflictContext().isActive() &&
            keybinding.getKeyModifier().isActive(keybinding.getKeyConflictContext());
    }

    public abstract void onPostInit(int x, int y);

    public abstract void tick(int x, int y);

    public abstract void renderBackground(int x, int y, int mouseX, int mouseY);

    public abstract void renderForeground(int mouseX, int mouseY);

    public static <T> void executeLater(Class<T> clazz, Consumer<T> callback) {
        Queue<Consumer> queue = ACTIONS.get(clazz);

        if (queue == null) {
            ACTIONS.put(clazz, queue = new ArrayDeque<>());
        }

        queue.add(callback);
    }

    public static void executeLater(Consumer<ContainerScreen> callback) {
        executeLater(ContainerScreen.class, callback);
    }
}
