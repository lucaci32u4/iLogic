package com.lucaci32u4.UI.Viewport;

//import com.lucaci32u4.UI.Viewport.RenderingSubsystem.GL2Subsystem;
import com.lucaci32u4.UI.Viewport.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.RenderingSubsystem.Java2DSubsystem;
import org.apache.commons.collections4.CollectionUtils;
import com.lucaci32u4.util.JSignal;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Semaphore;

public class LogicViewport implements Runnable {
	public static final int WORKER_EXEC = 1;
	public static final int WORKER_EXIT = 2;

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
	private Semaphore bufferLock;
	private Thread bufferWorker;
	private JSignal workerCmdSignal;
	public int workerCommand;
	
	public void init(JPanel displayPanel, ViewportArtifact backgroundSprite) {
		circuitPanel = displayPanel;
		pendingData = new DrawData();
		reserveData = new DrawData();
		pendingData.bkgndSprite = backgroundSprite;
		reserveData.bkgndSprite = backgroundSprite;
		pendingData.sprites = new ViewportArtifact[0];
		reserveData.sprites = new ViewportArtifact[0];
		bufferLock = new Semaphore(1);
		workerCmdSignal = new JSignal(false);
		pencil = new Java2DSubsystem();
		pencil.initRenderer(circuitPanel, this);
		bufferWorker = new Thread(this);
		bufferWorker.start();
	}
	
	public void attach(ViewportArtifact sprite) {
		pendingData.pendingAttach.offer(sprite);
		bufferLock.release();
		requestUpdate();
		bufferLock.acquireUninterruptibly();
	}
	
	public void detach(ViewportArtifact sprite) {
		bufferLock.acquireUninterruptibly();
		pendingData.pendingDetach.offer(sprite);
		requestUpdate();
		bufferLock.release();
	}
	
	public void requestUpdate() {
		boolean immediate = pencil.requestRenderFrame(pendingData);
		if (immediate) {
			// Swapping buffers
			DrawData aux = reserveData;
			reserveData = pendingData;
			pendingData = aux;
			workerCommand = WORKER_EXEC;
			workerCmdSignal.set(true);
		}
	}
	
	private void reshapeBuffers(@NotNull DrawData data) {
		bufferLock.acquireUninterruptibly();
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
		bufferLock.release();
	}

	@Override public void run() {
		boolean running = true;
		while (running) {
			workerCmdSignal.waitFor(true);
			workerCmdSignal.set(false);
			switch(workerCommand) {
				case WORKER_EXEC:
					bufferLock.acquireUninterruptibly();
					reshapeBuffers(pendingData);
					bufferLock.release();
					break;
				case WORKER_EXIT:
					running = false;
					break;
			}
		}
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
