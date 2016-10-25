package de.strubel.customcomponent;

import java.util.ArrayList;

import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.plugin.api.ContainerExtension;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

@VisPlugin
public class AccessProjectModule extends ProjectModule implements ContainerExtension {
	
	private static AccessProjectModule instance;
	
	@Override
	public void init() {
		instance = this;
	}
	
	public ArrayList<EntityProperties> getAllEntityProperties() {
		
		ArrayList<EntityProperties> entityPList = new ArrayList<>();
		
		for (SceneModuleContainer sceneMC : this.getAllSceneModuleContainers()) {
			EntityManipulatorModule entityMC = sceneMC.get(EntityManipulatorModule.class);
			entityPList.add(entityMC.getEntityProperties());
		}
		
		return entityPList;
	}
	
	public ArrayList<SceneModuleContainer> getAllSceneModuleContainers() {

		ArrayList<SceneModuleContainer> sceneMCList = new ArrayList<SceneModuleContainer>();
		
		if (this.projectContainer != null) {
			SceneTabsModule sceneTabModule = this.projectContainer.get(SceneTabsModule.class);

			for (SceneTab sceneTab : sceneTabModule.getSceneTabs()) {
				sceneMCList.add(sceneTab.getSceneMC());
			}
		}
		return sceneMCList;
	}
	
	@Override
	public ExtensionScope getScope () {
		return ExtensionScope.PROJECT;
	}
	
	public static AccessProjectModule getInstance() {
		return instance;
	}
	
}
