/*
 * iSignal - Digital circuit simulator
 * Copyright (C) 2018-present Iercosan-Lucaci Alexandru
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *    ||=============================================||
 *    ||     _  _____  _          _            _     ||
 *    ||    (_)/  ___|(_)       =)_)-         | |    ||
 *    ||     _ \ `--.  _   __ _  _ __    __ _ | |    ||
 *    ||    | | `--. \| | / _` || '_ \  / _` || |    ||
 *    ||    | |/\__/ /| || (_| || | | || (_| || |    ||
 *    ||    |_|\____/ |_| \__, ||_| |_| \__,_||_|    ||
 *    ||                   __/ |                     ||
 *    ||                  |___/  Digital Simulator   ||
 *    ||=============================================||
 */

package com.lucaci32u4.UI.Viewport;

import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;
import com.lucaci32u4.UI.Viewport.Picker.HitboxManager;
import com.lucaci32u4.UI.Viewport.Renderer.RenderAPI;
import com.lucaci32u4.UI.Viewport.Renderer.RenderManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class LogicViewport {

	private RenderAPI pencil;
	private PickerAPI picker;
	private UserInputListener userInputListener;

	public void init(@NotNull JPanel displayPanel, @NotNull RenderAPI renderAPI, @NotNull PickerAPI pickerAPI, @NotNull UserInputListener inputListener) {
		pencil = renderAPI;
		picker = pickerAPI;
		userInputListener = inputListener;
		pencil.init(displayPanel, this);
		picker.init();
		SwingUtilities.invokeLater(() -> {
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
					userInputListener.notifyPerimeter(true);
				}
				@Override public void mouseExited(MouseEvent e) {
					userInputListener.notifyPerimeter(false);
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
		});
	}

	public PickerAPI getPickerManager() {
		return picker;
	}

	public RenderManager getRenderManager() {
		return pencil;
	}

	public void destroy() {
		pencil.destroy();
		picker.destroy();
	}
	
	public Object switchChannel(Object newChannelContent) {
		return pencil.switchChannel(newChannelContent);
	}
	
	public void requestNewFrame() {
		pencil.requestRenderFrame();
	}
}