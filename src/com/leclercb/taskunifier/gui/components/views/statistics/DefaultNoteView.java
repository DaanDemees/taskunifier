package com.leclercb.taskunifier.gui.components.views.statistics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jdesktop.swingx.JXSearchField;

import com.explodingpixels.macwidgets.SourceListStandardColorScheme;
import com.jgoodies.common.base.SystemUtils;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.commons.gui.swing.lookandfeel.LookAndFeelUtils;
import com.leclercb.taskunifier.api.models.ModelNote;
import com.leclercb.taskunifier.api.models.NoteFactory;
import com.leclercb.taskunifier.gui.commons.events.NoteSearcherSelectionChangeEvent;
import com.leclercb.taskunifier.gui.components.modelnote.ModelNotePanel;
import com.leclercb.taskunifier.gui.components.notes.NoteTableView;
import com.leclercb.taskunifier.gui.components.notes.table.NoteTable;
import com.leclercb.taskunifier.gui.components.notesearchertree.NoteSearcherPanel;
import com.leclercb.taskunifier.gui.components.notesearchertree.NoteSearcherView;
import com.leclercb.taskunifier.gui.components.views.ViewType;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.main.MainView;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;

public class DefaultNoteView extends JPanel implements NoteView, SavePropertiesListener {
	
	private MainView mainView;
	
	private JSplitPane horizontalSplitPane;
	private JSplitPane verticalSplitPane;
	
	private NoteSearcherPanel noteSearcherPanel;
	private NoteTable noteTable;
	private ModelNotePanel noteNote;
	
	private JXSearchField searchField;
	
	public DefaultNoteView(MainView mainView) {
		CheckUtils.isNotNull(mainView, "Main view cannot be null");
		this.mainView = mainView;
		
		this.initialize();
	}
	
	@Override
	public ViewType getViewType() {
		return ViewType.NOTES;
	}
	
	@Override
	public JPanel getViewContent() {
		return this;
	}
	
	@Override
	public NoteSearcherView getNoteSearcherView() {
		return this.noteSearcherPanel;
	}
	
	@Override
	public NoteTableView getNoteTableView() {
		return this.noteTable;
	}
	
	private void initialize() {
		Main.SETTINGS.addSavePropertiesListener(this);
		
		this.setLayout(new BorderLayout());
		
		if (SystemUtils.IS_OS_MAC && LookAndFeelUtils.isCurrentLafSystemLaf()) {
			this.horizontalSplitPane = ComponentFactory.createThinJSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		} else {
			this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			
			this.horizontalSplitPane = new JSplitPane(
					JSplitPane.HORIZONTAL_SPLIT);
		}
		
		this.horizontalSplitPane.setOneTouchExpandable(true);
		
		this.verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.verticalSplitPane.setOneTouchExpandable(true);
		this.verticalSplitPane.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel searcherPane = new JPanel();
		searcherPane.setLayout(new BorderLayout());
		
		JPanel middlePane = new JPanel();
		middlePane.setLayout(new BorderLayout());
		
		JPanel notePane = new JPanel();
		notePane.setLayout(new BorderLayout());
		
		this.horizontalSplitPane.setLeftComponent(searcherPane);
		this.horizontalSplitPane.setRightComponent(this.verticalSplitPane);
		
		this.verticalSplitPane.setTopComponent(middlePane);
		this.verticalSplitPane.setBottomComponent(notePane);
		
		this.add(this.horizontalSplitPane, BorderLayout.CENTER);
		
		this.loadSplitPaneSettings();
		
		this.initializeSearchField();
		this.initializeSearcherList(searcherPane);
		this.initializeNoteTable(middlePane);
		this.initializeModelNote(notePane);
		
		this.noteSearcherPanel.refreshNoteSearcher();
	}
	
	private void loadSplitPaneSettings() {
		int hSplit = Main.SETTINGS.getIntegerProperty("view.notes.window.horizontal_split");
		int vSplit = Main.SETTINGS.getIntegerProperty("view.notes.window.vertical_split");
		
		this.horizontalSplitPane.setDividerLocation(hSplit);
		this.verticalSplitPane.setDividerLocation(vSplit);
	}
	
	@Override
	public void saveProperties() {
		Main.SETTINGS.setIntegerProperty(
				"view.notes.window.horizontal_split",
				this.horizontalSplitPane.getDividerLocation());
		Main.SETTINGS.setIntegerProperty(
				"view.notes.window.vertical_split",
				this.verticalSplitPane.getDividerLocation());
	}
	
	private void initializeSearchField() {
		this.searchField = new JXSearchField(
				Translations.getString("general.search"));
		this.searchField.setColumns(15);
		
		this.mainView.addPropertyChangeListener(
				MainView.PROP_MAIN_SEARCH,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals(
								MainView.PROP_MAIN_SEARCH))
							if (evt.getNewValue() instanceof String)
								DefaultNoteView.this.searchField.setText((String) evt.getNewValue());
					}
					
				});
		
		this.searchField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultNoteView.this.mainView.setSearch(e.getActionCommand());
				DefaultNoteView.this.noteSearcherPanel.setTitleFilter(e.getActionCommand());
			}
			
		});
	}
	
	private void initializeSearcherList(JPanel searcherPane) {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(new SourceListStandardColorScheme().getActiveBackgroundColor());
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(new SourceListStandardColorScheme().getActiveBackgroundColor());
		
		panel.add(northPanel, BorderLayout.NORTH);
		
		if (!(SystemUtils.IS_OS_MAC && LookAndFeelUtils.isCurrentLafSystemLaf())) {
			northPanel.add(this.searchField, BorderLayout.NORTH);
		}
		
		searcherPane.add(panel, BorderLayout.CENTER);
		
		this.initializeNoteSearcherList(panel);
	}
	
	private void initializeNoteSearcherList(JPanel searcherPane) {
		this.noteSearcherPanel = new NoteSearcherPanel();
		
		this.noteSearcherPanel.addPropertyChangeListener(
				NoteSearcherPanel.PROP_TITLE_FILTER,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						String filter = (String) evt.getNewValue();
						if (!DefaultNoteView.this.searchField.getText().equals(
								filter))
							DefaultNoteView.this.searchField.setText(filter);
					}
					
				});
		
		searcherPane.add(this.noteSearcherPanel);
	}
	
	private void initializeNoteTable(JPanel middlePane) {
		this.noteTable = new NoteTable();
		
		JPanel notePanel = new JPanel(new BorderLayout());
		notePanel.add(
				ComponentFactory.createJScrollPane(this.noteTable, false),
				BorderLayout.CENTER);
		
		this.noteSearcherPanel.addNoteSearcherSelectionChangeListener(this.noteTable);
		this.noteSearcherPanel.addPropertyChangeListener(
				NoteSearcherPanel.PROP_TITLE_FILTER,
				new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						DefaultNoteView.this.noteTable.noteSearcherSelectionChange(new NoteSearcherSelectionChangeEvent(
								evt.getSource(),
								DefaultNoteView.this.noteSearcherPanel.getSelectedNoteSearcher()));
					}
					
				});
		
		middlePane.add(notePanel);
	}
	
	private void initializeModelNote(JPanel notePane) {
		this.noteNote = new ModelNotePanel();
		this.noteTable.addModelSelectionChangeListener(this.noteNote);
		
		NoteFactory.getInstance().addPropertyChangeListener(
				ModelNote.PROP_NOTE,
				this.noteNote);
		
		notePane.add(this.noteNote);
	}
	
}
