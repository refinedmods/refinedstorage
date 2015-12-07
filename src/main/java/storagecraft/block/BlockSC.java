package storagecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import storagecraft.SC;

public class BlockSC extends Block {
	private String name;

	public BlockSC(String name) {
		super(Material.rock);

		this.name = name;

		this.setCreativeTab(SC.TAB);
	}

	@Override
	public String getUnlocalizedName() {
		return "block." + SC.ID + ":" + name;
	}
}
