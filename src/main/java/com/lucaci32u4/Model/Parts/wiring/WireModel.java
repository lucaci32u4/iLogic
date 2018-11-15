package com.lucaci32u4.model.parts.wiring;

import com.lucaci32u4.UI.Viewport.Renderer.DrawAPI;
import com.lucaci32u4.UI.Viewport.Renderer.VisualArtifact;
import com.lucaci32u4.main.Const;
import com.lucaci32u4.model.Subcurcuit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class WireModel implements VisualArtifact {
	private static final long DELTA_WIDTH = Long.parseLong(Const.query("dimensions.wireWidth")) >>> 1;
	private static final long X_MASK = 0xFFFFFFFF00000000L;
	private static final long Y_MASK = 0x00000000FFFFFFFFL;
	private static final long X_SHIFT = 32;
	private static final long Y_SHIFT = 0;
	private Subcurcuit subcircuit = null;
	private final AtomicLong boundsPosition = new AtomicLong();
	private final AtomicLong boundsDimension = new AtomicLong();
	private volatile AtomicLongArray wiresPos1 = new AtomicLongArray(0);
	private volatile AtomicLongArray wiresPos2 = new AtomicLongArray(0);
	private volatile ArrayList<Integer> selected = new ArrayList<>();
	private boolean isAreaSelecting = false;
	
	
	public void attach(Subcurcuit subcircuit) {
		this.subcircuit = subcircuit;
		if (wiresPos1.length() != 0) subcircuit.invalidateGraphics();
	}
	
	public boolean selectArea(long lt, long rb) {
		isAreaSelecting = pickAt(lt, rb, true) != 0;
		if (isAreaSelecting) subcircuit.invalidateGraphics();
		return isAreaSelecting;
	}
	
	public void endAreaSelection() {
		isAreaSelecting = false;
		subcircuit.invalidateGraphics();
	}
	
	public boolean select(long pos) {
		boolean hasSelected = pickAt(pos, pos, false) != 0;
		if (hasSelected) subcircuit.invalidateGraphics();
		return hasSelected;
	}
	
	public void deselect() {
		subcircuit.invalidateGraphics();
		selected = new ArrayList<>();
	}
	
	private int pickAt(long lt, long rb, boolean area) {
		final int length = wiresPos1.length();
		long end1;
		long end2;
		long aux;
		for (int i = 0; i < length; i++) {
			end1 = wiresPos1.get(i);
			end2 = wiresPos2.get(i);
			if (end1 > end2) {
				aux = end1; end1 = end2; end2 = aux;
			}
			if ((X_MASK & (~(end1 ^ end2))) == X_MASK) {
				end1 += (DELTA_WIDTH) << X_SHIFT; // make LT
				end2 -= (DELTA_WIDTH) << X_SHIFT; // make RB
				
			}
			if ((Y_MASK & (~(end1 ^ end2))) == Y_MASK) {
				end1 += (DELTA_WIDTH) << Y_SHIFT; // make LT
				end2 -= (DELTA_WIDTH) << Y_SHIFT; // make RB
			}
			if (area) {
				aux = end1; end1 = lt; lt = aux;
				aux = end2; end2 = rb; rb = aux;
			} 
			if ((end1 & X_MASK) >= (lt & X_MASK) && (lt & X_MASK) >= (end1 & X_MASK)) if ((end1 & Y_MASK) >= (lt & Y_MASK) && (lt & Y_MASK) >= (end2 & Y_MASK)) {
				if (area) {
					if ((end1 & X_MASK) >= (rb & X_MASK) && (rb & X_MASK) >= (end1 & X_MASK)) if ((end1 & Y_MASK) >= (rb & Y_MASK) && (rb & Y_MASK) >= (end2 & Y_MASK)) {
						selected.add(i);
					}
				} else {
					selected.add(i);
				}
			}
		}
		return selected.size();
	}
	
	private boolean[] drawMemory = null;
	@Override public void onDraw(@NotNull DrawAPI graphics, boolean attach, boolean detach) {
		AtomicLongArray w1 = wiresPos1;
		AtomicLongArray w2 = wiresPos2;
		ArrayList<Integer> select = selected;
		int length = w1.length();
		if (attach) {
			drawMemory = new boolean[length];
		}
		if (drawMemory.length != w1.length()) {
			drawMemory = new boolean[length];
		}
		for (int index : select) {
			drawMemory[index] = true;
		}
		for (int i = 0; i < length; i++) {
			drawWire(w1.get(i), w2.get(i), drawMemory[i]);
			drawMemory[i] = false;
		}
		if (detach) {
			drawMemory = null;
		}
	}
	
	private void drawWire(long end1, long end2, boolean selected) {
		// TODO: Wire drawing code
	}
	
	@Override public long getPosition() {
		return boundsPosition.get();
	}
	
	@Override public long getDimension() {
		return boundsDimension.get();
	}
}
