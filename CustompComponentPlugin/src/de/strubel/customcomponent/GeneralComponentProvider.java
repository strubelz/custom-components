package de.strubel.customcomponent;

import com.artemis.Component;
import com.kotcrab.vis.editor.plugin.api.UserAddableComponentProvider;

public class GeneralComponentProvider implements UserAddableComponentProvider {
	
	private Class<? extends Component> clazz;
	
	public GeneralComponentProvider(Class<? extends Component> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Class<? extends Component> provide() {
		return this.clazz;
	}

}
