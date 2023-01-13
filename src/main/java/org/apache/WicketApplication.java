package org.apache;

import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.FilteringHeaderResponse;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 *
 * @see org.apache.Start#main(String[])
 */
@Component
public class WicketApplication extends WebApplication
{
	@Inject
	private ApplicationContext ctx;

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));
		getHeaderResponseDecorators().add(new IHeaderResponseDecorator() {
			@Override
			public IHeaderResponse decorate(IHeaderResponse response) {
				return new FilteringHeaderResponse(response);
			}
		});
		getCspSettings().blocking().strict();
		super.init();
		mountPage("csstest", CssTestPage.class);
	}
}
