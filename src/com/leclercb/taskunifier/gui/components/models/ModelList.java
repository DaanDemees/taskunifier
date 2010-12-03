/*
 * TaskUnifier: Manage your tasks and synchronize them
 * Copyright (C) 2010  Benjamin Leclerc
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.leclercb.taskunifier.gui.components.models;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.gui.images.Images;
import com.leclercb.taskunifier.gui.models.ModelListModel;

abstract class ModelList extends JPanel {

	private JList modelList;
	private JButton addButton;
	private JButton removeButton;

	public ModelList(ModelListModel model) {
		this.initialize(model);
	}

	private void initialize(ModelListModel model) {
		this.setLayout(new BorderLayout());

		modelList = new JList();
		modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modelList.setModel(model);
		modelList.setBorder(new LineBorder(Color.BLACK));
		modelList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting())
					return;

				if (modelList.getSelectedValue() == null) {
					removeButton.setEnabled(false);
				} else {
					modelSelected((Model) modelList.getSelectedValue());
					removeButton.setEnabled(true);
				}
			}

		});

		this.add(modelList, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		this.add(buttonsPanel, BorderLayout.SOUTH);

		this.initializeButtons(buttonsPanel);
	}

	private void initializeButtons(JPanel buttonsPanel) {
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("ADD"))
					addModel();
				else
					removeModel((Model) modelList.getSelectedValue());
			}

		};

		addButton = new JButton(Images.getImage("add.png", 16, 16));
		addButton.setActionCommand("ADD");
		addButton.addActionListener(listener);
		buttonsPanel.add(addButton);

		removeButton = new JButton(Images.getImage("remove.png", 16, 16));
		removeButton.setActionCommand("REMOVE");
		removeButton.addActionListener(listener);
		removeButton.setEnabled(false);
		buttonsPanel.add(removeButton);
	}

	public void setSelectedModel(Model model) {
		modelList.setSelectedValue(model, true);
	}

	public Model getSelectedModel() {
		return (Model) modelList.getSelectedValue();
	}

	public abstract void addModel();

	public abstract void removeModel(Model model);

	public abstract void modelSelected(Model model);

}
