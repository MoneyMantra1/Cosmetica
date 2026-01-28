package benzenestudios.sulphate;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ClassicButton extends Button {
	@FunctionalInterface
	public interface OnTooltip {
		void renderTooltip(Button button, GuiGraphics graphics, int mouseX, int mouseY);
	}

	private final OnTooltip tooltip;

	public ClassicButton(int x, int y, int width, int height, Component message, OnPress onPress) {
		this(x, y, width, height, message, onPress, null);
	}

	public ClassicButton(int x, int y, int width, int height, Component message, OnPress onPress, OnTooltip tooltip) {
		super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
		this.tooltip = tooltip;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.renderWidget(graphics, mouseX, mouseY, partialTick);
		if (this.tooltip != null && this.isHoveredOrFocused()) {
			this.tooltip.renderTooltip(this, graphics, mouseX, mouseY);
		}
	}
}
