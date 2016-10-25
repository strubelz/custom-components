package de.strubel.customcomponent;

import com.artemis.Component;
import com.kotcrab.vis.runtime.util.autotable.ATProperty;

public class TestComponent extends Component {
	
	@ATProperty(fieldName = "TestBool")
	public boolean test;
	
}
