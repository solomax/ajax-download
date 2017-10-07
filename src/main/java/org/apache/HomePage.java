package org.apache;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	private File dwnldFile;
	WebMarkupContainer container = new WebMarkupContainer("container");
	private final AbstractDefaultAjaxBehavior pageLoad = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			target.add(container.replace(new WebSocketPanel("socketPanel")));
		}
	};

	public HomePage(final PageParameters parameters) {
		super(parameters);
		WebSession.get().setLocale(new Locale.Builder().setLanguage("es").setRegion("CO").build());

		add(pageLoad);
		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		add(container.add(new EmptyPanel("socketPanel")).setOutputMarkupId(true));
		final AjaxDownloadBehavior download = new AjaxDownloadBehavior(new IResource() {
			private static final long serialVersionUID = 1L;

			@Override
			public void respond(Attributes attributes) {
				new FileSystemResource(dwnldFile.toPath()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected ResourceResponse createResourceResponse(Attributes attrs, Path path) {
						ResourceResponse response = super.createResourceResponse(attrs, path);
						response.setContentDisposition(ContentDisposition.ATTACHMENT);
						return response;
					}
				}.respond(attributes);
			}
		});
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(download);
		add(new Form<Void>("form").add(
				feedback.setOutputMarkupId(true)
				, new AjaxButton("download-zip-blob") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						createZip();
						info("ZIP Blob, filename: " + dwnldFile.toPath().getFileName());
						target.add(feedback);
						download.setLocation(AjaxDownloadBehavior.Location.Blob).initiate(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				}
				, new AjaxButton("download-text-blob") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						createText();
						info("Text Blob, filename: " + dwnldFile.toPath().getFileName());
						target.add(feedback);
						download.setLocation(AjaxDownloadBehavior.Location.Blob).initiate(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				}
				, new AjaxButton("download-zip-frame") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						createZip();
						info("ZIP iFrame, filename: " + dwnldFile.toPath().getFileName());
						target.add(feedback);
						download.setLocation(AjaxDownloadBehavior.Location.IFrame).initiate(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				}
				, new AjaxButton("download-text-frame") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						createText();
						info("Text iFrame, filename: " + dwnldFile.toPath().getFileName());
						target.add(feedback);
						download.setLocation(AjaxDownloadBehavior.Location.IFrame).initiate(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				}
				, new Button("oauthBtn").add(new Label("label", "Redirect to Google"))
					.add(new AjaxEventBehavior("click") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onEvent(AjaxRequestTarget target) {
							throw new RedirectToUrlException("https://google.com");
						}
					})
			));
	}

	private void createText() {
		StringBuilder sb = new StringBuilder("Test String ");
		try {
			dwnldFile = File.createTempFile("wicketTest", ".html");
			Files.write(dwnldFile.toPath(), sb.append(dwnldFile.getCanonicalPath()).toString().getBytes(UTF_8), StandardOpenOption.WRITE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void createZip() {
		StringBuilder sb = new StringBuilder("Test String ");
		try {
			dwnldFile = File.createTempFile("wicketTest", ".zip");
			try (ZipOutputStream _zip = new ZipOutputStream(new FileOutputStream(dwnldFile))) {
				ZipEntry e = new ZipEntry("mytext.txt");
				_zip.putNextEntry(e);

				byte[] data = sb.append(dwnldFile.getCanonicalPath()).toString().getBytes(UTF_8);
				_zip.write(data, 0, data.length);
				_zip.closeEntry();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(OnDomReadyHeaderItem.forScript(pageLoad.getCallbackScript()));
	}
}
