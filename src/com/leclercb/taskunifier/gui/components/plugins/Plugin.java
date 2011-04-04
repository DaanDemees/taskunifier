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
package com.leclercb.taskunifier.gui.components.plugins;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.leclercb.commons.api.event.propertychange.PropertyChangeSupported;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.translations.Translations;

public class Plugin implements PropertyChangeSupported {
	
	public static enum PluginStatus {
		
		TO_INSTALL(Translations.getString("plugin.status.to_install")),
		INSTALLED(Translations.getString("plugin.status.installed")),
		TO_UPDATE(Translations.getString("plugin.status.to_update")),
		DELETED(Translations.getString("plugin.status.deleted"));
		
		private String label;
		
		private PluginStatus(String label) {
			this.label = label;
		}
		
		@Override
		public String toString() {
			return this.label;
		}
		
	}
	
	public static final String PROP_STATUS = "status";
	public static final String PROP_ID = "id";
	public static final String PROP_NAME = "name";
	public static final String PROP_AUTHOR = "author";
	public static final String PROP_VERSION = "version";
	public static final String PROP_SERVICE_PROVIDER = "serviceProvider";
	public static final String PROP_DOWNLOAD_URL = "downloadUrl";
	public static final String PROP_HISTORY = "history";
	public static final String PROP_PRICE = "price";
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private PluginStatus status;
	private String id;
	private String name;
	private String author;
	private String version;
	private String serviceProvider;
	private String downloadUrl;
	private String history;
	private String price;
	
	public Plugin(
			PluginStatus status,
			String id,
			String name,
			String author,
			String version,
			String serviceProvider,
			String downloadUrl,
			String history,
			String price) {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		
		this.setStatus(status);
		this.setId(id);
		this.setName(name);
		this.setAuthor(author);
		this.setVersion(version);
		this.setServiceProvider(serviceProvider);
		this.setDownloadUrl(downloadUrl);
		this.setHistory(history);
		this.setPrice(price);
	}
	
	public PluginStatus getStatus() {
		return this.status;
	}
	
	public void setStatus(PluginStatus status) {
		CheckUtils.isNotNull(status, "Status cannot be null");
		PluginStatus oldStatus = this.status;
		this.status = status;
		this.propertyChangeSupport.firePropertyChange(
				PROP_STATUS,
				oldStatus,
				status);
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		CheckUtils.isNotNull(id, "Id cannot be null");
		String oldId = this.id;
		this.id = id;
		this.propertyChangeSupport.firePropertyChange(PROP_ID, oldId, id);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		CheckUtils.isNotNull(name, "Name cannot be null");
		String oldName = this.name;
		this.name = name;
		this.propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public void setAuthor(String author) {
		CheckUtils.isNotNull(author, "Author cannot be null");
		String oldAuthor = this.author;
		this.author = author;
		this.propertyChangeSupport.firePropertyChange(
				PROP_AUTHOR,
				oldAuthor,
				author);
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		CheckUtils.isNotNull(version, "Version cannot be null");
		String oldVersion = this.version;
		this.version = version;
		this.propertyChangeSupport.firePropertyChange(
				PROP_VERSION,
				oldVersion,
				version);
	}
	
	public String getServiceProvider() {
		return this.serviceProvider;
	}
	
	public void setServiceProvider(String serviceProvider) {
		CheckUtils.isNotNull(serviceProvider, "Service provider cannot be null");
		String oldServiceProvider = this.serviceProvider;
		this.serviceProvider = serviceProvider;
		this.propertyChangeSupport.firePropertyChange(
				PROP_SERVICE_PROVIDER,
				oldServiceProvider,
				serviceProvider);
	}
	
	public String getDownloadUrl() {
		return this.downloadUrl;
	}
	
	public void setDownloadUrl(String downloadUrl) {
		CheckUtils.isNotNull(downloadUrl, "Download url cannot be null");
		String oldDownloadUrl = this.downloadUrl;
		this.downloadUrl = downloadUrl;
		this.propertyChangeSupport.firePropertyChange(
				PROP_DOWNLOAD_URL,
				oldDownloadUrl,
				downloadUrl);
	}
	
	public String getHistory() {
		return this.history;
	}
	
	public void setHistory(String history) {
		String oldHistory = this.history;
		this.history = history;
		this.propertyChangeSupport.firePropertyChange(
				PROP_HISTORY,
				oldHistory,
				history);
	}
	
	public String getPrice() {
		return this.price;
	}
	
	public void setPrice(String price) {
		CheckUtils.isNotNull(price, "Price cannot be null");
		String oldPrice = this.price;
		this.price = price;
		this.propertyChangeSupport.firePropertyChange(
				PROP_PRICE,
				oldPrice,
				price);
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
}
