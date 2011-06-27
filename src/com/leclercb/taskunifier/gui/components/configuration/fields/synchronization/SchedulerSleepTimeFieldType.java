package com.leclercb.taskunifier.gui.components.configuration.fields.synchronization;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.leclercb.taskunifier.gui.components.configuration.api.ConfigurationFieldTypeExt;
import com.leclercb.taskunifier.gui.main.Main;
import com.leclercb.taskunifier.gui.utils.review.Reviewed;

@Reviewed
public class SchedulerSleepTimeFieldType extends ConfigurationFieldTypeExt.Spinner {
	
	public SchedulerSleepTimeFieldType() {
		super(Main.SETTINGS, "synchronizer.scheduler_sleep_time");
		
		this.setModel(new SpinnerNumberModel(
				(Number) this.getPropertyValue(),
				10,
				5 * 3600,
				60));
		
		this.setEditor(new JSpinner.NumberEditor(this));
	}
	
	@Override
	public Object getPropertyValue() {
		return (int) (Main.SETTINGS.getLongProperty("synchronizer.scheduler_sleep_time") / 1000);
	}
	
	@Override
	public void saveAndApplyConfig() {
		Main.SETTINGS.setLongProperty(
				"synchronizer.scheduler_sleep_time",
				((Integer) this.getFieldValue()) * 1000l);
	}
	
}
