package org.apache;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.AjaxDownload;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.FileSystemResource;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	private File zipFile;

	public HomePage(final PageParameters parameters) {
		super(parameters);

		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		final AjaxDownload download = new AjaxDownload(new FileSystemResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ResourceResponse createResourceResponse(Path path) {
				return super.createResourceResponse(zipFile.toPath());
			}
		});
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(download);
		add(new Form<Void>("form").add(
				feedback.setOutputMarkupId(true)
				, new AjaxButton("download") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						StringBuilder sb = new StringBuilder("Test String");
						try {
							zipFile = File.createTempFile("wicketTest", ".zip");
							try (ZipOutputStream _zip = new ZipOutputStream(new FileOutputStream(zipFile))) {
								ZipEntry e = new ZipEntry("mytext.txt");
								_zip.putNextEntry(e);

								byte[] data = sb.toString().getBytes(UTF_8);
								_zip.write(data, 0, data.length);
								_zip.closeEntry();
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						download.initiate(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				}));
	}
}
