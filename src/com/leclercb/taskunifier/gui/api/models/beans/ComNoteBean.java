package com.leclercb.taskunifier.gui.api.models.beans;

import java.io.InputStream;

import com.leclercb.taskunifier.api.models.ModelId;
import com.leclercb.taskunifier.api.models.beans.NoteBean;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ComNoteBean extends NoteBean {
	
	@XStreamAlias("foldertitle")
	private String folderTitle;
	
	public ComNoteBean() {
		this(null);
	}
	
	public ComNoteBean(ModelId modelId) {
		super(modelId);
		this.setFolderTitle(null);
	}
	
	public String getFolderTitle() {
		return this.folderTitle;
	}
	
	public void setFolderTitle(String folderTitle) {
		this.folderTitle = folderTitle;
	}
	
	public static ComNoteBean decodeFromXML(InputStream input) {
		XStream xstream = new XStream(
				new PureJavaReflectionProvider(),
				new DomDriver("UTF-8"));
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("note", ComNoteBean.class);
		xstream.processAnnotations(ComNoteBean.class);
		
		return (ComNoteBean) xstream.fromXML(input);
	}
	
}