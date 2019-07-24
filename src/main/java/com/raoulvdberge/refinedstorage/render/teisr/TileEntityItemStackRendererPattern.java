package com.raoulvdberge.refinedstorage.render.teisr;

import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.ItemPattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class TileEntityItemStackRendererPattern extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack) {
    	renderByItem(stack, 1.0F);
    }

	@Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        CraftingPattern pattern = ItemPattern.getPatternFromCache(null, stack);
        ItemStack outputStack = pattern.getOutputs().get(0);

		GlStateManager.pushMatrix();
		if(handleBrokenRendering(outputStack)) {
	        RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
	        IBakedModel model = renderer.getItemModelWithOverrides(outputStack, null, null);
	        
			renderer.renderItem(outputStack, model);
		}else {
			outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack, partialTicks);
		}
		GlStateManager.popMatrix();
    }
	
	private boolean handleBrokenRendering(ItemStack stack) {
		String creatorModId = stack.getItem().getCreatorModId(stack);
		if(creatorModId == null) return false;
		
		switch(creatorModId) {
			case "gregtech":
			case "gtadditions":
				 if ("tile.pipe".equals(stack.getTranslationKey()) || "machine".equals(stack.getItem().delegate.name().getPath())) {
		            	GlStateManager.translate(0.5, 0.5, 0.5);
		                return true;
		            }
				break;
				
			default:
				break;
		}
        return false;
    }
}