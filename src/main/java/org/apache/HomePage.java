package org.apache;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.AjaxDownload;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;

import com.googlecode.wicket.kendo.ui.KendoCultureHeaderItem;
import com.googlecode.wicket.kendo.ui.form.datetime.local.DateTimePicker;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	private File dwnldFile;

	public HomePage(final PageParameters parameters) {
		super(parameters);
		WebSession.get().setLocale(new Locale.Builder().setLanguage("es").setRegion("CO").build());

		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		final AjaxDownload downloadBlob = new AjaxDownload(new IResource() {
			private static final long serialVersionUID = 1L;

			@Override
			public void respond(Attributes attributes) {
				new FileSystemResource(dwnldFile.toPath()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected ResourceResponse createResourceResponse(Path path) {
						ResourceResponse response = super.createResourceResponse(path);
						response.setFileName("" + path.getFileName());
						return response;
					}
				}.respond(attributes);
			}
		});
		final AjaxDownload downloadFrame = new AjaxDownload(new IResource() {
			private static final long serialVersionUID = 1L;

			@Override
			public void respond(Attributes attributes) {
				new FileSystemResource(dwnldFile.toPath()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected ResourceResponse createResourceResponse(Path path) {
						ResourceResponse response = super.createResourceResponse(path);
						response.setFileName("" + path.getFileName());
						return response;
					}
				}.respond(attributes);
			}
		}).setLocation(AjaxDownload.Location.IFrame);
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(downloadBlob, downloadFrame);
		add(new Form<Void>("form").add(
				feedback.setOutputMarkupId(true)
				, new AjaxButton("download-zip-blob") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						createZip();
						info("ZIP Blob, filename: " + dwnldFile.getName());
						target.add(feedback);
						downloadBlob.initiate(target);
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
						info("Text Blob, filename: " + dwnldFile.getName());
						target.add(feedback);
						downloadBlob.initiate(target);
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
						info("ZIP iFrame, filename: " + dwnldFile.getName());
						target.add(feedback);
						downloadFrame.initiate(target);
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
						info("Text iFrame, filename: " + dwnldFile.getName());
						target.add(feedback);
						downloadFrame.initiate(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						target.add(feedback);
					}
				}
				, new DateTimePicker("dateTime", Model.of(LocalDateTime.now()), WebSession.get().getLocale()).setLabel(Model.of("Test DateTime"))
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
		response.render(KendoCultureHeaderItem.of(WebSession.get().getLocale()));
	}
}
