package refinedstorage.gui;

import org.lwjgl.input.Mouse;

public class Scrollbar
{
	private boolean canScroll = true;

	private int x;
	private int y;
	private int scrollbarWidth;
	private int scrollbarHeight;

	private float currentScroll;
	private boolean wasClicking = false;
	private boolean isScrolling = false;

	public Scrollbar(int x, int y, int scrollbarWidth, int scrollbarHeight)
	{
		this.x = x;
		this.y = y;
		this.scrollbarWidth = scrollbarWidth;
		this.scrollbarHeight = scrollbarHeight;
	}

	public void setCanScroll(boolean canScroll)
	{
		this.canScroll = canScroll;
	}

	public boolean canScroll()
	{
		return canScroll;
	}

	public float getCurrentScroll()
	{
		return currentScroll;
	}

	public void setCurrentScroll(float newCurrentScroll)
	{
		if (newCurrentScroll < 0)
		{
			newCurrentScroll = 0;
		}

		int scrollbarItselfHeight = 12;

		int max = scrollbarHeight - scrollbarItselfHeight - 3;

		if (newCurrentScroll > max)
		{
			newCurrentScroll = max;
		}

		currentScroll = newCurrentScroll;
	}

	public void draw(GuiBase gui)
	{
		gui.bindTexture("icons.png");
		gui.drawTexture(gui.guiLeft + x, gui.guiTop + y + (int) currentScroll, canScroll() ? 232 : 244, 0, 12, 15);
	}

	public void update(GuiBase gui, int mouseX, int mouseY)
	{
		if (!canScroll())
		{
			isScrolling = false;
			wasClicking = false;
			currentScroll = 0;
		}
		else
		{
			int wheel = Mouse.getDWheel();

			wheel = Math.max(Math.min(-wheel, 1), -1);

			float delta = 15;

			if (wheel == -1)
			{
				setCurrentScroll(currentScroll - delta);
			}
			else if (wheel == 1)
			{
				setCurrentScroll(currentScroll + delta);
			}

			boolean down = Mouse.isButtonDown(0);

			if (!wasClicking && down && gui.inBounds(x, y, scrollbarWidth, scrollbarHeight, mouseX, mouseY))
			{
				isScrolling = true;
			}

			if (!down)
			{
				isScrolling = false;
			}

			wasClicking = down;

			if (isScrolling)
			{
				setCurrentScroll(mouseY - 20);
			}
		}
	}
}
