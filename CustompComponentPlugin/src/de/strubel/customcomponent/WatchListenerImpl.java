package de.strubel.customcomponent;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;

public class WatchListenerImpl implements WatchListener {
	
	@Override
	public void fileChanged (FileHandle file) {
		Main.getInstance().reloadCustomComponents();
		Log.info("changed");
	}
	
	@Override
	public void fileDeleted (FileHandle file) {
		Main.getInstance().reloadCustomComponents();
		Log.info("deleted");
	}
	
	@Override
	public void fileCreated (FileHandle file) {
		Main.getInstance().reloadCustomComponents();
		Log.info("created");
	}
	
}
