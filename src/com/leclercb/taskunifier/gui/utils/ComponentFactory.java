package com.leclercb.taskunifier.gui.utils;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.jgoodies.common.base.SystemUtils;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelUtils;
import com.leclercb.taskunifier.gui.images.Images;

public final class ComponentFactory {
	
	private ComponentFactory() {

	}
	
	public static JPanel createSearchField(JTextField textField) {
		if (SystemUtils.IS_OS_MAC && LookAndFeelUtils.isCurrentLafSystemLaf()) {
			textField.putClientProperty("JTextField.variant", "search");
			
			JPanel panel = new JPanel(new BorderLayout());
			panel.setOpaque(false);
			panel.add(textField, BorderLayout.CENTER);
			
			return panel;
		} else {
			JPanel panel = new JPanel(new BorderLayout(5, 0));
			panel.setOpaque(false);
			panel.add(
					new JLabel(Images.getResourceImage("search.png", 16, 16)),
					BorderLayout.WEST);
			panel.add(textField, BorderLayout.CENTER);
			
			return panel;
		}
	}
	
	public static JScrollPane createJScrollPane(
			JComponent component,
			boolean border) {
		JScrollPane scrollPane = new JScrollPane(component);
		
		if (SystemUtils.IS_OS_MAC && LookAndFeelUtils.isCurrentLafSystemLaf())
			IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		
		if (border)
			scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		else
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		return scrollPane;
	}
	
	public static JSplitPane createThinJScrollPane(int orientation) {
		JSplitPane splitPane = new JSplitPane(orientation);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerSize(1);
		((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(
				BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0xa5a5a5)));
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		return splitPane;
	}
	
}
