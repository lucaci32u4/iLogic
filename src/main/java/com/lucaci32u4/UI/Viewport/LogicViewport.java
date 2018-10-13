package com.lucaci32u4.UI.Viewport;

import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;
import com.lucaci32u4.UI.Viewport.Renderer.Brushes.Brush;
import com.lucaci32u4.UI.Viewport.Renderer.RenderAPI;
import com.lucaci32u4.UI.Viewport.Renderer.VisualArtifact;
import com.lucaci32u4.UI.Viewport.Renderer.RenderingSubsystem.Java2D.Java2DSubsystem;
import com.lucaci32u4.util.SimpleWorkerThread;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
	
	private ViewportData data;
	private Semaphore bufferLock;
	private SimpleWorkerThread bufferWorker;
	private RenderAPI pencil;
	private PickerAPI picker;
	private UserInputListener userInputListener;

	public void init(@NotNull JPanel displayPanel, @NotNull RenderAPI renderAPI, @NotNull PickerAPI pickerAPI, @NotNull UserInputListener inputListener) {
		pencil = renderAPI;
		picker = pickerAPI;
		userInputListener = inputListener;
		data = new ViewportData();
		data.bkgndSprite = null;
		data.sprites = new VisualArtifact[0];
		bufferWorker = new SimpleWorkerThread(this::run);
		bufferLock = new Semaphore(1);
		pencil = new Java2DSubsystem();
		pencil.initRenderer(displayPanel, this);
		pencil.getCanvas().addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {

			}
			@Override public void mousePressed(MouseEvent e) {
				userInputListener.mouseButtonEvent(e);
			}
			@Override public void mouseReleased(MouseEvent e) {
				userInputListener.mouseButtonEvent(e);
			}
			@Override public void mouseEntered(MouseEvent e) {
				userInputListener.isInsidePerimeter(true);
			}
			@Override public void mouseExited(MouseEvent e) {
				userInputListener.isInsidePerimeter(false);
			}
		});
		pencil.getCanvas().addMouseMotionListener(new MouseMotionListener() {
			@Override public void mouseDragged(MouseEvent e) {
				userInputListener.mouseMotionEvent(e, true);
			}
			@Override public void mouseMoved(MouseEvent e) {
				userInputListener.mouseMotionEvent(e, false);
			}
		});
		bufferWorker.start();
	}
	
	public void attach(VisualArtifact sprite) {
		data.pendingAttach.offer(sprite);
		bufferLock.release();
		requestUpdate();
		bufferLock.acquireUninterruptibly();
	}
	
	public void detach(VisualArtifact sprite) {
		bufferLock.acquireUninterruptibly();
		data.pendingDetach.offer(sprite);
		requestUpdate();
		bufferLock.release();
	}

	public void destroy() {
		pencil.destroyRenderer();
		picker.destroy();
		bufferWorker.exit(true);
	}
	
	public void requestUpdate() {
		boolean immediate = pencil.requestRenderFrame(data);
		if (immediate) {
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
		reshapeBuffers(data);
		bufferLock.release();
	}
}