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
package com.leclercb.taskunifier.gui.components.searcherlist.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.leclercb.commons.api.utils.CheckUtils;

public class TaskSearcherTransferable implements Transferable {
	
	public static final DataFlavor TASK_SEARCHER_FLAVOR = new DataFlavor(
			TaskSearcherTransferData.class,
			"TASK_SEARCHER_FLAVOR");
	public static final DataFlavor[] FLAVORS = { TASK_SEARCHER_FLAVOR };
	private static final List<DataFlavor> FLAVOR_LIST = Arrays.asList(FLAVORS);
	
	private TaskSearcherTransferData data;
	
	public TaskSearcherTransferable(TaskSearcherTransferData data) {
		CheckUtils.isNotNull(data, "Data cannot be null");
		
		this.data = data;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS;
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return FLAVOR_LIST.contains(flavor);
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return this.data;
	}
	
}