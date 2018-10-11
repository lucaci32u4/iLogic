package com.lucaci32u4.UI.Viewport;

//import com.lucaci32u4.UI.Viewport.RenderingSubsystem.GL2Subsystem;
import com.lucaci32u4.UI.Viewport.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.RenderingSubsystem.Java2DSubsystem;
import com.lucaci32u4.util.Helper;
import org.apache.commons.collections4.CollectionUtils;
import com.lucaci32u4.util.JSignal;
import org.apache.commons.collections4.Equator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;

public class LogicViewport {
	public static class DrawData {
		public final ArrayDeque<ViewportArtifact> pendingAttach = new ArrayDeque<>(100);
		public final ArrayDeque<ViewportArtifact> pendingDetach = new ArrayDeque<>(100);
		public ViewportArtifact[] sprites;
		public ViewportArtifact bkgndSprite;
	}
	
	private DrawData pendingData;
	private DrawData reserveData;
	private JPanel circuitPanel;
	private RenderAPI pencil;
	private JSignal buffersUnlocked;
	
	public void init(JPanel displayPanel, ViewportArtifact backgroundSprite) {
		circuitPanel = displayPanel;
		pendingData = new DrawData();
		reserveData = new DrawData();
		pendingData.bkgndSprite = backgroundSprite;
		reserveData.bkgndSprite = backgroundSprite;
		pendingData.sprites = new ViewportArtifact[0];
		reserveData.sprites = new ViewportArtifact[0];
		buffersUnlocked = new JSignal(true);
		pencil = new Java2DSubsystem();
		pencil.initRenderer(circuitPanel, this);
	}
	
	public void attach(ViewportArtifact sprite) {
		pendingData.pendingAttach.offer(sprite);
		requestUpdate();
	}
	
	public void detach(ViewportArtifact sprite) {
		pendingData.pendingDetach.offer(sprite);
		requestUpdate();
	}
	
	public void requestUpdate() {
		
		boolean immediate = pencil.requestRenderFrame(pendingData);
		if (immediate) {
			// Swapping buffers
			DrawData aux = reserveData;
			reserveData = pendingData;
			pendingData = aux;
			reshapeBuffers(pendingData);
		}
	}
	
	private void reshapeBuffers(@NotNull DrawData data) {
		buffersUnlocked.set(false);
		if (data.pendingAttach.size() + data.pendingDetach.size() != 0) {
			Collection<ViewportArtifact> com = CollectionUtils.retainAll(data.pendingAttach, data.pendingDetach);
			data.pendingAttach.removeAll(com);
			data.pendingDetach.removeAll(com);
			if (data.pendingAttach.size() + data.pendingDetach.size() != 0) {
				com = Arrays.asList(data.sprites);
				com.removeAll(data.pendingDetach);
				com.addAll(data.pendingAttach);
				data.sprites = (ViewportArtifact[]) com.toArray();
			}
		}
		buffersUnlocked.set(true);
	}

	public interface RenderAPI extends ControlAPI, DrawAPI, ResourceAPI { }
	public interface ControlAPI {
		boolean initRenderer(JPanel panel, LogicViewport viewport);
		void destroyRenderer();
		boolean requestRenderFrame(DrawData drawData);
		void setSpaceScale(float pixelsPerUnit);
		void setSpaceOffset(int offsetX, int offsetY);
	}
	public interface DrawAPI {
		float unitsToPixels(int units);
		float pixelsToUnits(int pixels);
		void setCanvasOffsetUnits(int offsetX, int offsetY);
		void setBrush(Brush brush);
		void drawLine(int fromX, int fromY, int toX, int toY, float thicknessPixels);
		void drawTriangle(int aX, int aY, int bX, int bY, int cX, int cY);
		void drawRectangle(int left, int top, int right, int bottom);
	}
	
	public interface ResourceAPI {
	
	}
}
