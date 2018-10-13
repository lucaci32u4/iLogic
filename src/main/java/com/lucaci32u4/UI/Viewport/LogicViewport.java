package com.lucaci32u4.UI.Viewport;

import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;
import com.lucaci32u4.UI.Viewport.Renderer.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.Renderer.VisualArtifact;
import com.lucaci32u4.UI.Viewport.Renderer.RenderingSubsystem.Java2D.Java2DSubsystem;
import com.lucaci32u4.util.SimpleWorkerThread;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Semaphore;

public class LogicViewport {

	public static class ViewportData {
		public final ArrayDeque<VisualArtifact> pendingAttach = new ArrayDeque<>(100);
		public final ArrayDeque<VisualArtifact> pendingDetach = new ArrayDeque<>(100);
		public VisualArtifact[] sprites;
		public VisualArtifact bkgndSprite;
		public float pixelsPerUnit;
		public int offX, offY;
	}
	
	private ViewportData pendingData;
	private ViewportData reserveData;
	private Semaphore bufferLock;
	private SimpleWorkerThread bufferWorker;
	private RenderAPI pencil;
	private PickerAPI picker;

	public void init(@NotNull JPanel displayPanel, @NotNull RenderAPI renderAPI, @NotNull PickerAPI pickerAPI) {
		pencil = renderAPI;
		picker = pickerAPI;
		pendingData = new ViewportData();
		reserveData = new ViewportData();
		pendingData.bkgndSprite = null;
		reserveData.bkgndSprite = null;
		pendingData.sprites = new VisualArtifact[0];
		reserveData.sprites = new VisualArtifact[0];
		bufferWorker = new SimpleWorkerThread(this::run);
		bufferLock = new Semaphore(1);
		pencil = new Java2DSubsystem();
		pencil.initRenderer(displayPanel, this);
		bufferWorker.start();
	}
	
	public void attach(VisualArtifact sprite) {
		pendingData.pendingAttach.offer(sprite);
		bufferLock.release();
		requestUpdate();
		bufferLock.acquireUninterruptibly();
	}
	
	public void detach(VisualArtifact sprite) {
		bufferLock.acquireUninterruptibly();
		pendingData.pendingDetach.offer(sprite);
		requestUpdate();
		bufferLock.release();
	}

	public void destroy() {
		pencil.destroyRenderer();
		bufferWorker.exit(true);
	}
	
	public void requestUpdate() {
		boolean immediate = pencil.requestRenderFrame(pendingData);
		if (immediate) {
			ViewportData aux = reserveData;
			reserveData = pendingData;
			pendingData = aux;
			bufferWorker.submit();
		}
	}
	
	private void reshapeBuffers(@NotNull LogicViewport.ViewportData data) {
		if (data.pendingAttach.size() + data.pendingDetach.size() != 0) {
			Collection<VisualArtifact> com = CollectionUtils.retainAll(data.pendingAttach, data.pendingDetach);
			data.pendingAttach.removeAll(com);
			data.pendingDetach.removeAll(com);
			if (data.pendingAttach.size() + data.pendingDetach.size() != 0) {
				com = Arrays.asList(data.sprites);
				com.removeAll(data.pendingDetach);
				com.addAll(data.pendingAttach);
				data.sprites = (VisualArtifact[]) com.toArray();
			}
		}
	}

	private void run() {
		bufferLock.acquireUninterruptibly();
		reshapeBuffers(pendingData);
		bufferLock.release();
	}

	public interface RenderAPI extends DrawAPI, ResourceAPI {
		void initRenderer(JPanel panel, LogicViewport viewport);
		void destroyRenderer();
		boolean requestRenderFrame(ViewportData drawData);
		Canvas getCanvas();
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
