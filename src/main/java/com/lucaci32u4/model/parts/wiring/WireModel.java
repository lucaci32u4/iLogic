package com.lucaci32u4.model.parts.wiring;

import com.lucaci32u4.core.LogicNode;
import com.lucaci32u4.model.CoordinateHelper;
import com.lucaci32u4.main.Const;
import com.lucaci32u4.model.Subcurcuit;
import com.lucaci32u4.ui.viewport.renderer.RenderAPI;
import com.lucaci32u4.ui.viewport.renderer.brush.Brush;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("squid:S1659")
public class WireModel {
	private static final int DELTA_WIDTH = Integer.parseInt(Const.query("dimensions.wireWidth")) / 2;
	
	private Subcurcuit subcircuit = null;
	private int boundsX = 0, boundsY = 0;
	private int width = 0, height = 0;
	
	// Segment data
	private final Object segmentLock = new Object();
	private ArrayList<Pivot> pivots = new ArrayList<>();
	private ArrayList<Integer> wiresPos1 = new ArrayList<>();
	private ArrayList<Integer> wiresPos2 = new ArrayList<>();
	private volatile ArrayList<Integer> selected = new ArrayList<>();
	private boolean isAreaSelecting = false;
	private LogicNode[] node = null;

	// Brnch data
	private final Object branchLock = new Object();
	private Collection<Integer> branchPointsX = new ArrayDeque<>();
	private Collection<Integer> branchPointsY = new ArrayDeque<>();
	
	// Expansion variables
	private volatile boolean expanding = false;
	private boolean hasDirection = false;
	private boolean dirVertical = false;
	private volatile int extensionCount = 0;
	private final Pivot ext1 = new Pivot();
	private final Pivot ext2 = new Pivot();
	private final Pivot ext3 = new Pivot();
	
	
	public void attach(Subcurcuit subcircuit) {
		this.subcircuit = subcircuit;
		if (!wiresPos1.isEmpty()) subcircuit.invalidateGraphics();
		node = new LogicNode[1];
		for (int i = 0; i < node.length; i++) {
			node[i] = new LogicNode();
			subcircuit.getSimulator().addNode(node[i]);
		}
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
		if (!wiresPos1.isEmpty()) {
			select(x, y);
			int wireIndex = (!selected.isEmpty() ? selected.get(0) : -1);
			deselect();
			if (wireIndex >= 0) {
				Pivot p1 = pivots.get(wiresPos1.get(wireIndex));
				Pivot p2 = pivots.get(wiresPos2.get(wireIndex));
				if (p1.getX() == p2.getX()) {
					ext1.setX(p1.getX()).setY(CoordinateHelper.snapToGrid(y));
					ext2.copy(ext1);
					ext3.copy(ext2);
				} else if (p1.getY() == p2.getY()) {
					ext1.setY(p1.getY()).setX(CoordinateHelper.snapToGrid(x));
					ext2.copy(ext1);
					ext3.copy(ext2);
				} else throw new IllegalStateException();
			}
		} else {
			ext1.setX(x).setY(y);
		}
		expanding = true;
		hasDirection = false;
	}
	
	public void continueExpand(int x, int y) {
		extensionCount = 0;
		if (x != ext1.getX() || y != ext1.getY()) {
			if (!hasDirection) {
				dirVertical = Math.abs(ext1.getX() - x) < Math.abs(ext1.getY()- y);
				hasDirection = true;
			}
			extensionCount = 1;
			if (x != ext1.getX() && y != ext1.getY()) {
				extensionCount = 2;
			}
			if (dirVertical) {
				ext2.setX(ext1.getX());
				ext2.setY(y);
				if (extensionCount == 2) {
					ext3.setX(x);
					ext3.setY(ext2.getY());
				}
			} else {
				ext2.setX(x);
				ext2.setY(ext1.getY());
				if (extensionCount == 2) {
					ext3.setX(ext2.getX());
					ext3.setY(y);
				}
			}
		} else hasDirection = false;
		subcircuit.invalidateGraphics();
	}
	
	public void endExpand() {
		expanding = false;
		if (extensionCount != 0) {
			Pivot middle = null;
			if (extensionCount >= 2) {
				middle = new Pivot().copy(ext2);
			}
			synchronized (segmentLock) {
				if (extensionCount >= 1) {
					if (!pivots.contains(ext1)) pivots.add(new Pivot().copy(ext1));
					if (!pivots.contains(ext2)) pivots.add(new Pivot().copy(ext2));
					Pivot.sort(ext1, ext2);
					wiresPos1.add(pivots.indexOf(ext1));
					wiresPos2.add(pivots.indexOf(ext2));
				}
				if (extensionCount >= 2) {
					ext2.copy(middle);
					if (!pivots.contains(ext3)) pivots.add(new Pivot().copy(ext3));
					Pivot.sort(ext2, ext3);
					wiresPos1.add(pivots.indexOf(ext2));
					wiresPos2.add(pivots.indexOf(ext3));
				}
			}
			int left = Math.min(boundsX, ext1.getX());
			int top = Math.min(boundsY, ext1.getY());
			int right = Math.max(boundsX + width, ext2.getX());
			int bottom = Math.max(boundsY + height, ext2.getY());
			if (extensionCount >= 2) {
				right = Math.max(right, ext3.getX());
				bottom = Math.max(bottom, ext3.getY());
			}
			boundsX = left;
			boundsY = top;
			width = right - left;
			height = top - bottom;
			rescanWires();
		}
		subcircuit.invalidateGraphics();
	}
	
	@SuppressWarnings("all")
	private int pickAt(int l, int t, int r, int b, boolean area) {
		final int length = wiresPos1.size();
		int end1X = 0, end1Y = 0;
		int end2X = 0, end2Y = 0;
		int aux;
		for (int i = 0; i < length; i++) {
			Pivot p1 = pivots.get(wiresPos1.get(i));
			Pivot p2 = pivots.get(wiresPos2.get(i));
			end1X = p1.getX();
			end1Y = p1.getY();
			end2X = p2.getX();
			end2Y = p2.getY();
			if (end1X == end2X) {
				if (end1Y < end2Y) { aux = end1Y; end1Y = end2Y; end2Y = aux; }
				end1X -= DELTA_WIDTH; // make LT
				end2X += DELTA_WIDTH; // make RB
			} else if (end1Y == end2Y) {
				if (end1X > end2X) { aux = end1X; end1X = end2X; end2X = aux; }
				end1Y += DELTA_WIDTH; // make LT
				end2Y -= DELTA_WIDTH; // make RB
			} else {
				// Broken coordinates!
			}
			if (area) {
				if (CoordinateHelper.intersectRect(l, t, r, b, end1X, end1Y, end2X, end2Y)) {
					synchronized (segmentLock) {
						selected.add(i);
					}
				}
			} else {
				if (CoordinateHelper.intersectRect(end1X, end1Y, end2X, end2Y, l, t, r, b)) {
					synchronized (segmentLock) {
						selected.add(i);
					}
					break;
				}
			}
		}
		return selected.size();
	}
	
	private void rescanWires() {
		// Nothing yet
		subcircuit.invalidateGraphics();
	}

	public LogicNode[] getSimulatorNode() {
		return node;
	}
	
	public void onDraw(@NotNull RenderAPI graphics, boolean attach, boolean detach) {
		synchronized (segmentLock) {
			ArrayList<Integer> select = selected;
			int length = wiresPos1.size();
			for (int i = 0; i < length; i++) {
				Pivot p1 = pivots.get(wiresPos1.get(i));
				Pivot p2 = pivots.get(wiresPos2.get(i));
				drawWire(graphics, p1.getX(), p1.getY(), p2.getX(), p2.getY(), false, false);
			}
			for (int i : select) {
				Pivot p1 = pivots.get(wiresPos1.get(i));
				Pivot p2 = pivots.get(wiresPos2.get(i));
				drawWire(graphics, p1.getX(), p1.getY(), p2.getX(), p2.getY(), true, false);
			}
		}
		try {
			synchronized (branchLock) {
				int length = branchPointsX.size();
				Iterator<Integer> itX = branchPointsX.iterator();
				Iterator<Integer> itY = branchPointsY.iterator();
				for (int i = 0; i < length; i++) {
					Integer x = itX.next();
					Integer y = itY.next();
					drawBranch(graphics, x, y);
				}
			}
		} catch (NoSuchElementException nseex) {
			nseex.printStackTrace(System.out);
		}
		if (expanding) {
			if (extensionCount >= 1) {
				drawWire(graphics, ext1.getX(), ext1.getY(), ext2.getX(), ext2.getY(), false, true);
			}
			if (extensionCount >= 2) {
				drawWire(graphics, ext2.getX(), ext2.getY(), ext3.getX(), ext3.getY(), false, true);
			}
		}
	}

	private static Brush wireBrush = null;
	private static Brush selectBrush = null;
	private static Brush newBrush = null;
	private void drawWire(@NotNull RenderAPI graphics, int fromX, int fromY, int toX, int toY, boolean selected, boolean extension) {
		if (wireBrush == null) wireBrush = graphics.createSolidBrush(127, 127, 127);
		if (selectBrush == null) selectBrush = graphics.createSolidBrush(60, 60, 60);
		if (newBrush == null) newBrush = graphics.createSolidBrush(200, 200, 200);
		graphics.setBrush(selected ? selectBrush : (extension ? newBrush : wireBrush));
		graphics.drawLine(fromX, fromY, toX, toY, DELTA_WIDTH * 2);
	}
	private void drawBranch(@NotNull RenderAPI graphics, int x, int y) {
		graphics.setBrush(wireBrush);
		graphics.drawRectangle(x - DELTA_WIDTH * 2, y - DELTA_WIDTH * 2, x + DELTA_WIDTH * 2, y + DELTA_WIDTH * 2);
	}
}

@Accessors(chain = true)
@EqualsAndHashCode
class Pivot implements Comparable<Pivot> {
	private @Getter @Setter int x = Integer.MIN_VALUE;
	private @Getter @Setter int y = Integer.MIN_VALUE;
	public Pivot copy(Pivot src) {
		x = src.x; y = src.y; return this;
	}
	
	@Override public String toString() {
		return String.format("[%1$d %2$d]", x, y);
	}
	
	static void sort(@NotNull Pivot toSmall, @NotNull Pivot toBig) {
		if (toSmall.compareTo(toBig) > 0) {
			toSmall.y = toSmall.y ^ toBig.y;
			toBig.y = toSmall.y ^ toBig.y;
			toSmall.y = toSmall.y ^ toBig.y;
			toSmall.x = toSmall.x ^ toBig.x;
			toBig.x = toSmall.x ^ toBig.x;
			toSmall.x = toSmall.x ^ toBig.x;
		}
	}
	
	@Override
	public int compareTo(@NotNull Pivot o) {
		return (x * x + y * y) - (o.x * o.x + o.y * o.y);
	}
}

