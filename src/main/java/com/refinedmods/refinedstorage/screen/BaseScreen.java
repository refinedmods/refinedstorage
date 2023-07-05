package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.grid.AlternativesScreen;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(RS.ID, "textures/icons.png");

    private static final int Z_LEVEL_QTY = 300;
    private static final Map<Class<?>, Queue<Consumer<?>>> ACTIONS = new HashMap<>();

    private static final Component ALTERNATIVES_TEXT = Component.translatable("gui.refinedstorage.alternatives");
    protected final Inventory inventory;
    private final List<SideButton> sideButtons = new ArrayList<>();
    private final Logger logger = LogManager.getLogger(getClass());
    private int sideButtonY;

    protected BaseScreen(T containerMenu, int xSize, int ySize, Inventory inventory, Component title) {
        super(containerMenu, inventory, title);

        this.imageWidth = xSize;
        this.imageHeight = ySize;
        this.inventory = inventory;
    }

    public static boolean isKeyDown(KeyMapping keybinding) {
        return !keybinding.isUnbound() && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybinding.getKey().getValue()) &&
            keybinding.getKeyConflictContext().isActive() &&
            keybinding.getKeyModifier().isActive(keybinding.getKeyConflictContext());
    }

    public static <T> void executeLater(Class<T> clazz, Consumer<T> callback) {
        ACTIONS.computeIfAbsent(clazz, key -> new ArrayDeque<>()).add(callback);
    }

    public static void executeLater(Consumer<AbstractContainerScreen> callback) {
        executeLater(AbstractContainerScreen.class, callback);
    }

    public void runActions() {
        runActions(getClass());
        runActions(AbstractContainerScreen.class);
    }

    private void runActions(Class<?> clazz) {
        Queue<Consumer<?>> queue = ACTIONS.get(clazz);

        if (queue != null && !queue.isEmpty()) {
            Consumer callback;
            while ((callback = queue.poll()) != null) {
                callback.accept(this);
            }
        }
    }

    @Override
    public void init() {
        onPreInit();

        super.init();

        // TODO: what about craft tweaker buttons?
        this.clearWidgets();

        sideButtonY = 6;
        sideButtons.clear();

        onPostInit(leftPos, topPos);

        runActions();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        runActions();
        tick(leftPos, topPos);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);

        super.render(graphics, mouseX, mouseY, partialTicks);

        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float renderPartialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        renderBackground(graphics, leftPos, topPos, mouseX, mouseY);

        for (int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = menu.slots.get(i);

            if (slot.isActive() && slot instanceof FluidFilterSlot) {
                FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

                if (!stack.isEmpty()) {
                    FluidRenderer.INSTANCE.render(graphics, leftPos + slot.x, topPos + slot.y, stack);

                    if (((FluidFilterSlot) slot).isSizeAllowed()) {
                        renderQuantity(graphics, leftPos + slot.x, topPos + slot.y, API.instance().getQuantityFormatter().formatInBucketForm(stack.getAmount()), RenderSettings.INSTANCE.getSecondaryColor());

                        GL11.glDisable(GL11.GL_LIGHTING);
                    }
                }
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        mouseX -= leftPos;
        mouseY -= topPos;

        renderForeground(graphics, mouseX, mouseY);

        for (int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = menu.slots.get(i);

            if (slot.isActive() && slot instanceof FluidFilterSlot) {
                FluidStack stack = ((FluidFilterSlot) slot).getFluidInventory().getFluid(slot.getSlotIndex());

                if (!stack.isEmpty() && RenderUtils.inBounds(slot.x, slot.y, 17, 17, mouseX, mouseY)) {
                    renderTooltip(graphics, mouseX, mouseY, stack.getDisplayName().getString());
                }
            }
        }
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        boolean valid = type != ClickType.QUICK_MOVE && minecraft.player.containerMenu.getCarried().isEmpty();

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
                        Math.min(slot.getMaxStackSize(), slot.getItem().getMaxStackSize()),
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

    public CheckboxWidget addCheckBox(int x, int y, Component text, boolean checked, Consumer<Checkbox> onPress) {
        CheckboxWidget checkBox = new CheckboxWidget(x, y, text, checked, onPress);

        this.addRenderableWidget(checkBox);

        return checkBox;
    }

    public Button addButton(int x, int y, int w, int h, Component text, boolean enabled, boolean visible, Button.OnPress onPress) {
        Button button = Button.builder(text, onPress).pos(x, y).size(w, h).build();
        button.active = enabled;
        button.visible = visible;
        addRenderableWidget(button);
        return button;
    }

    public void addSideButton(SideButton button) {
        button.setX(leftPos - button.getWidth() - 2);
        button.setY(topPos + sideButtonY);
        sideButtonY += button.getHeight() + 2;
        sideButtons.add(button);
        addRenderableWidget(button);
    }

    public List<SideButton> getSideButtons() {
        return sideButtons;
    }

    public void renderItem(GuiGraphics graphics, int x, int y, ItemStack stack) {
        renderItem(graphics, x, y, stack, false, null, 0);
    }

    public void renderItem(GuiGraphics graphics, int x, int y, ItemStack stack, boolean overlay, @Nullable String text, int textColor) {
        try {
            graphics.renderItem(stack, x, y);
            if (overlay) {
                graphics.renderItemDecorations(font, stack, x, y, "");
            }
            if (text != null) {
                renderQuantity(graphics, x, y, text, textColor);
            }
        } catch (Throwable t) {
            logger.warn("Couldn't render stack: {}", ForgeRegistries.ITEMS.getKey(stack.getItem()));
        }
    }

    public void renderQuantity(GuiGraphics graphics, int x, int y, String qty, int color) {
        boolean large = minecraft.isEnforceUnicode() || RS.CLIENT_CONFIG.getGrid().getLargeFont();

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, Z_LEVEL_QTY);

        if (!large) {
            graphics.pose().scale(0.5F, 0.5F, 1);
        }

        graphics.drawString(font, qty, (large ? 16 : 30) - font.width(qty), large ? 8 : 22, color);

        graphics.pose().popPose();
    }

    public void renderString(GuiGraphics graphics, int x, int y, String message) {
        renderString(graphics, x, y, message, RenderSettings.INSTANCE.getPrimaryColor());
    }

    public void renderString(GuiGraphics graphics, int x, int y, String message, int color) {
        graphics.drawString(font, message, x, y, color, false);
    }

    public void renderTooltip(GuiGraphics graphics, int x, int y, String lines) {
        renderTooltip(graphics, ItemStack.EMPTY, x, y, lines);
    }

    public void renderTooltip(GuiGraphics graphics, @Nonnull ItemStack stack, int x, int y, String lines) {
        renderTooltip(graphics, stack, x, y, Arrays.stream(lines.split("\n")).map(Component::literal).collect(Collectors.toList()));
    }

    public void renderTooltip(GuiGraphics graphics, @Nonnull ItemStack stack, int x, int y, List<Component> lines) {
        graphics.renderComponentTooltip(font, lines, x, y, stack);
    }

    protected void onPreInit() {
        // NO OP
    }

    public abstract void onPostInit(int x, int y);

    public abstract void tick(int x, int y);

    public abstract void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY);

    public abstract void renderForeground(GuiGraphics graphics, int mouseX, int mouseY);
}
