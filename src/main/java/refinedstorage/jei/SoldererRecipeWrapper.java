package refinedstorage.jei;

import com.google.common.base.Objects;
import java.util.Collections;
import java.util.List;
import mezz.jei.plugins.vanilla.VanillaRecipeWrapper;
import net.minecraft.item.ItemStack;

public class SoldererRecipeWrapper extends VanillaRecipeWrapper
{
	private int hashCode;
	private List<ItemStack> inputs;
	private ItemStack output;

	public SoldererRecipeWrapper(List<ItemStack> inputs, ItemStack output)
	{
		this.inputs = inputs;
		this.output = output;

		int available = 0;

		for (int i = 0; i < 3; ++i)
		{
			if (inputs.get(i) != null)
			{
				available = i;

				break;
			}
		}

		hashCode = Objects.hashCode(inputs.get(available), output);
	}

	@Override
	public List<ItemStack> getInputs()
	{
		return inputs;
	}

	@Override
	public List<ItemStack> getOutputs()
	{
		return Collections.singletonList(output);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SoldererRecipeWrapper))
		{
			return false;
		}

		SoldererRecipeWrapper other = (SoldererRecipeWrapper) obj;

		for (int i = 0; i < inputs.size(); i++)
		{
			if (!ItemStack.areItemStacksEqual(inputs.get(i), other.inputs.get(i)))
			{
				return false;
			}
		}

		return ItemStack.areItemStacksEqual(output, other.output);
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}

	@Override
	public String toString()
	{
		return inputs + " = " + output;
	}
}
