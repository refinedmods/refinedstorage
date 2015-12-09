package storagecraft.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.tile.TileCable;

public class CableModel extends ModelBase {
	private ModelRenderer core;
	private ModelRenderer up;
	private ModelRenderer down;
	private ModelRenderer north;
	private ModelRenderer east;
	private ModelRenderer south;
	private ModelRenderer west;

	public CableModel() {
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

	public void render(TileCable cable, float f, float f1, float f2, float f3, float f4, float f5) {
		core.render(f5);

		if (cable != null) {
			if (cable.hasConnection(ForgeDirection.UP)) {
				up.render(f5);
			}

			if (cable.hasConnection(ForgeDirection.DOWN)) {
				down.render(f5);
			}

			if (cable.hasConnection(ForgeDirection.NORTH)) {
				north.render(f5);
			}

			if (cable.hasConnection(ForgeDirection.EAST)) {
				east.render(f5);
			}

			if (cable.hasConnection(ForgeDirection.SOUTH)) {
				south.render(f5);
			}

			if (cable.hasConnection(ForgeDirection.WEST)) {
				west.render(f5);
			}
		} else {
			north.render(f5);
			south.render(f5);
		}
	}
}
