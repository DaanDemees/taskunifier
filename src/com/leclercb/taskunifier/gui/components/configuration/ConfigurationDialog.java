/*
 * TaskUnifier
 * Copyright (c) 2011, Benjamin Leclerc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of TaskUnifier or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.leclercb.taskunifier.gui.components.configuration;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.error.ErrorInfo;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationGroup;
import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationPanel;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.MainFrame;
import com.leclercb.taskunifier.gui.swing.buttons.TUApplyButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUButtonsPanel;
import com.leclercb.taskunifier.gui.swing.buttons.TUCancelButton;
import com.leclercb.taskunifier.gui.swing.buttons.TUOkButton;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.ImageUtils;
import com.leclercb.taskunifier.gui.utils.SynchronizerUtils;

public class ConfigurationDialog extends JDialog implements ConfigurationGroup {
	
	private static ConfigurationDialog INSTANCE;
	
	public static ConfigurationDialog getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ConfigurationDialog();
		
		return INSTANCE;
	}
	
	public static enum ConfigurationTab {
		
		GENERAL,
		DATE,
		BACKUP,
		PROXY,
		SEARCHER,
		THEME,
		TOOLBAR,
		ADVANCED,
		SYNCHRONIZATION,
		PLUGIN;
		
	}
	
	private JTabbedPane tabbedPane;
	
	private ConfigurationPanel generalConfigurationPanel;
	private ConfigurationPanel dateConfigurationPanel;
	private ConfigurationPanel backupConfigurationPanel;
	private ConfigurationPanel proxyConfigurationPanel;
	private ConfigurationPanel searcherConfigurationPanel;
	private ConfigurationPanel themeConfigurationPanel;
	private ConfigurationPanel toolbarConfigurationPanel;
	private ConfigurationPanel advancedConfigurationPanel;
	private ConfigurationPanel synchronizationConfigurationPanel;
	private ConfigurationPanel pluginConfigurationPanel;
	
	private ConfigurationDialog() {
		super(MainFrame.getInstance().getFrame(), true);
		
		this.initialize();
	}
	
	public void setSelectedConfigurationTab(ConfigurationTab tab) {
		CheckUtils.isNotNull(tab);
		this.tabbedPane.setSelectedIndex(tab.ordinal());
	}
	
	private void initialize() {
		this.setTitle(Translations.getString("general.configuration"));
		this.setSize(900, 700);
		this.setResizable(true);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		if (this.getOwner() != null)
			this.setLocationRelativeTo(this.getOwner());
		
		JXHeader header = new JXHeader();
		header.setTitle(Translations.getString("header.title.configuration"));
		header.setDescription(Translations.getString("header.description.configuration"));
		header.setIcon(ImageUtils.getResourceImage("settings.png", 32, 32));
		
		this.tabbedPane = new JTabbedPane();
		
		this.add(header, BorderLayout.NORTH);
		this.add(this.tabbedPane, BorderLayout.CENTER);
		
		this.initializeButtonsPanel();
		
		this.initializeGeneralPanel();
		this.initializeDatePanel();
		this.initializeBackupPanel();
		this.initializeProxyPanel();
		this.initializeSearcherPanel();
		this.initializeThemePanel();
		this.initializeToolbarPanel();
		this.initializeAdvancedPanel();
		this.initializeSynchronizationPanel();
		this.initializePluginPanel();
		
		Main.getUserSettings().addPropertyChangeListener(
				"api.id",
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						ConfigurationDialog.this.refreshSynchronizationPanels();
					}
					
				});
	}
	
	private void initializeButtonsPanel() {
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("OK")) {
					ConfigurationDialog.this.saveAndApplyConfig();
					ConfigurationDialog.this.setVisible(false);
				}
				
				if (event.getActionCommand().equals("CANCEL")) {
					ConfigurationDialog.this.cancelConfig();
					ConfigurationDialog.this.setVisible(false);
				}
				
				if (event.getActionCommand().equals("APPLY")) {
					ConfigurationDialog.this.saveAndApplyConfig();
				}
			}
			
		};
		
		JButton okButton = new TUOkButton(listener);
		JButton cancelButton = new TUCancelButton(listener);
		JButton applyButton = new TUApplyButton(listener);
		
		JPanel panel = new TUButtonsPanel(okButton, cancelButton, applyButton);
		
		this.add(panel, BorderLayout.SOUTH);
		this.getRootPane().setDefaultButton(okButton);
	}
	
	private void initializeGeneralPanel() {
		this.generalConfigurationPanel = new GeneralConfigurationPanel(
				this,
				false,
				false);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.general"),
				ComponentFactory.createJScrollPane(
						this.generalConfigurationPanel,
						false));
	}
	
	private void initializeDatePanel() {
		this.dateConfigurationPanel = new DateConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.date"),
				ComponentFactory.createJScrollPane(
						this.dateConfigurationPanel,
						false));
	}
	
	private void initializeBackupPanel() {
		this.backupConfigurationPanel = new BackupConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.backup"),
				ComponentFactory.createJScrollPane(
						this.backupConfigurationPanel,
						false));
	}
	
	private void initializeProxyPanel() {
		this.proxyConfigurationPanel = new ProxyConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.proxy"),
				ComponentFactory.createJScrollPane(
						this.proxyConfigurationPanel,
						false));
	}
	
	private void initializeSearcherPanel() {
		this.searcherConfigurationPanel = new SearcherConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.searcher"),
				ComponentFactory.createJScrollPane(
						this.searcherConfigurationPanel,
						false));
	}
	
	private void initializeThemePanel() {
		this.themeConfigurationPanel = new ThemeConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.theme"),
				this.themeConfigurationPanel);
	}
	
	private void initializeToolbarPanel() {
		this.toolbarConfigurationPanel = new ToolBarConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.toolbar"),
				ComponentFactory.createJScrollPane(
						this.toolbarConfigurationPanel,
						false));
	}
	
	private void initializeAdvancedPanel() {
		this.advancedConfigurationPanel = new AdvancedConfigurationPanel(this);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.advanced"),
				ComponentFactory.createJScrollPane(
						this.advancedConfigurationPanel,
						false));
	}
	
	private void initializeSynchronizationPanel() {
		this.synchronizationConfigurationPanel = new SynchronizationConfigurationPanel(
				this,
				false);
		this.tabbedPane.addTab(
				Translations.getString("configuration.tab.synchronization"),
				ComponentFactory.createJScrollPane(
						this.synchronizationConfigurationPanel,
						false));
	}
	
	private void initializePluginPanel() {
		this.pluginConfigurationPanel = new PluginConfigurationPanel(
				this,
				false,
				SynchronizerUtils.getPlugin());
		this.tabbedPane.addTab(
				SynchronizerUtils.getPlugin().getName(),
				ComponentFactory.createJScrollPane(
						this.pluginConfigurationPanel,
						false));
	}
	
	@Override
	public void saveAndApplyConfig() {
		try {
			this.pluginConfigurationPanel.saveAndApplyConfig();
			
			this.generalConfigurationPanel.saveAndApplyConfig();
			this.dateConfigurationPanel.saveAndApplyConfig();
			this.backupConfigurationPanel.saveAndApplyConfig();
			this.proxyConfigurationPanel.saveAndApplyConfig();
			this.searcherConfigurationPanel.saveAndApplyConfig();
			this.themeConfigurationPanel.saveAndApplyConfig();
			this.toolbarConfigurationPanel.saveAndApplyConfig();
			this.advancedConfigurationPanel.saveAndApplyConfig();
			this.synchronizationConfigurationPanel.saveAndApplyConfig();
			
			Main.saveSettings();
			Main.saveUserSettings();
			
			this.refreshSynchronizationPanels();
		} catch (Exception e) {
			ErrorInfo info = new ErrorInfo(
					Translations.getString("general.error"),
					Translations.getString("error.save_settings"),
					null,
					null,
					e,
					null,
					null);
			
			JXErrorPane.showDialog(MainFrame.getInstance().getFrame(), info);
			
			return;
		}
	}
	
	@Override
	public void cancelConfig() {
		try {
			this.generalConfigurationPanel.cancelConfig();
			this.dateConfigurationPanel.cancelConfig();
			
			this.synchronizationConfigurationPanel.cancelConfig();
			this.pluginConfigurationPanel.cancelConfig();
			
			this.backupConfigurationPanel.cancelConfig();
			this.proxyConfigurationPanel.cancelConfig();
			this.searcherConfigurationPanel.cancelConfig();
			this.themeConfigurationPanel.cancelConfig();
			this.toolbarConfigurationPanel.cancelConfig();
			this.advancedConfigurationPanel.cancelConfig();
		} catch (Exception e) {
			ErrorInfo info = new ErrorInfo(
					Translations.getString("general.error"),
					Translations.getString("error.save_settings"),
					null,
					null,
					e,
					null,
					null);
			
			JXErrorPane.showDialog(MainFrame.getInstance().getFrame(), info);
			
			return;
		}
	}
	
	private void refreshSynchronizationPanels() {
		int selectedTab = this.tabbedPane.getSelectedIndex();
		
		this.tabbedPane.removeTabAt(this.tabbedPane.getTabCount() - 1);
		
		this.initializePluginPanel();
		
		try {
			this.tabbedPane.setSelectedIndex(selectedTab);
		} catch (IndexOutOfBoundsException e) {
			
		}
	}
	
}
