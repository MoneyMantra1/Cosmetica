package benzenestudios.sulphate;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface ExtendedScreen {
	List<? extends GuiEventListener> getChildren();

	List<? extends Renderable> getWidgets();

	void setTitle(Component title);
}
