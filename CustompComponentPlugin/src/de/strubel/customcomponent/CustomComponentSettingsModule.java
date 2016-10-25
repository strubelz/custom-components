package de.strubel.customcomponent;

import java.io.FileNotFoundException;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.kotcrab.vis.editor.module.editor.EditorSettingsModule;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import de.strubel.customcomponent.CustomComponentSettingsModule.CustomComponentConfig;

public class CustomComponentSettingsModule extends EditorSettingsModule<CustomComponentConfig> {
	
	private VisTextField pathTextField;
	private VisTextField classTextField;
	
	public CustomComponentSettingsModule() {
		super("Custom Components", "customComponentSettings", CustomComponentConfig.class);
	}

	@Override
	public void settingsApply() {
		config.path = pathTextField.getText();
		config.className = classTextField.getText();
		
		if (config.path.equals("") || config.className.equals("")) {
			config.edited = false;
			Main.getInstance().getFileMonitor().removeAllFileChangeListeners();
		}else {
			config.edited = true;
			Main.getInstance().getFileMonitor().removeAllFileChangeListeners();
			try {
				Main.getInstance().getFileMonitor().addFileChangeListener(new FileListenerImpl(), config.path, 500);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		settingsSave();
		Main.getInstance().reloadCustomComponents();
		
	}

	@Override
	protected void buildTable() {
		prepareTable();
		
		pathTextField = new VisTextField();
		VisTable pathTable = new VisTable();
		pathTable.add("Path (to Jar or Class file): ");
		pathTable.add(pathTextField);
		
		classTextField = new VisTextField();
		VisTable classTable = new VisTable();
		classTable.add("Full class name: ");
		classTable.add(classTextField);
		
		settingsTable.add(pathTable).row();
		settingsTable.add(classTable).row();
		settingsTable.add(new VisLabel("Leave one (or both) of the fields empty to ignore this settings."));
		
		
	}

	@Override
	protected void loadConfigToTable() {
		pathTextField.setText(config.path);
		classTextField.setText(config.className);
	}
	
	public String getPath() {
		return config.path;
	}
	
	public String getClassName() {
		return config.className;
	}
	
	public boolean isEdited() {
		return config.edited;
	}
	

	public static class CustomComponentConfig {
		@Tag(0) private String path = "";
		@Tag(1) private String className = "";
		@Tag(3) private boolean edited = false;
	}
	
}
