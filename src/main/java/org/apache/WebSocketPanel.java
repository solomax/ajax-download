package org.apache;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;

public class WebSocketPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public WebSocketPanel(String id) {
		super(id);
		add(new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

		});
	}
}
