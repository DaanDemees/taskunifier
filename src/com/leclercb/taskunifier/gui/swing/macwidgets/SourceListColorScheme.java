package com.leclercb.taskunifier.gui.swing.macwidgets;

import java.awt.Color;
import java.awt.Component;

import com.explodingpixels.macwidgets.SourceListStandardColorScheme;
import com.explodingpixels.painter.GradientWithBorderPainter;
import com.explodingpixels.painter.Painter;
import com.leclercb.taskunifier.gui.utils.review.NoReview;

@NoReview
public class SourceListColorScheme extends SourceListStandardColorScheme {
	
	private static final Painter<Component> ACTIVE_FOCUSED_SELECTION_PAINTER = createSourceListActiveFocusedSelectionPainter();
	private static Color ACTIVE_BACKGROUND_COLOR = new Color(0xd6dde5);
	private static Color INACTIVE_BACKGROUND_COLOR = new Color(0xd6dde5);
	
	@Override
	public Painter<Component> getActiveFocusedSelectedItemPainter() {
		return ACTIVE_FOCUSED_SELECTION_PAINTER;
	}
	
	@Override
	public Color getActiveBackgroundColor() {
		return ACTIVE_BACKGROUND_COLOR;
	}
	
	@Override
	public Color getInactiveBackgroundColor() {
		return INACTIVE_BACKGROUND_COLOR;
	}
	
	private static Painter<Component> createSourceListActiveFocusedSelectionPainter() {
		Color topLineColor = new Color(58, 93, 137);
		Color topColor = new Color(106, 144, 182);
		Color bottomColor = new Color(77, 111, 148);
		return new GradientWithBorderPainter(
				topLineColor,
				bottomColor,
				topColor,
				bottomColor);
	}
	
}
