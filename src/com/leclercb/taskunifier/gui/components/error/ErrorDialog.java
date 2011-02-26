package com.leclercb.taskunifier.gui.components.error;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.leclercb.taskunifier.gui.MainFrame;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class ErrorDialog extends JDialog {
	
	private boolean reportable;
	private String message;
	private Throwable throwable;
	
	public ErrorDialog(
			Frame frame,
			String message,
			Throwable throwable,
			boolean reportable) {
		super(frame, Translations.getString("general.error"), true);
		
		if (reportable && throwable == null)
			throw new IllegalArgumentException(
					"Throwable cannot be null if reportable is true");
		
		if (message == null && throwable == null)
			throw new IllegalArgumentException(
					"Message and throwable cannot be both null");
		
		if (message == null)
			message = throwable.getMessage();
		
		this.reportable = reportable;
		this.message = message;
		this.throwable = throwable;
		
		this.initialize();
	}
	
	private void initialize() {
		this.setSize(500, (this.reportable ? 400 : 180));
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		
		if (this.getOwner() != null)
			this.setLocationRelativeTo(this.getOwner());
		
		JPanel panel = null;
		
		panel = new JPanel(new BorderLayout(20, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		this.add(panel, BorderLayout.NORTH);
		
		JLabel icon = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
		panel.add(icon, BorderLayout.WEST);
		
		JTextArea error = new JTextArea(3, 1);
		error.setText(this.message);
		error.setWrapStyleWord(true);
		error.setLineWrap(true);
		error.setEditable(false);
		error.setOpaque(false);
		panel.add(error, BorderLayout.CENTER);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
		
		if (this.reportable) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			this.throwable.printStackTrace(printWriter);
			printWriter.close();
			
			try {
				stringWriter.close();
			} catch (IOException e) {}
			
			JTextArea stackTrace = new JTextArea(stringWriter.toString());
			stackTrace.setEditable(false);
			panel.add(
					ComponentFactory.createJScrollPane(stackTrace, true),
					BorderLayout.CENTER);
		}
		
		this.add(panel, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		this.add(buttonsPanel, BorderLayout.SOUTH);
		
		this.initializeButtonsPanel(buttonsPanel);
	}
	
	private void initializeButtonsPanel(JPanel buttonsPanel) {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand() == "CREATE_REPORT") {
					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int result = fc.showSaveDialog(ErrorDialog.this);
					
					if (result == JFileChooser.APPROVE_OPTION) {
						try {
							File file = fc.getSelectedFile();
							file.createNewFile();
							PrintStream ps = new PrintStream(file);
							
							if (ErrorDialog.this.message != null)
								ps.println(ErrorDialog.this.message);
							
							ErrorDialog.this.throwable.printStackTrace(ps);
							ps.close();
						} catch (Exception exc) {
							JOptionPane.showMessageDialog(
									MainFrame.getInstance().getFrame(),
									exc.getMessage(),
									Translations.getString("general.error"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				
				if (event.getActionCommand() == "OK") {
					ErrorDialog.this.dispose();
				}
			}
			
		};
		
		JButton reportButton = new JButton(
				Translations.getString("general.create_report"));
		reportButton.setActionCommand("CREATE_REPORT");
		reportButton.addActionListener(listener);
		reportButton.setVisible(this.reportable);
		buttonsPanel.add(reportButton);
		
		JButton okButton = new JButton(Translations.getString("general.ok"));
		okButton.setActionCommand("OK");
		okButton.addActionListener(listener);
		buttonsPanel.add(okButton);
		
		this.getRootPane().setDefaultButton(okButton);
	}
	
}
