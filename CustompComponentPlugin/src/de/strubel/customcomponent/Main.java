package de.strubel.customcomponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
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
	
	private CustomComponentSettingsModule compSettingsModule;
	private FileMonitor monitor;
	
	private static Main instance;
	
    @SuppressWarnings("all")
	@Override
    public void init () {
    	
    	compSettingsModule = new CustomComponentSettingsModule();
    	Editor.instance.getEditorModuleContainer().add(compSettingsModule);
    	
        Log.info(this.getClass().getName(), "Hello from plugin!");
    	
    	instance = this;
    }
    
    @Override
    public void postInit () {
        
    	defaultUserCompArray.addAll(extensionStorage.getUserAddableComponentProviders());
    	defaultCompTableArray.addAll(extensionStorage.getComponentTableProviders());
		
		this.reloadCustomComponents();
		
		monitor = FileMonitor.getInstance();
		try {
			monitor.addFileChangeListener(new FileListenerImpl(), new File(compSettingsModule.getPath()), 500);
		} catch (FileNotFoundException e) {
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
	
	public FileMonitor getFileMonitor() {
		return monitor;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
//	public static ArrayList<Class<? extends Component>> getCustomComponents() {
//		ArrayList<Class<? extends Component>> customCompList = new ArrayList<>();
//		customCompList.add(TestComponent.class);
//		return customCompList;
//	}
	
}
