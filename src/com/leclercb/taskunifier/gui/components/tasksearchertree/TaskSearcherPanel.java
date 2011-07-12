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
package com.leclercb.taskunifier.gui.components.tasksearchertree;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SortOrder;
import javax.swing.tree.TreePath;

import com.explodingpixels.macwidgets.SourceListStandardColorScheme;
import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.properties.events.SavePropertiesListener;
import com.leclercb.commons.api.utils.EqualsUtils;
import com.leclercb.taskunifier.api.models.ContextFactory;
import com.leclercb.taskunifier.api.models.FolderFactory;
import com.leclercb.taskunifier.api.models.GoalFactory;
import com.leclercb.taskunifier.api.models.LocationFactory;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.settings.ModelIdSettingsCoder;
import com.leclercb.taskunifier.gui.actions.ActionConfiguration;
import com.leclercb.taskunifier.gui.actions.ActionEditSearcher;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcher;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcherFactory;
import com.leclercb.taskunifier.gui.api.searchers.TaskSearcherType;
import com.leclercb.taskunifier.gui.api.searchers.filters.FilterLink;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilter;
import com.leclercb.taskunifier.gui.api.searchers.filters.TaskFilterElement;
import com.leclercb.taskunifier.gui.api.searchers.filters.conditions.StringCondition;
import com.leclercb.taskunifier.gui.api.searchers.sorters.TaskSorter;
import com.leclercb.taskunifier.gui.api.searchers.sorters.TaskSorterElement;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionChangeEvent;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionChangeSupport;
import com.leclercb.taskunifier.gui.commons.events.TaskSearcherSelectionListener;
import com.leclercb.taskunifier.gui.components.configuration.ConfigurationDialog.ConfigurationPanel;
import com.leclercb.taskunifier.gui.components.models.ModelConfigurationDialog;
import com.leclercb.taskunifier.gui.components.tasks.TaskColumn;
import com.leclercb.taskunifier.gui.components.tasksearchertree.nodes.ModelItem;
import com.leclercb.taskunifier.gui.components.tasksearchertree.nodes.TagItem;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.translations.Translations;
import com.leclercb.taskunifier.gui.utils.ComponentFactory;
import com.leclercb.taskunifier.gui.utils.Images;
import com.leclercb.taskunifier.gui.utils.review.Reviewed;

@Reviewed
public class TaskSearcherPanel extends JPanel implements SavePropertiesListener, TaskSearcherView, PropertyChangeSupported, TaskSearcherSelectionListener {
	
	public static final String PROP_TITLE_FILTER = "titleFilter";
	
	private TaskSearcherSelectionChangeSupport taskSearcherSelectionChangeSupport;
	
	private TaskSearcherTree searcherView;
	
	private String titleFilter;
	
	private Action addAction;
	private Action removeAction;
	private Action editAction;
	
	public TaskSearcherPanel() {
		this.taskSearcherSelectionChangeSupport = new TaskSearcherSelectionChangeSupport(
				this);
		
		Main.SETTINGS.addSavePropertiesListener(this);
		
		this.initialize();
	}
	
	@Override
	public void setTitleFilter(String titleFilter) {
		if (EqualsUtils.equals(this.titleFilter, titleFilter))
			return;
		
		String oldTitleFilter = this.titleFilter;
		this.titleFilter = titleFilter;
		
		this.firePropertyChange(PROP_TITLE_FILTER, oldTitleFilter, titleFilter);
	}
	
	@Override
	public void selectDefaultTaskSearcher() {
		this.searcherView.selectDefaultTaskSearcher();
	}
	
	@Override
	public boolean selectTaskSearcher(TaskSearcher searcher) {
		return this.searcherView.selectTaskSearcher(searcher);
	}
	
	@Override
	public boolean selectModel(Model model) {
		return this.searcherView.selectModel(model);
	}
	
	@Override
	public boolean selectTag(String tag) {
		return this.searcherView.selectTag(tag);
	}
	
	@Override
	public TaskSearcher getSelectedTaskSearcher() {
		TaskSearcher searcher = this.searcherView.getSelectedTaskSearcher();
		
		if (searcher == null)
			return null;
		
		searcher = searcher.clone();
		
		if (this.titleFilter != null && this.titleFilter.length() != 0) {
			TaskFilter originalFilter = searcher.getFilter();
			
			TaskFilter newFilter = new TaskFilter();
			newFilter.setLink(FilterLink.AND);
			
			TaskFilter searchFilter = new TaskFilter();
			searchFilter.setLink(FilterLink.OR);
			searchFilter.addElement(new TaskFilterElement(
					TaskColumn.TITLE,
					StringCondition.CONTAINS,
					this.titleFilter));
			searchFilter.addElement(new TaskFilterElement(
					TaskColumn.TAGS,
					StringCondition.CONTAINS,
					this.titleFilter));
			searchFilter.addElement(new TaskFilterElement(
					TaskColumn.NOTE,
					StringCondition.CONTAINS,
					this.titleFilter));
			
			newFilter.addFilter(searchFilter);
			newFilter.addFilter(originalFilter);
			
			searcher.setFilter(newFilter);
		}
		
		if (Main.SETTINGS.getBooleanProperty("searcher.show_completed_tasks_at_the_end")) {
			searcher.getSorter().addElement(
					new TaskSorterElement(
							0,
							TaskColumn.COMPLETED,
							SortOrder.ASCENDING));
		}
		
		return searcher;
	}
	
	@Override
	public void refreshTaskSearcher() {
		this.taskSearcherSelectionChangeSupport.fireTaskSearcherSelectionChange(this.getSelectedTaskSearcher());
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		
		this.searcherView = new TaskSearcherTree();
		
		this.add(
				ComponentFactory.createJScrollPane(this.searcherView, false),
				BorderLayout.CENTER);
		
		this.searcherView.addTaskSearcherSelectionChangeListener(this);
		
		this.searcherView.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					TreePath path = TaskSearcherPanel.this.searcherView.getPathForLocation(
							e.getX(),
							e.getY());
					
					Object node = path.getLastPathComponent();
					
					if (node instanceof ModelItem)
						TaskSearcherPanel.this.openManageModels((ModelItem) node);
					
					if (node instanceof TagItem)
						TaskSearcherPanel.this.openManageTags((TagItem) node);
					
					TaskSearcherPanel.this.openTaskSearcherEdit();
				}
			}
			
		});
		
		this.initializeButtons();
		
		this.initializeSelectedSearcher();
	}
	
	private void initializeButtons() {
		this.addAction = new AbstractAction(null, Images.getResourceImage(
				"add.png",
				16,
				16)) {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				TaskSorter sorter = new TaskSorter();
				sorter.addElement(new TaskSorterElement(
						1,
						TaskColumn.COMPLETED,
						SortOrder.ASCENDING));
				sorter.addElement(new TaskSorterElement(
						2,
						TaskColumn.DUE_DATE,
						SortOrder.ASCENDING));
				sorter.addElement(new TaskSorterElement(
						3,
						TaskColumn.PRIORITY,
						SortOrder.DESCENDING));
				sorter.addElement(new TaskSorterElement(
						4,
						TaskColumn.TITLE,
						SortOrder.ASCENDING));
				
				TaskSearcherFactory.getInstance().create(
						TaskSearcherType.PERSONAL,
						Integer.MAX_VALUE,
						Translations.getString("searcher.default.title"),
						new TaskFilter(),
						sorter);
				
				TaskSearcherPanel.this.openTaskSearcherEdit();
			}
			
		};
		
		this.removeAction = new AbstractAction(null, Images.getResourceImage(
				"remove.png",
				16,
				16)) {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				TaskSearcher searcher = TaskSearcherPanel.this.searcherView.getSelectedTaskSearcher();
				TaskSearcherFactory.getInstance().unregister(searcher);
			}
			
		};
		
		this.editAction = new AbstractAction(null, Images.getResourceImage(
				"edit.png",
				16,
				16)) {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				TaskSearcherPanel.this.openTaskSearcherEdit();
			}
			
		};
		
		JPanel panel = ComponentFactory.createButtonsPanel(new JButton(
				this.addAction), new JButton(this.removeAction), new JButton(
				this.editAction));
		panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		panel.setBackground(new SourceListStandardColorScheme().getActiveBackgroundColor());
		
		this.add(panel, BorderLayout.SOUTH);
		
		this.addAction.setEnabled(true);
		this.removeAction.setEnabled(false);
		this.editAction.setEnabled(false);
	}
	
	private void openManageModels(ModelItem item) {
		if (item.getModel() == null) {
			ActionConfiguration.configuration(ConfigurationPanel.SEARCHER);
			return;
		}
		
		ModelConfigurationDialog dialog = ModelConfigurationDialog.getInstance();
		dialog.setSelectedModel(item.getModelType(), item.getModel());
		dialog.setVisible(true);
	}
	
	private void openManageTags(TagItem item) {
		ModelConfigurationDialog dialog = ModelConfigurationDialog.getInstance();
		dialog.setSelectedTag(item.getTag());
		dialog.setVisible(true);
	}
	
	private void openTaskSearcherEdit() {
		TaskSearcher searcher = TaskSearcherPanel.this.searcherView.getSelectedTaskSearcher();
		
		if (searcher != null && searcher.getType().isEditable()) {
			ActionEditSearcher.editSearcher(searcher);
			this.searcherView.updateBadges();
		}
	}
	
	@Override
	public void addTaskSearcherSelectionChangeListener(
			TaskSearcherSelectionListener listener) {
		this.taskSearcherSelectionChangeSupport.addTaskSearcherSelectionChangeListener(listener);
	}
	
	@Override
	public void removeTaskSearcherSelectionChangeListener(
			TaskSearcherSelectionListener listener) {
		this.taskSearcherSelectionChangeSupport.removeTaskSearcherSelectionChangeListener(listener);
	}
	
	@Override
	public void taskSearcherSelectionChange(
			TaskSearcherSelectionChangeEvent event) {
		TaskSearcher searcher = event.getSelectedTaskSearcher();
		
		if (searcher == null)
			return;
		
		boolean foundInFactory = TaskSearcherFactory.getInstance().contains(
				searcher.getId());
		
		this.setTitleFilter(null);
		this.removeAction.setEnabled(foundInFactory
				&& searcher.getType().isDeletable());
		this.editAction.setEnabled(foundInFactory
				&& searcher.getType().isEditable());
		
		this.taskSearcherSelectionChangeSupport.fireTaskSearcherSelectionChange(this.getSelectedTaskSearcher());
	}
	
	private void initializeSelectedSearcher() {
		try {
			String value = Main.SETTINGS.getStringProperty("searcher.task.selected.value");
			TaskSearcherType type = Main.SETTINGS.getEnumProperty(
					"searcher.task.selected.type",
					TaskSearcherType.class);
			
			if (value != null && type != null) {
				if (type == TaskSearcherType.GENERAL
						|| type == TaskSearcherType.PERSONAL) {
					TaskSearcher searcher = null;
					List<TaskSearcher> searchers = TaskSearcherFactory.getInstance().getList();
					for (TaskSearcher s : searchers) {
						if (s.getTitle().equals(value)
								&& s.getType().equals(type)) {
							searcher = s;
							break;
						}
					}
					
					if (searcher != null) {
						if (this.searcherView.selectTaskSearcher(searcher))
							return;
					}
				}
				
				if (type == TaskSearcherType.TAG) {
					if (this.searcherView.selectTag(value))
						return;
				}
				
				if (type == TaskSearcherType.CONTEXT
						|| type == TaskSearcherType.FOLDER
						|| type == TaskSearcherType.GOAL
						|| type == TaskSearcherType.LOCATION) {
					ModelId id = new ModelIdSettingsCoder().decode(value);
					Model model = null;
					
					if (type == TaskSearcherType.CONTEXT) {
						model = ContextFactory.getInstance().get(id);
					} else if (type == TaskSearcherType.FOLDER) {
						model = FolderFactory.getInstance().get(id);
					} else if (type == TaskSearcherType.GOAL) {
						model = GoalFactory.getInstance().get(id);
					} else if (type == TaskSearcherType.LOCATION) {
						model = LocationFactory.getInstance().get(id);
					}
					
					if (model != null) {
						if (this.searcherView.selectModel(model))
							return;
					}
				}
				
				this.selectDefaultTaskSearcher();
				return;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		this.selectDefaultTaskSearcher();
	}
	
	@Override
	public void saveProperties() {
		try {
			TaskSearcher searcher = this.searcherView.getSelectedTaskSearcher();
			
			if (searcher == null)
				return;
			
			Main.SETTINGS.setEnumProperty(
					"searcher.task.selected.type",
					TaskSearcherType.class,
					searcher.getType());
			
			if (searcher.getType() == TaskSearcherType.GENERAL
					|| searcher.getType() == TaskSearcherType.PERSONAL) {
				Main.SETTINGS.setStringProperty(
						"searcher.task.selected.value",
						searcher.getTitle());
				return;
			}
			
			if (searcher.getType() == TaskSearcherType.TAG) {
				if (this.searcherView.getSelectedTag() != null) {
					Main.SETTINGS.setStringProperty(
							"searcher.task.selected.value",
							this.searcherView.getSelectedTag());
				}
				
				return;
			}
			
			if (searcher.getType() == TaskSearcherType.CONTEXT
					|| searcher.getType() == TaskSearcherType.FOLDER
					|| searcher.getType() == TaskSearcherType.GOAL
					|| searcher.getType() == TaskSearcherType.LOCATION) {
				if (this.searcherView.getSelectedModel() != null) {
					ModelId id = this.searcherView.getSelectedModel().getModelId();
					Main.SETTINGS.setStringProperty(
							"searcher.task.selected.value",
							new ModelIdSettingsCoder().encode(id));
				}
				
				return;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		Main.SETTINGS.setStringProperty("searcher.task.selected.value", null);
	}
	
}
