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

package com.lucaci32u4.UI.Viewport.Renderer;

import com.lucaci32u4.UI.Viewport.Renderer.Brushes.OutlineBrush;
import com.lucaci32u4.UI.Viewport.Renderer.Brushes.SolidBrush;
import com.lucaci32u4.UI.Viewport.Renderer.Brushes.TextureBrush;

import java.awt.image.BufferedImage;

public interface ResourceAPI {
	SolidBrush createSolidBrush(int r, int g, int b);
	OutlineBrush createOutlineBrush(int r, int g, int b);
	TextureBrush createTextureBrush(BufferedImage image, int unitWidth, int unitHeight);
}
