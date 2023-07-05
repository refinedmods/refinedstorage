package com.refinedmods.refinedstorage.screen.grid;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class SmallClientTooltipComponent implements ClientTooltipComponent {
    private final Component component;
    private final float scale;

    public SmallClientTooltipComponent(Component component, float scale) {
        this.component = component;
        this.scale = scale;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f pose, MultiBufferSource.BufferSource buffer) {
        Matrix4f scaled = new Matrix4f(pose);
        scaled.scale(scale, scale, 1);
        font.drawInBatch(component, x / scale, y / scale, -1, true, scaled, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
    }

    @Override
    public int getHeight() {
        return 9;
    }

    @Override
    public int getWidth(Font font) {
        return (int) (font.width(component) * scale);
    }
}
