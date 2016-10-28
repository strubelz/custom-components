package de.strubel.customcomponent;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.SkipInject;
import com.kotcrab.vis.editor.module.editor.EditorModule;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.plugin.api.ComponentTableProvider;
import com.kotcrab.vis.editor.plugin.api.ContainerExtension;
import com.kotcrab.vis.editor.plugin.api.UserAddableComponentProvider;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

@VisPlugin
public class Main extends EditorModule implements ContainerExtension {
	
	private ExtensionStorageModule extensionStorage;
	
	private final String METHOD_NAME = "getCustomComponents";
	
	private ArrayList<Class<? extends Component>> compList = new ArrayList<Class<? extends Component>>();
	private Array<UserAddableComponentProvider> defaultUserCompArray = new Array<>();
	private Array<ComponentTableProvider> defaultCompTableArray = new Array<>();
	
	private WatchHandler handler;
	private WatchService watcher;
	private Path watchPath;
	private WatchKey key;
	
	@SkipInject
	private CustomComponentSettingsModule compSettingsModule;
	
	private static Main instance;
	
    @SuppressWarnings("all")
	@Override
    public void init () {
    	Log.fatal("init()");
    	compSettingsModule = new CustomComponentSettingsModule();
    	Editor.instance.getEditorModuleContainer().add(compSettingsModule);
    	
        Log.info(this.getClass().getName(), "Hello from plugin!");
    	
    	instance = this;
    }
    
    @Override
    public void postInit () {
        Log.fatal("postInit()");
    	defaultUserCompArray.addAll(extensionStorage.getUserAddableComponentProviders());
    	defaultCompTableArray.addAll(extensionStorage.getComponentTableProviders());
		
		this.reloadCustomComponents();
		
		watchPath = Paths.get(compSettingsModule.getPath()).getParent();
		FileSystem fs = watchPath.getFileSystem();
		
		try {
			
			watcher = fs.newWatchService();
			key = watchPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			handler = new WatchHandler(watcher, watchPath);
			handler.getListeners().add(new WatchListenerImpl());
			
			handler.start();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	@Override
	public ExtensionScope getScope () {
		return ExtensionScope.EDITOR;
	}
	
	@Override
	public void dispose () {
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void reloadCustomComponents() {
		
		if (!compSettingsModule.isEdited())
			return;
		
		Log.info("Reloading custom components");
		
		File jar = new File(compSettingsModule.getPath());
		
		try {
			URLClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, this.getClass().getClassLoader());
			Class<?> clazz = Class.forName(compSettingsModule.getClassName(), true, loader);
			Method method = clazz.getDeclaredMethod(METHOD_NAME);
			compList = (ArrayList<Class<? extends Component>>) method.invoke(null);
		} catch (Exception e) {
			Log.error("An error occured while loading '" + jar.getAbsolutePath() + "'");
			e.printStackTrace();
		}
		
		for (UserAddableComponentProvider provider : extensionStorage.getUserAddableComponentProviders()) {
			if (!defaultUserCompArray.contains(provider, false)) {
				extensionStorage.getUserAddableComponentProviders().removeValue(provider, false);
			}
		}
		
		for (ComponentTableProvider tableProvider : extensionStorage.getComponentTableProviders()) {
			if (!defaultCompTableArray.contains(tableProvider, false)) {
				extensionStorage.getComponentTableProviders().removeValue(tableProvider, false);
			}
		}
		
		for (Class<? extends Component> clazz : compList) {
			extensionStorage.addUserAddableComponentProvider(new GeneralComponentProvider(clazz));
			extensionStorage.addComponentTableProvider(new GeneralComponentTableProvider(clazz));
		}
		
		if (AccessProjectModule.getInstance() != null) {
			for (EntityProperties entityP : AccessProjectModule.getInstance().getAllEntityProperties()) {
				entityP.reloadComponents();
			}
		}
		
	}
	
	public static Main getInstance() {
		return instance;
	}

	public WatchHandler getWatchHandler() {
		return handler;
	}

	public WatchService getWatchService() {
		return watcher;
	}

	public Path getWatchPath() {
		return watchPath;
	}

	public void setWatchPath(Path watchPath) {
		this.watchPath = watchPath;
	}

	public CustomComponentSettingsModule getComponentSettingsModule() {
		return compSettingsModule;
	}

	public WatchKey getWatchKey() {
		return key;
	}

	public void setWatchKey(WatchKey key) {
		this.key = key;
	}
	
}
