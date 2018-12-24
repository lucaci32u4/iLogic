package com.lucaci32u4.model.parts.wiring;

import com.lucaci32u4.model.CoordinateHelper;
import com.lucaci32u4.main.Const;
import com.lucaci32u4.model.Subcurcuit;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import com.lucaci32u4.ui.viewport.renderer.brush.Brush;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicIntegerArray;

@SuppressWarnings("squid:S1659")
public class WireModel {
	private static final long DELTA_WIDTH = Long.parseLong(Const.query("dimensions.wireWidth")) / 2;
	private Subcurcuit subcircuit = null;
	private int boundsX = 0, boundsY = 0;
	private int width = 0, height = 0;
	private volatile AtomicIntegerArray wiresPos1X = new AtomicIntegerArray(0);
	private volatile AtomicIntegerArray wiresPos2X = new AtomicIntegerArray(0);
	private volatile AtomicIntegerArray wiresPos1Y = new AtomicIntegerArray(0);
	private volatile AtomicIntegerArray wiresPos2Y = new AtomicIntegerArray(0);
	private volatile ArrayList<Integer> selected = new ArrayList<>();
	private boolean isAreaSelecting = false;
	
	// Expansion variables
	private volatile boolean expanding = false;
	private boolean hasDirection = false;
	private boolean dirVertical = false;
	private int beginX = 0, beginY = 0;
	private volatile int extensionCount = 0;
	private int ext1X = 0, ext1Y = 0, ext2X = 0, ext2Y = 0;
	private int sup1X = 0, sup1Y = 0, sup2X = 0, sup2Y = 0;
	
	
	public void attach(Subcurcuit subcircuit) {
		this.subcircuit = subcircuit;
		if (wiresPos1X.length() != 0) subcircuit.invalidateGraphics();
	}
	
	public boolean selectArea(int l, int t, int r, int b) {
		if (l > r) { l = l ^ r; r = l ^ r; l = l ^ r; }
		if (t > b) { t = t ^ b; b = t ^ b; t = t ^ b; }
		isAreaSelecting = pickAt(l, t, r, b, true) != 0;
		if (isAreaSelecting) subcircuit.invalidateGraphics();
		return isAreaSelecting;
	}
	
	public void endAreaSelection() {
		isAreaSelecting = false;
		subcircuit.invalidateGraphics();
	}
	
	public boolean select(int x, int y) {
		boolean hasSelected = pickAt(x, y, x, y, false) != 0;
		if (hasSelected) subcircuit.invalidateGraphics();
		return hasSelected;
	}
	
	public void deselect() {
		subcircuit.invalidateGraphics();
		selected = new ArrayList<>();
	}
	
	public void beginExpand(int x, int y) {
		expanding = true;
		hasDirection = false;
		beginX = x;
		beginY = y;
	}
	
	public void continueExpand(int x, int y) {
		System.out.println("expanding...");
		extensionCount = 0;
		if (x != beginX || y != beginY) {
			if (!hasDirection) {
				dirVertical = Math.abs(beginX - x) < Math.abs(beginY - y);
				hasDirection = true;
				ext1X = beginX;
				ext1Y = beginY;
			}
			extensionCount = 1;
			if (x != beginX && y != beginY) {
				extensionCount = 2;
			}
			if (dirVertical) {
				ext2X = ext1X;
				ext2Y = y;
				if (extensionCount == 2) {
					sup1X = ext2X;
					sup1Y = ext2Y;
					sup2X = x;
					sup2Y = sup1Y;
				}
			} else {
				ext2X = x;
				ext2Y = ext1Y;
				if (extensionCount == 2) {
					sup1X = ext2X;
					sup1Y = ext2Y;
					sup2X = sup1X;
					sup2Y = y;
				}
			}
		}
		subcircuit.invalidateGraphics();
	}
	
	public void endExpand() {
		expanding = false;
		AtomicIntegerArray old1X = wiresPos1X, old1Y = wiresPos1Y, old2X = wiresPos2X, old2Y = wiresPos2Y;
		int oldLength = old1X.length();
		int newLength = oldLength + extensionCount;
		if (extensionCount != 0) {
			wiresPos1X = new AtomicIntegerArray(newLength);
			wiresPos1Y = new AtomicIntegerArray(newLength);
			wiresPos2X = new AtomicIntegerArray(newLength);
			wiresPos2Y = new AtomicIntegerArray(newLength);
			for (int i = 0; i < oldLength; i++) {
				wiresPos1X.set(i, old1X.get(i));
				wiresPos1Y.set(i, old1Y.get(i));
				wiresPos2X.set(i, old2X.get(i));
				wiresPos2Y.set(i, old2Y.get(i));
			}
			if (extensionCount >= 1) {
				if (ext1X > ext2X) { ext1X = ext1X ^ ext2X; ext2X = ext1X ^ ext2X; ext1X = ext1X ^ ext2X; }
				else if (ext1Y > ext2Y) { ext1Y = ext1Y ^ ext2Y; ext2Y = ext1Y ^ ext2Y; ext1Y = ext1Y ^ ext2Y; }
				wiresPos1X.set(oldLength, ext1X);
				wiresPos1Y.set(oldLength, ext1Y);
				wiresPos2X.set(oldLength, ext2X);
				wiresPos2Y.set(oldLength, ext2Y);
			}
			if (extensionCount >= 2) {
				if (sup1X > sup2X) { sup1X = sup1X ^ sup2X; sup2X = sup1X ^ sup2X; sup1X = sup1X ^ sup2X; }
				else if (sup1Y > sup2Y) { sup1Y = sup1Y ^ sup2Y; sup2Y = sup1Y ^ sup2Y; sup1Y = sup1Y ^ sup2Y; }
				wiresPos1X.set(oldLength + 1, sup1X);
				wiresPos1Y.set(oldLength + 1, sup1Y);
				wiresPos2X.set(oldLength + 1, sup2X);
				wiresPos2Y.set(oldLength + 1, sup2Y);
			}
			int left = Math.min(boundsX, ext1X);
			int top = Math.min(boundsY, ext1Y);
			int right = Math.max(boundsX + width, ext2X);
			int bottom = Math.max(boundsY + height, ext2Y);
			if (extensionCount >= 2) {
				left = Math.min(left, sup1X);
				top = Math.min(top, sup1Y);
				right = Math.max(right, sup2X);
				bottom = Math.max(bottom, sup2Y);
			}
			boundsX = left;
			boundsY = top;
			width = right - left;
			height = top - bottom;
		}
		subcircuit.invalidateGraphics();
	}
	
	@SuppressWarnings("all")
	private int pickAt(int l, int t, int r, int b, boolean area) {
		final int length = wiresPos1X.length();
		int end1X = 0, end1Y = 0;
		int end2X = 0, end2Y = 0;
		int aux;
		for (int i = 0; i < length; i++) {
			end1X = wiresPos1X.get(i);
			end1Y = wiresPos1Y.get(i);
			end2X = wiresPos2X.get(i);
			end2Y = wiresPos2Y.get(i);
			if (end1X == end2X) {
				if (end1Y < end2Y) { aux = end1Y; end1Y = end2Y; end2Y = aux; }
				end1X -= DELTA_WIDTH; // make LT
				end2X += DELTA_WIDTH; // make RB
			} else if (end1Y == end2Y) {
				if (end1X > end2Y) { aux = end1X; end1X = end2X; end2X = aux; }
				end1Y += DELTA_WIDTH; // make LT
				end2Y -= DELTA_WIDTH; // make RB
			} else {
				// Broken coordinates!
			}
			if (area) {
				if (CoordinateHelper.intersectRect(l, t, r, b, end1X, end1Y, end2X, end2Y)) {
					selected.add(i);
				}
			} else {
				if (CoordinateHelper.intersectRect(end1X, end1Y, end2X, end2Y, l, t, r, b)) {
					selected.add(i);
					break;
				}
			}
		}
		return selected.size();
	}
	
	private boolean[] drawMemory = null;
	public void onDraw(@NotNull RenderAPI graphics, boolean attach, boolean detach) {
		AtomicIntegerArray w1x = wiresPos1X;
		AtomicIntegerArray w1y = wiresPos1Y;
		AtomicIntegerArray w2x = wiresPos2X;
		AtomicIntegerArray w2y = wiresPos2Y;
		ArrayList<Integer> select = selected;
		int length = w1x.length();
		if (drawMemory == null) {
			drawMemory = new boolean[length];
		}
		if (drawMemory.length != length) {
			drawMemory = new boolean[length];
		}
		for (int index : select) {
			drawMemory[index] = true;
		}
		for (int i = 0; i < length; i++) {
			drawWire(graphics, w1x.get(i), w1y.get(i), w2x.get(i), w2y.get(i), drawMemory[i], false);
			drawMemory[i] = false;
		}
		if (expanding) {
			if (extensionCount >= 1) {
				drawWire(graphics, ext1X, ext1Y, ext2X, ext2Y, false, true);
			}
			if (extensionCount >= 2) {
				drawWire(graphics, sup1X, sup1Y, sup2X, sup2Y, false, true);
			}
		}
		if (detach) {
			drawMemory = null;
		}
	}

	private static Brush wireBrush = null;
	private void drawWire(RenderAPI graphics, int fromX, int fromY, int toX, int toY, boolean selected, boolean extension) {
		if (wireBrush == null) wireBrush = graphics.createSolidBrush(127, 127, 127);
		graphics.setBrush(wireBrush);
		graphics.drawLine(fromX, fromY, toX, toY, DELTA_WIDTH * 2);
		// TODO: (lucaci32u4, 23/12/18): Convert wire drawing from lines to rectangles in the nearest stable version
	}
}
