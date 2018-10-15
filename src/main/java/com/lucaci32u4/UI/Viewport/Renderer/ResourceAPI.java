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
