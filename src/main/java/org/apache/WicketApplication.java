package org.apache;

import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.ws.WebSocketAwareCsrfPreventionRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.FilteringHeaderResponse;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 *
 * @see org.apache.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
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
		getRequestCycleListeners().add(new WebSocketAwareCsrfPreventionRequestCycleListener() {
			@Override
			public void onEndRequest(RequestCycle cycle) {
				Response resp = cycle.getResponse();
				if (resp instanceof WebResponse) {
					WebResponse wresp = (WebResponse)resp;
					if (wresp.isHeaderSupported()) {
						wresp.setHeader("X-XSS-Protection", "1; mode=block");
						wresp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
						wresp.setHeader("X-Content-Type-Options", "nosniff");
					}
				}
			}
		});
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
