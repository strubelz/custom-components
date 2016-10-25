package de.strubel.customcomponent;

import com.artemis.Component;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.plugin.api.ComponentTableProvider;
import com.kotcrab.vis.editor.ui.scene.entityproperties.ComponentTable;
import com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.AutoComponentTable;

public class GeneralComponentTableProvider implements ComponentTableProvider {
	
	private Class<? extends Component> clazz;
	
	public GeneralComponentTableProvider(Class<? extends Component> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public ComponentTable<? extends Component> provide(SceneModuleContainer sceneMC) {
		return new AutoComponentTable<>(sceneMC, clazz, true);
	}
	
	
	
}
