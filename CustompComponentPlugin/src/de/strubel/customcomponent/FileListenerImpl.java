package de.strubel.customcomponent;

import java.io.File;

public class FileListenerImpl implements FileChangeListener {

	@Override
	public void fileChanged(File file) {
		
		Main.getInstance().reloadCustomComponents();
		
	}

}
