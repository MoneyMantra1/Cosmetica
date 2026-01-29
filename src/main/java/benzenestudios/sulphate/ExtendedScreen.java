package benzenestudios.sulphate;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface ExtendedScreen {
	List<? extends GuiEventListener> getChildren();

	List<? extends Renderable> getWidgets();

	/**
	 * @deprecated Screen.title is final in 1.21.1+. Use a separate field for dynamic titles.
	 */
	@Deprecated
	void setTitle(Component title);
}
