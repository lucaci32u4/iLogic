package com.lucaci32u4.model.parts;

import java.util.concurrent.atomic.AtomicBoolean;
import com.lucaci32u4.core.LogicComponent;
import com.lucaci32u4.model.library.LibComponent;
import com.lucaci32u4.model.Subcurcuit;
import com.lucaci32u4.model.parts.wiring.Connectable;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import com.lucaci32u4.ui.viewport.renderer.brush.Brush;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import lombok.Getter;

import com.lucaci32u4.core.LogicPin;


@SuppressWarnings("squid:S1659")
public class Component {
	private final LibComponent libComponent;
	private int posX = 0, posY = 0;
	private int width = 0, height = 0;
	private @Getter boolean ghosting = false;
	private final AtomicBoolean selected = new AtomicBoolean();
	private final Subcurcuit subcircuit;
	private final Termination[] terminations;
	private final LogicComponent logicComponent;
	
	public Component(@NotNull LibComponent libComponent, @NotNull Subcurcuit subcircuit) {
		this.libComponent = libComponent;
		this.subcircuit = subcircuit;
		logicComponent = new LogicComponent(libComponent);
		libComponent.onAttach(this);
		subcircuit.getSimulator().addComponent(logicComponent);
		Termination[] terminations = libComponent.getTerminations();
		this.terminations = new Termination[terminations.length];
		System.arraycopy(terminations, 0, this.terminations, 0, terminations.length);
	}
	
	public int getPositionX() {
		return posX;
	}
	
	public int getPositionY() {
		return posY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public Component setWidth(int width) { this.width = width; return this; }

	public Component setHeight(int height) { this.height = height; return this; }

	public Termination[] getTerminations() {
		return terminations;
	}
	
	public void select(boolean isSel) {
		selected.set(isSel);
		invalidateGraphics();
	}
	
	public void move(int x, int y) {
		posX = x;
		posY = y;
		libComponent.onChangePosition(x, y);
		invalidateGraphics();
	}
	
	public void interact(int x, int y, boolean begin, boolean end) {
		if (begin) selected.set(true);
		if (end) selected.set(false);
		libComponent.onInteractiveClick(x, y, begin, end);
		invalidateGraphics();
	}

	public void render(RenderAPI pencil, boolean attach, boolean detach) {
		libComponent.onDraw(pencil);
	}

	public LogicComponent.Interrupter createNewInterrupter() {
		return new LogicComponent.Interrupter(logicComponent);
	}
	
	public void invalidateGraphics() {
		subcircuit.invalidateGraphics();
	}
	
	public static class Termination implements Connectable {
		public static final int STATE_TRISTATE = 0;
		public static final int STATE_HIGH = 1;
		public static final int STATE_LOW = 2;
		public static final int STATE_CONFLICT = 3;
		public static final int STATE_MULTIBIT = 4;
		public static final int TERMINATION_RADIUS = 6;
		private @Setter int state;
		private int connectX = 0, connectY = 0;
		private final @Getter Component owner;
		private final @Getter int bitWidth;
		private final @Getter LogicPin[] pins;
		public Termination(@NotNull Component owner, @NotNull LogicPin[] pins) {
			this.owner = owner;
			this.pins = pins;
			bitWidth = pins.length;
		}

		private static Brush terminationBrush = null;
		@Override public void render(RenderAPI graphics) {
			if (terminationBrush == null) terminationBrush = graphics.createOutlineBrush(127, 127, 127);
			graphics.setBrush(terminationBrush);
			graphics.drawRectangle(connectX - TERMINATION_RADIUS, connectY - TERMINATION_RADIUS, connectX + TERMINATION_RADIUS, connectY + TERMINATION_RADIUS);
		}
		
		@Override public int getConnectPositionX() {
			return connectX;
		}
		@Override public int getConnectPositionY() {
			return connectY;
		}
		@Override public int getConnectionRadius() {
			return TERMINATION_RADIUS;
		}
		public void setConnectPosiiton(int x, int y) {
			connectX = x;
			connectY = y;
		}
		
		@Override public boolean isMutable() {
			return false;
		}
	}
	
	public interface BehaviourSpecification {
		Termination[] getTerminations();
		void onAttach(Component componentContainer);
		void onChangePosition(int x, int y);
		void onInteractiveClick(int x, int y, boolean begin, boolean end);
		void onDetach();
	}
}
