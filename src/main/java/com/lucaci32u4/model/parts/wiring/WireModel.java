package com.lucaci32u4.model.parts.wiring;

import com.lucaci32u4.ui.viewport.renderer.DrawAPI;
import com.lucaci32u4.main.Const;
import com.lucaci32u4.model.Subcurcuit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class WireModel {
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
	
	// Expansion variables
	private volatile boolean expanding = false;
	private boolean hasDirection = false;
	private boolean dirVertical = false;
	private long begin = 0;
	private volatile int extensionCount = 0;
	private volatile long ext1 = 0;
	private volatile long ext2 = 0;
	private volatile long sup1 = 0;
	private volatile long sup2 = 0;
	
	
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
	
	public void beginExpand(long beginPos) {
		expanding = true;
		hasDirection = false;
		begin = beginPos;
	}
	
	public void continueExpand(long pos) {
		if (!hasDirection) {
			dirVertical = (pos << X_SHIFT) == (begin << X_SHIFT);
			hasDirection = true;
		}
		ext1 = begin;
		extensionCount = 0;
		if (pos != begin) {
			extensionCount = 1;
			if ((pos << X_SHIFT) != (begin << X_SHIFT) && (pos << Y_SHIFT) != (begin << Y_SHIFT)) {
				extensionCount = 2;
			}
			if (dirVertical) {
				ext2 = (ext1 & X_MASK) | (pos & Y_MASK);
				if (extensionCount == 2) {
					sup1 = ext2;
					sup2 = (sup1 & Y_MASK) | (pos & X_MASK);
				}
			} else {
				ext2 = (ext1 & Y_MASK) | (pos & X_MASK);
				if (extensionCount == 2) {
					sup1 = ext2;
					sup2 = (sup1 & X_MASK) | (pos & Y_MASK);
				}
			}
		}
	}
	
	public void endExpand() {
		expanding = false;
		AtomicLongArray old1 = wiresPos1;
		AtomicLongArray old2 = wiresPos2;
		int oldLength = old1.length();
		int newLength = oldLength + extensionCount;
		if (extensionCount != 0) {
			wiresPos1 = new AtomicLongArray(newLength);
			wiresPos2 = new AtomicLongArray(newLength);
			for (int i = 0; i < oldLength; i++) {
				wiresPos1.set(i, old1.get(i));
				wiresPos2.set(i, old2.get(i));
			}
			if (extensionCount >= 1) {
				wiresPos1.set(oldLength, ext1);
				wiresPos2.set(oldLength, ext2);
			}
			if (extensionCount >= 2) {
				wiresPos1.set(oldLength + 1, sup1);
				wiresPos2.set(oldLength + 1, sup2);
			}
			long left = Math.min(boundsPosition.get() & X_MASK, Math.min(ext2 & X_MASK, ext1 & X_MASK));
			long top = Math.min(boundsPosition.get() << X_SHIFT, Math.min(ext2 << X_SHIFT, ext1 << X_SHIFT));
			long right = Math.max(boundsPosition.get() & X_MASK, Math.max(ext2 & X_MASK, ext1 & X_MASK));
			long bottom = Math.max(boundsPosition.get() << X_SHIFT, Math.max(ext2 << X_SHIFT, ext1 << X_SHIFT));
			if (extensionCount >= 2) {
				left = Math.min(left, Math.min(sup1 & X_MASK, sup2 & X_MASK));
				top = Math.min(top, Math.min(sup1 << X_SHIFT, sup2 << X_SHIFT));
				right = Math.max(right, Math.max(sup1 & X_MASK, sup2 & X_MASK));
				bottom = Math.max(bottom, Math.max(sup1 << X_SHIFT, sup2 << X_SHIFT));
			}
			right -= left;
			bottom -= top;
			right = right >>> X_SHIFT;
			bottom = bottom >>> X_SHIFT;
			boundsPosition.set(left | top);
			boundsPosition.set(right | bottom);
		}
	}
	
	@SuppressWarnings("all")
	private int pickAt(long lt, long rb, boolean area) {
		final int length = wiresPos1.length();
		long end1;
		long end2;
		long aux;
		for (int i = 0; i < length; i++) {
			end1 = wiresPos1.get(i);
			end2 = wiresPos2.get(i);
			
			if ((X_MASK & (~(end1 ^ end2))) == X_MASK) {
				if (end1 < end2) { aux = end1; end1 = end2; end2 = aux; }
				end1 -= (DELTA_WIDTH) << X_SHIFT; // make LT
				end2 += (DELTA_WIDTH) << X_SHIFT; // make RB
			}
			if ((Y_MASK & (~(end1 ^ end2))) == Y_MASK) {
				if (end1 > end2) { aux = end1; end1 = end2; end2 = aux; }
				// TODO: Not good. Does not work with two's complement!
				end1 += (DELTA_WIDTH) << Y_SHIFT; // make LT
				end2 -= (DELTA_WIDTH) << Y_SHIFT; // make RB
			}
			if (area) {
				aux = end1; end1 = lt; lt = aux;
				aux = end2; end2 = rb; rb = aux;
			} 
			if ((end1 & X_MASK) >= (lt & X_MASK) && (lt & X_MASK) >= (end1 & X_MASK)) if (((end1 & Y_MASK) << X_SHIFT) >= ((lt & Y_MASK) << X_SHIFT) && ((lt & Y_MASK) << X_SHIFT) >= ((end2 & Y_MASK) << X_SHIFT)) {
				if (area) {
					if ((end1 & X_MASK) >= (rb & X_MASK) && (rb & X_MASK) >= (end1 & X_MASK)) if (((end1 & Y_MASK) << X_SHIFT) >= ((lt & Y_MASK) << X_SHIFT) && ((lt & Y_MASK) << X_SHIFT) >= ((end2 & Y_MASK) << X_SHIFT)) {
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
	public void onDraw(@NotNull DrawAPI graphics, boolean attach, boolean detach) {
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
			drawWire(graphics, w1.get(i), w2.get(i), drawMemory[i], false);
			drawMemory[i] = false;
		}
		if (expanding) {
			if (extensionCount >= 1) drawWire(graphics, ext1, ext2, false, true);
			if (extensionCount >= 2) drawWire(graphics, sup1, sup2, false, true);
		}
		if (detach) {
			drawMemory = null;
		}
	}
	
	private void drawWire(DrawAPI graphics, long end1, long end2, boolean selected, boolean extension) {
		// TODO: Wire drawing code
	}
	
	public long getPosition() {
		return boundsPosition.get();
	}
	
	public long getDimension() {
		return boundsDimension.get();
	}
}
