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
package com.leclercb.taskunifier.gui.components.statusbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.leclercb.taskunifier.api.settings.Settings;
import com.leclercb.taskunifier.gui.translations.Translations;

public class StatusBar extends JPanel {

	private StatusElement lastSynchronizationDate;

	public StatusBar() {
		this.initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(1, 1, 1, 1));
		this.add(panel, BorderLayout.CENTER);

		this.lastSynchronizationDate = new StatusElement();
		panel.add(this.lastSynchronizationDate, BorderLayout.EAST);

		this.initializeLastSynchronizationDate();
	}

	private void initializeLastSynchronizationDate() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(Settings.getStringProperty("date.date_format") + " "
				+ Settings.getStringProperty("date.time_format"));

		String date = Translations.getString("statusbar.never");

		if (Settings.getCalendarProperty("synchronizer.last_synchronization_date") != null)
			date = dateFormat.format(Settings.getCalendarProperty("synchronizer.last_synchronization_date").getTime());

		this.lastSynchronizationDate.setText(Translations.getString("statusbar.last_synchronization_date") + ": "
				+ date);

		Settings.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals("synchronizer.last_synchronization_date")) {
					String date = Translations.getString("statusbar.never");

					if (Settings.getCalendarProperty("synchronizer.last_synchronization_date") != null)
						dateFormat.format(Settings.getCalendarProperty("synchronizer.last_synchronization_date")
								.getTime());

					StatusBar.this.lastSynchronizationDate.setText(Translations
							.getString("statusbar.last_synchronization_date") + ": " + date);
				}
			}

		});
	}

	private class StatusElement extends JPanel {

		private JLabel label;

		public StatusElement() {
			this.initialize();
		}

		public void setText(String text) {
			this.label.setText(text);
		}

		private void initialize() {
			this.setLayout(new BorderLayout());
			this.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			this.label = new JLabel();
			this.label.setHorizontalAlignment(SwingConstants.TRAILING);
			this.label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			this.add(this.label, BorderLayout.CENTER);
		}

	}

}
