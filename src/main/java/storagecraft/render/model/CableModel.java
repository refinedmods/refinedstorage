package storagecraft.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import storagecraft.tile.TileCable;

public class CableModel extends ModelBase
{
	public static final ResourceLocation CABLE_RESOURCE = new ResourceLocation("storagecraft:textures/blocks/cable.png");
	public static final ResourceLocation CABLE_UNPOWERED_RESOURCE = new ResourceLocation("storagecraft:textures/blocks/cable_sensitive_unpowered.png");
	public static final ResourceLocation CABLE_POWERED_RESOURCE = new ResourceLocation("storagecraft:textures/blocks/cable_sensitive_powered.png");

	private ModelRenderer core;
	private ModelRenderer up;
	private ModelRenderer down;
	private ModelRenderer north;
	private ModelRenderer east;
	private ModelRenderer south;
	private ModelRenderer west;

	public CableModel()
	{
		core = new ModelRenderer(this, 0, 0);
		core.addBox(6F, 6F, 6F, 4, 4, 4);
		core.setTextureSize(16, 16);

		up = new ModelRenderer(this, 0, 0);
		up.addBox(6F, 10F, 6F, 4, 6, 4);
		up.setTextureSize(16, 16);

		down = new ModelRenderer(this, 0, 0);
		down.addBox(6F, 0F, 6F, 4, 6, 4);
		down.setTextureSize(16, 16);

		north = new ModelRenderer(this, 0, 0);
		north.addBox(6F, 6F, 0F, 4, 4, 6);
		north.setTextureSize(16, 16);

		east = new ModelRenderer(this, 0, 0);
		east.addBox(10F, 6F, 6F, 6, 4, 4);
		east.setTextureSize(16, 16);

		south = new ModelRenderer(this, 0, 0);
		south.addBox(6F, 6F, 10F, 4, 4, 6);
		south.setTextureSize(16, 16);

		west = new ModelRenderer(this, 0, 0);
		west.addBox(0F, 6F, 6F, 6, 4, 4);
		west.setTextureSize(16, 16);
	}

	public void render(ItemStack cable, float x)
	{
		if (cable.getItemDamage() == 1)
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_UNPOWERED_RESOURCE);
		}
		else
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_RESOURCE);
		}

		core.render(x);

		if (cable.getItemDamage() == 1)
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_RESOURCE);
		}

		north.render(x);
		south.render(x);
	}

	public void render(TileCable cable, float x)
	{
		if (cable.isSensitiveCable())
		{
			if (cable.isPowered())
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_POWERED_RESOURCE);
			}
			else
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_UNPOWERED_RESOURCE);
			}
		}
		else
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_RESOURCE);
		}

		core.render(x);

		if (cable.isSensitiveCable())
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_RESOURCE);
		}

		if (cable.hasConnection(EnumFacing.UP))
		{
			up.render(x);
		}

		if (cable.hasConnection(EnumFacing.DOWN))
		{
			down.render(x);
		}

		if (cable.hasConnection(EnumFacing.NORTH))
		{
			north.render(x);
		}

		if (cable.hasConnection(EnumFacing.EAST))
		{
			east.render(x);
		}

		if (cable.hasConnection(EnumFacing.SOUTH))
		{
			south.render(x);
		}

		if (cable.hasConnection(EnumFacing.WEST))
		{
			west.render(x);
		}
	}
}
