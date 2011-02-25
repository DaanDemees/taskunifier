package com.leclercb.taskunifier.gui.components.export_data;

import java.awt.Frame;
import java.io.FileOutputStream;

import com.leclercb.commons.api.coder.FactoryCoder;
import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.gui.translations.Translations;

public class DefaultExportDialog extends AbstractExportDialog {
	
	private FactoryCoder coder;
	
	public DefaultExportDialog(
			FactoryCoder coder,
			String title,
			Frame frame,
			boolean modal) {
		super(
				title,
				frame,
				modal,
				"xml",
				Translations.getString("general.xml_files"));
		
		CheckUtils.isNotNull(coder, "Coder cannot be null");
		
		this.coder = coder;
	}
	
	@Override
	protected void exportToFile(String file) throws Exception {
		FileOutputStream output = new FileOutputStream(file);
		this.coder.encode(output);
	}
	
}
