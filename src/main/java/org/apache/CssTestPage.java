package org.apache;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

public class CssTestPage extends WebPage {
	private static final long serialVersionUID = 1L;
	public static final String HEAD_TEXT = "#invisible { display:none; }\nbody {background-color:#ccc; }\n";

	public CssTestPage() {
		final WebMarkupContainer fixme1 = new WebMarkupContainer("fixme1");
		fixme1.setOutputMarkupId(true);
		final WebMarkupContainer fixme = new WebMarkupContainer("fixme");
		add(fixme1, fixme.
			add(new AjaxEventBehavior("click") {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void onEvent(AjaxRequestTarget target) {
					target.add(fixme1.add(new AbstractDefaultAjaxBehavior() {
						private static final long serialVersionUID = 1L;

						@Override
						protected void respond(AjaxRequestTarget target) {
						}
						
						@Override
						public void renderHead(Component component, IHeaderResponse response) {
							super.renderHead(component, response);
							response.render(new CssContentHeaderItem(HEAD_TEXT, "mystyle", null));
						}
					}));
				}
			}));
	}
}
