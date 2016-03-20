package refinedstorage.tile.settings;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum RedstoneMode
{
	IGNORE(0),
	HIGH(1),
	LOW(2);

	public static final String NBT = "RedstoneMode";

	public final int id;

	RedstoneMode(int id)
	{
		this.id = id;
	}

	public RedstoneMode next()
	{
		RedstoneMode next = getById(id + 1);

		if (next == null)
		{
			return getById(0);
		}

		return next;
	}

	public boolean isEnabled(World world, BlockPos pos)
	{
		switch (this)
		{
			case IGNORE:
				return true;
			case HIGH:
				return world.isBlockPowered(pos);
			case LOW:
				return !world.isBlockPowered(pos);
		}

		return false;
	}

	public static RedstoneMode getById(int id)
	{
		for (RedstoneMode control : values())
		{
			if (control.id == id)
			{
				return control;
			}
		}

		return null;
	}
}
