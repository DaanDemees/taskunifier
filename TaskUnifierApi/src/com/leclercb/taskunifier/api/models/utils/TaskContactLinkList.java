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
package com.leclercb.taskunifier.api.models.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.leclercb.commons.api.event.listchange.ListChangeEvent;
import com.leclercb.commons.api.event.listchange.ListChangeListener;
import com.leclercb.commons.api.event.listchange.ListChangeSupport;
import com.leclercb.commons.api.event.listchange.ListChangeSupported;
import com.leclercb.commons.api.utils.IgnoreCaseString;
import com.leclercb.taskunifier.api.models.ContactList;
import com.leclercb.taskunifier.api.models.ContactList.ContactItem;
import com.leclercb.taskunifier.api.models.Model;
import com.leclercb.taskunifier.api.models.ModelStatus;
import com.leclercb.taskunifier.api.models.Task;
import com.leclercb.taskunifier.api.models.TaskFactory;

public final class TaskContactLinkList implements ListChangeSupported, ListChangeListener, PropertyChangeListener {
	
	private static TaskContactLinkList INSTANCE;
	
	public static TaskContactLinkList getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskContactLinkList();
		
		return INSTANCE;
	}
	
	private ListChangeSupport listChangeSupport;
	
	private List<IgnoreCaseString> links;
	private SortedSet<IgnoreCaseString> sortedLinks;
	
	private TaskContactLinkList() {
		this.listChangeSupport = new ListChangeSupport(this);
		this.links = new ArrayList<IgnoreCaseString>();
		this.sortedLinks = new TreeSet<IgnoreCaseString>();
		
		this.initialize();
	}
	
	public String[] getLinks() {
		List<String> links = new ArrayList<String>();
		for (IgnoreCaseString link : this.sortedLinks)
			links.add(link.toString());
		
		return links.toArray(new String[0]);
	}
	
	private List<IgnoreCaseString> getLinks(ContactList contacts) {
		List<IgnoreCaseString> links = new ArrayList<IgnoreCaseString>();
		for (ContactItem item : contacts)
			if (item.getLink() != null)
				links.add(new IgnoreCaseString(item.getLink()));
		
		return links;
	}
	
	private void initialize() {
		List<Task> tasks = TaskFactory.getInstance().getList();
		
		for (Task task : tasks) {
			if (!task.getModelStatus().isEndUserStatus()) {
				continue;
			}
			
			task.getContacts().addListChangeListener(this);
			task.getContacts().addPropertyChangeListener(this);
			
			this.links.addAll(this.getLinks(task.getContacts()));
			this.sortedLinks.addAll(this.links);
		}
		
		TaskFactory.getInstance().addListChangeListener(this);
		TaskFactory.getInstance().addPropertyChangeListener(this);
	}
	
	@Override
	public void listChange(ListChangeEvent evt) {
		if (evt.getValue() instanceof Task) {
			Task task = (Task) evt.getValue();
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				if (task.getModelStatus().isEndUserStatus()) {
					task.getContacts().addListChangeListener(this);
					task.getContacts().addPropertyChangeListener(this);
					this.addLinks(task.getContacts());
				}
			}
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
				task.getContacts().removeListChangeListener(this);
				task.getContacts().removePropertyChangeListener(this);
				this.removeLinks(task.getContacts());
			}
		}
		
		if (evt.getValue() instanceof ContactItem) {
			ContactItem item = (ContactItem) evt.getValue();
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_ADDED) {
				this.addLink(item.getLink());
			}
			
			if (evt.getChangeType() == ListChangeEvent.VALUE_REMOVED) {
				this.removeLink(item.getLink());
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Task) {
			Task task = (Task) evt.getSource();
			
			if (evt.getPropertyName().equals(Model.PROP_MODEL_STATUS)) {
				ModelStatus oldStatus = (ModelStatus) evt.getOldValue();
				ModelStatus newStatus = (ModelStatus) evt.getNewValue();
				
				if (!oldStatus.isEndUserStatus() && newStatus.isEndUserStatus()) {
					task.getContacts().addListChangeListener(this);
					task.getContacts().addPropertyChangeListener(this);
					this.addLinks(task.getContacts());
				} else if (oldStatus.isEndUserStatus()
						&& !newStatus.isEndUserStatus()) {
					task.getContacts().removeListChangeListener(this);
					task.getContacts().removePropertyChangeListener(this);
					this.removeLinks(task.getContacts());
				}
			}
		}
		
		if (evt.getSource() instanceof ContactItem) {
			if (evt.getPropertyName().equals(ContactItem.PROP_LINK)) {
				String oldLink = (String) evt.getOldValue();
				String newLink = (String) evt.getNewValue();
				
				this.removeLink(oldLink);
				this.addLink(newLink);
			}
		}
	}
	
	private void addLinks(ContactList contacts) {
		for (ContactItem item : contacts) {
			this.addLink(item.getLink());
		}
	}
	
	private void addLink(String link) {
		if (link == null)
			return;
		
		IgnoreCaseString oLink = new IgnoreCaseString(link);
		this.links.add(oLink);
		
		if (!this.sortedLinks.contains(oLink)) {
			this.sortedLinks.add(oLink);
			
			int index = 0;
			Iterator<IgnoreCaseString> it = this.sortedLinks.iterator();
			while (it.hasNext()) {
				if (it.next().equals(oLink))
					break;
				
				index++;
			}
			
			this.listChangeSupport.fireListChange(
					ListChangeEvent.VALUE_ADDED,
					index,
					link);
		}
	}
	
	private void removeLinks(ContactList contacts) {
		for (ContactItem item : contacts) {
			this.removeLink(item.getLink());
		}
	}
	
	private void removeLink(String link) {
		if (link == null)
			return;
		
		IgnoreCaseString oLink = new IgnoreCaseString(link);
		this.links.remove(oLink);
		
		if (!this.links.contains(oLink)) {
			if (this.sortedLinks.contains(oLink)) {
				this.sortedLinks.remove(oLink);
				this.listChangeSupport.fireListChange(
						ListChangeEvent.VALUE_REMOVED,
						-1,
						link);
			}
		}
	}
	
	@Override
	public void addListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.addListChangeListener(listener);
	}
	
	@Override
	public void removeListChangeListener(ListChangeListener listener) {
		this.listChangeSupport.removeListChangeListener(listener);
	}
	
}
