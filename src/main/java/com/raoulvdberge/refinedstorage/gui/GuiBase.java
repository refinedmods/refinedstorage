package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.gui.control.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.control.SideButton;
import com.raoulvdberge.refinedstorage.integration.jei.IntegrationJEI;
import com.raoulvdberge.refinedstorage.integration.jei.RecipeTransferHandlerGrid;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public abstract class GuiBase extends GuiContainer {
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    private static final Map<Class, Queue<Consumer>> RUNNABLES = new HashMap<>();

    public static final RenderUtils.FluidRenderer FLUID_RENDERER = new RenderUtils.FluidRenderer(-1, 16, 16);

    public class ElementDrawers implements IElementDrawers {
        private IElementDrawer<FluidStack> fluidDrawer = (x, y, element) -> FLUID_RENDERER.draw(GuiBase.this.mc, x, y, element);

        @Override
        public IElementDrawer<ItemStack> getItemDrawer() {
            return GuiBase.this::drawItem;
        }

        @Override
        public IElementDrawer<FluidStack> getFluidDrawer() {
            return fluidDrawer;
        }

        @Override
        public IElementDrawer<String> getStringDrawer() {
            return GuiBase.this::drawString;
        }

        @Override
        public FontRenderer getFontRenderer() {
            return fontRenderer;
        }
    }

    private int lastButtonId;
    private int lastSideButtonY;

    private String hoveringFluid = null;

    protected int screenWidth;
    protected int screenHeight;

    protected Scrollbar scrollbar;

    private boolean initializing;

    public GuiBase(Container container, int screenWidth, int screenHeight) {
        super(container);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.xSize = screenWidth;
        this.ySize = screenHeight;
    }

    private void runRunnables() {
        Queue<Consumer> queue = RUNNABLES.get(getClass());

        if (queue != null && !queue.isEmpty()) {
            Consumer callback;
            while ((callback = queue.poll()) != null) {
                callback.accept(this);
            }
        }

        queue = RUNNABLES.get(GuiContainer.class);

        if (queue != null && !queue.isEmpty()) {
            Consumer callback;
            while ((callback = queue.poll()) != null) {
                callback.accept(this);
            }
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    public boolean isMouseOverSlotPublic(Slot slot, int mx, int my) {
        return this.isPointInRegion(slot.xPos, slot.yPos, 16, 16, mx, my);
    }

    @Override
    public void initGui() {
        if (initializing) { // Fix double initialize because of runRunnables
            return;
        }

        initializing = true;

        Keyboard.enableRepeatEvents(true);

        calcHeight();

        super.initGui();

        if (!buttonList.isEmpty()) {
            buttonList.removeIf(b -> !b.getClass().getName().contains("net.blay09.mods.craftingtweaks")); // Prevent crafting tweaks buttons from resetting
        }

        lastButtonId = 0;
        lastSideButtonY = getSideButtonYStart();

        init(guiLeft, guiTop);

        runRunnables();

        initializing = false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    protected void calcHeight() {
        // NO OP
    }

    protected int getSideButtonYStart() {
        return 6;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        runRunnables();

        update(guiLeft, guiTop);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        try {
            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Exception e) {
            // NO OP: Prevent a MC crash (see #1483)
        }

        renderHoveredToolTip(mouseX, mouseY);

        // Prevent accidental scrollbar click after clicking recipe transfer button
        if (scrollbar != null && (!IntegrationJEI.isLoaded() || System.currentTimeMillis() - RecipeTransferHandlerGrid.LAST_TRANSFER > RecipeTransferHandlerGrid.TRANSFER_SCROLL_DELAY_MS)) {
            scrollbar.update(this, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawBackground(guiLeft, guiTop, mouseX, mouseY);

        this.hoveringFluid = null;

        for (int i = 0; i < inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = inventorySlots.inventorySlots.get(i);

            if (slot.isEnabled() && slot instanceof SlotFilterFluid) {
                FluidStack stack = ((SlotFilterFluid) slot).getFluidInventory().getFluid(slot.getSlotIndex());

                if (stack != null) {
                    FLUID_RENDERER.draw(mc, guiLeft + slot.xPos, guiTop + slot.yPos, stack);

                    if (((SlotFilterFluid) slot).isSizeAllowed()) {
                        drawQuantity(guiLeft + slot.xPos, guiTop + slot.yPos, API.instance().getQuantityFormatter().formatInBucketForm(stack.amount));

                        GL11.glDisable(GL11.GL_LIGHTING);
                    }

                    if (inBounds(guiLeft + slot.xPos, guiTop + slot.yPos, 17, 17, mouseX, mouseY)) {
                        this.hoveringFluid = stack.getLocalizedName();
                    }
                }
            }
        }

        if (scrollbar != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            scrollbar.draw(this);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mouseX -= guiLeft;
        mouseY -= guiTop;

        String sideButtonTooltip = null;

        for (int i = 0; i < buttonList.size(); ++i) {
            GuiButton button = buttonList.get(i);

            if (button instanceof SideButton && ((SideButton) button).isHovered()) {
                sideButtonTooltip = ((SideButton) button).getTooltip();
            }
        }

        drawForeground(mouseX, mouseY);

        if (sideButtonTooltip != null || hoveringFluid != null) {
            drawTooltip(mouseX, mouseY, sideButtonTooltip != null ? sideButtonTooltip : hoveringFluid);
        }
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
        boolean valid = type != ClickType.QUICK_MOVE && Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty();

        if (valid && slot instanceof SlotFilter && slot.isEnabled() && ((SlotFilter) slot).isSizeAllowed()) {
            if (!slot.getStack().isEmpty()) {
                FMLClientHandler.instance().showGuiScreen(new GuiAmount(
                    (GuiBase) Minecraft.getMinecraft().currentScreen,
                    Minecraft.getMinecraft().player,
                    slot.slotNumber,
                    slot.getStack(),
                    slot.getSlotStackLimit()
                ));
            }
        } else if (valid && slot instanceof SlotFilterFluid && slot.isEnabled() && ((SlotFilterFluid) slot).isSizeAllowed()) {
            FluidStack stack = ((SlotFilterFluid) slot).getFluidInventory().getFluid(slot.getSlotIndex());

            if (stack != null) {
                FMLClientHandler.instance().showGuiScreen(new GuiFluidAmount(
                    (GuiBase) Minecraft.getMinecraft().currentScreen,
                    Minecraft.getMinecraft().player,
                    slot.slotNumber,
                    stack,
                    ((SlotFilterFluid) slot).getFluidInventory().getMaxAmount()
                ));
            } else {
                super.handleMouseClick(slot, slotId, mouseButton, type);
            }
        } else {
            super.handleMouseClick(slot, slotId, mouseButton, type);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int d = Mouse.getEventDWheel();

        if (scrollbar != null && d != 0) {
            scrollbar.wheel(d);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button instanceof SideButton) {
            ((SideButton) button).actionPerformed();
        }
    }

    public GuiCheckBox addCheckBox(int x, int y, String text, boolean checked) {
        GuiCheckBox checkBox = new GuiCheckBox(lastButtonId++, x, y, text, checked);

        buttonList.add(checkBox);

        return checkBox;
    }

    public GuiButton addButton(int x, int y, int w, int h, String text) {
        return addButton(x, y, w, h, text, true, true);
    }

    public GuiButton addButton(int x, int y, int w, int h, String text, boolean enabled, boolean visible) {
        GuiButton button = new GuiButton(lastButtonId++, x, y, w, h, text);
        button.enabled = enabled;
        button.visible = visible;

        buttonList.add(button);

        return button;
    }

    public SideButton addSideButton(SideButton button) {
        button.id = lastButtonId++;
        button.x = guiLeft + -SideButton.WIDTH - 2;
        button.y = guiTop + lastSideButtonY;

        lastSideButtonY += SideButton.HEIGHT + 2;

        buttonList.add(button);

        return button;
    }

    public boolean inBounds(int x, int y, int w, int h, int ox, int oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }

    public void bindTexture(String file) {
        bindTexture(RS.ID, file);
    }

    public void bindTexture(String base, String file) {
        String id = base + ":" + file;

        if (!TEXTURE_CACHE.containsKey(id)) {
            TEXTURE_CACHE.put(id, new ResourceLocation(base, "textures/" + file));
        }

        mc.getTextureManager().bindTexture(TEXTURE_CACHE.get(id));
    }

    public void drawItem(int x, int y, ItemStack stack) {
        drawItem(x, y, stack, false);
    }

    public void drawItem(int x, int y, ItemStack stack, boolean withOverlay) {
        drawItem(x, y, stack, withOverlay, null);
    }

    public void drawItem(int x, int y, ItemStack stack, boolean withOverlay, @Nullable String text) {
        zLevel = 200.0F;
        itemRender.zLevel = 200.0F;

        try {
            itemRender.renderItemIntoGUI(stack, x, y);
        } catch (Throwable t) {
            // NO OP
        }

        if (withOverlay) {
            drawItemOverlay(stack, text, x, y);
        }

        zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    public void drawItemOverlay(ItemStack stack, @Nullable String text, int x, int y) {
        try {
            itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, "");
        } catch (Throwable t) {
            // NO OP
        }

        if (text != null) {
            drawQuantity(x, y, text);
        }
    }

    public void drawQuantity(int x, int y, String qty) {
        boolean large = fontRenderer.getUnicodeFlag() || RS.INSTANCE.config.largeFont;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 1);

        if (!large) {
            GlStateManager.scale(0.5f, 0.5f, 1);
        }

        GlStateManager.disableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableDepth();

        fontRenderer.drawStringWithShadow(qty, (large ? 16 : 30) - fontRenderer.getStringWidth(qty), large ? 8 : 22, 16777215);

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void drawString(int x, int y, String message) {
        drawString(x, y, message, 4210752);
    }

    public void drawString(int x, int y, String message, int color) {
        GlStateManager.disableLighting();
        fontRenderer.drawString(message, x, y, color);
        GlStateManager.enableLighting();
    }

    public void drawTooltip(@Nonnull ItemStack stack, int x, int y, String lines) {
        drawTooltip(stack, x, y, Arrays.asList(lines.split("\n")));
    }

    public void drawTooltip(int x, int y, String lines) {
        drawTooltip(ItemStack.EMPTY, x, y, lines);
    }

    public void drawTooltip(@Nonnull ItemStack stack, int x, int y, List<String> lines) {
        GlStateManager.disableLighting();
        GuiUtils.drawHoveringText(stack, lines, x, y, width - guiLeft, height, -1, fontRenderer);
        GlStateManager.enableLighting();
    }

    public void drawTexture(int x, int y, int textureX, int textureY, int width, int height) {
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public static String t(String name, Object... format) {
        return I18n.format(name, format);
    }

    public abstract void init(int x, int y);

    public abstract void update(int x, int y);

    public abstract void drawBackground(int x, int y, int mouseX, int mouseY);

    public abstract void drawForeground(int mouseX, int mouseY);

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    public static <T> void executeLater(Class<T> clazz, Consumer<T> callback) {
        Queue<Consumer> queue = RUNNABLES.get(clazz);

        if (queue == null) {
            RUNNABLES.put(clazz, queue = new ArrayDeque<>());
        }

        queue.add(callback);
    }

    public static void executeLater(Consumer<GuiContainer> callback) {
        executeLater(GuiContainer.class, callback);
    }
}
