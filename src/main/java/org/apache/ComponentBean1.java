package org.apache;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class ComponentBean1 {
	@Inject
	private ComponentBean2 bean2;

	void nonPublic() {
		bean2.method();
	}

	public void nonPrivate() {
		bean2.method();
	}
}