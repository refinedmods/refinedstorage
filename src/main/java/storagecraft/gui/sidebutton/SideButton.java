package storagecraft.gui.sidebutton;

import storagecraft.gui.GuiBase;

public abstract class SideButton {
	private int id;
	private int x;
	private int y;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public abstract String getTooltip(GuiBase gui);

	public abstract void draw(GuiBase gui, int x, int y);

	public abstract void actionPerformed();
}
