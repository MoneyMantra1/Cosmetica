package benzenestudios.sulphate;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public abstract class SulphateScreen extends Screen {
	private static final int DEFAULT_BUTTON_WIDTH = 150;
	private static final int DEFAULT_BUTTON_HEIGHT = 20;

	protected SulphateScreen(Component title, Screen parent) {
		super(title);
		this.parent = parent;
	}

	protected Screen parent;

	private Anchor anchorX = Anchor.CENTRE;
	private Anchor anchorY = Anchor.CENTRE;
	private IntSupplier anchorXOffset = () -> this.width / 2;
	private IntSupplier anchorYOffset = () -> this.height / 2;
	private int columns = 1;
	private int xSeparation = 10;
	private int ySeparation = 24;

	private final List<AbstractWidget> layoutWidgets = new ArrayList<>();

	protected abstract void addWidgets();

	protected void afterInit() {
	}

	@Override
	protected void init() {
		this.clearWidgets();
		this.layoutWidgets.clear();
		this.addWidgets();
		this.layoutWidgets();
		this.afterInit();
	}

	public void setRows(int columns) {
		this.columns = Math.max(1, columns);
	}

	public int getXSeparation() {
		return this.xSeparation;
	}

	public void setXSeparation(int xSeparation) {
		this.xSeparation = xSeparation;
	}

	public int getYSeparation() {
		return this.ySeparation;
	}

	public void setYSeparation(int ySeparation) {
		this.ySeparation = ySeparation;
	}

	public void setAnchorX(Anchor anchor, IntSupplier offsetSupplier) {
		this.anchorX = anchor;
		this.anchorXOffset = offsetSupplier;
	}

	public void setAnchorY(Anchor anchor, IntSupplier offsetSupplier) {
		this.anchorY = anchor;
		this.anchorYOffset = offsetSupplier;
	}

	protected AbstractButton addDone() {
		return addDone(this.height - 40);
	}

	protected AbstractButton addDone(int y) {
		ClassicButton button = new ClassicButton(this.width / 2 - 100, y, 200, 20, CommonComponents.GUI_DONE, b -> this.onClose());
		return this.addRenderableWidget(button);
	}

	protected <T extends ClassicButton> T addDoneWithOffset(ButtonFactory<T> factory, int offset) {
		int y = this.height - 40 - offset;
		T button = factory.create(this.width / 2 - 100, y, 200, 20, CommonComponents.GUI_DONE, b -> this.onClose(), null);
		this.addRenderableWidget(button);
		return button;
	}

	protected Button addButton(Component text, Button.OnPress onPress) {
		return addButton(DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, text, onPress, null);
	}

	protected Button addButton(Component text, Button.OnPress onPress, ClassicButton.OnTooltip tooltip) {
		return addButton(DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, text, onPress, tooltip);
	}

	protected Button addButton(int width, int height, Component text, Button.OnPress onPress) {
		return addButton(width, height, text, onPress, null);
	}

	protected Button addButton(int width, int height, Component text, Button.OnPress onPress, ClassicButton.OnTooltip tooltip) {
		ClassicButton button = new ClassicButton(0, 0, width, height, text, onPress, tooltip);
		this.layoutWidgets.add(button);
		return this.addRenderableWidget(button);
	}

	protected <T extends AbstractWidget> T addWidget(T widget) {
		this.layoutWidgets.add(widget);
		if (!this.renderables.contains(widget)) {
			this.addRenderableWidget(widget);
		}
		return widget;
	}

	protected <T extends AbstractWidget> T addWidget(WidgetFactory<T> factory, Component text) {
		return addWidget(factory, text, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
	}

	protected <T extends AbstractWidget> T addWidget(WidgetFactory<T> factory, Component text, int width, int height) {
		T widget = factory.create(0, 0, width, height, text);
		return addWidget(widget);
	}

	private void layoutWidgets() {
		if (this.layoutWidgets.isEmpty()) {
			return;
		}

		int totalWidgets = this.layoutWidgets.size();
		int rows = (int) Math.ceil(totalWidgets / (double) this.columns);

		int[] columnWidths = new int[this.columns];
		int[] rowHeights = new int[rows];

		for (int i = 0; i < totalWidgets; i++) {
			AbstractWidget widget = this.layoutWidgets.get(i);
			int column = i % this.columns;
			int row = i / this.columns;
			columnWidths[column] = Math.max(columnWidths[column], widget.getWidth());
			rowHeights[row] = Math.max(rowHeights[row], widget.getHeight());
		}

		int totalWidth = 0;
		for (int width : columnWidths) {
			totalWidth += width;
		}
		totalWidth += this.xSeparation * Math.max(0, this.columns - 1);

		int totalHeight = 0;
		for (int height : rowHeights) {
			totalHeight += height;
		}
		totalHeight += this.ySeparation * Math.max(0, rows - 1);

		int anchorX = this.anchorXOffset.getAsInt();
		int anchorY = this.anchorYOffset.getAsInt();

		int startX = switch (this.anchorX) {
			case LEFT -> anchorX;
			case RIGHT -> anchorX - totalWidth;
			case CENTRE -> anchorX - totalWidth / 2;
			default -> anchorX;
		};

		int startY = switch (this.anchorY) {
			case TOP -> anchorY;
			case BOTTOM -> anchorY - totalHeight;
			case CENTRE -> anchorY - totalHeight / 2;
			default -> anchorY;
		};

		int[] columnX = new int[this.columns];
		int currentX = startX;
		for (int col = 0; col < this.columns; col++) {
			columnX[col] = currentX;
			currentX += columnWidths[col] + this.xSeparation;
		}

		int[] rowY = new int[rows];
		int currentY = startY;
		for (int row = 0; row < rows; row++) {
			rowY[row] = currentY;
			currentY += rowHeights[row] + this.ySeparation;
		}

		for (int i = 0; i < totalWidgets; i++) {
			AbstractWidget widget = this.layoutWidgets.get(i);
			int column = i % this.columns;
			int row = i / this.columns;
			int x = columnX[column] + (columnWidths[column] - widget.getWidth()) / 2;
			int y = rowY[row] + (rowHeights[row] - widget.getHeight()) / 2;
			widget.setX(x);
			widget.setY(y);
		}
	}

	@FunctionalInterface
	protected interface WidgetFactory<T extends AbstractWidget> {
		T create(int x, int y, int width, int height, Component component);
	}

	@FunctionalInterface
	protected interface ButtonFactory<T extends ClassicButton> {
		T create(int x, int y, int width, int height, Component component, Button.OnPress onPress, ClassicButton.OnTooltip tooltip);
	}
}
