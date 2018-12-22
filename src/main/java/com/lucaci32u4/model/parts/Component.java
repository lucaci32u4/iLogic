package com.lucaci32u4.model.parts;

import com.lucaci32u4.model.library.LibComponent;
import com.lucaci32u4.model.Subcurcuit;
import com.lucaci32u4.model.parts.wiring.Connectable;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import lombok.Getter;

import com.lucaci32u4.core.LogicPin;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("squid:S1659")
public class Component {
	private final LibComponent libComponent;
	private int posX = 0, posY = 0;
	private int width = 0, height = 0;
	private @Getter boolean ghosting = false;
	private final AtomicBoolean selected = new AtomicBoolean();
	private Subcurcuit subcircuit = null;
	
	public Component(@NotNull LibComponent libComponent, Subcurcuit subcircuit) {
		this.libComponent = libComponent;
		this.subcircuit = subcircuit;
		libComponent.onAttach(this);
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
		libComponent.onInteractiveClick(x, y);
		invalidateGraphics();
	}

	public void render(RenderAPI pencil, boolean attach, boolean detach) {
		libComponent.onDraw(pencil);
		for (Termination termination : libComponent.getTerminations()) {
			termination.render(pencil);
		}
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
		void render(RenderAPI pencil) {
		
		}
		
		@Override public int getConnectPositionX() {
			return connectX;
		}
		@Override public int getConnectPositionY() {
			return connectY;
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
		void onAttach(Component componentContainer);
		void onChangePosition(int x, int y);
		void onChangeDimension(int width, int height);
		void onInteractiveClick(int x, int y);
		Termination[] getTerminations();
		void onDetach();
	}
}
