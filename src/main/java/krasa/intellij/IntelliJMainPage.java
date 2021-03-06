package krasa.intellij;

import java.util.*;

import krasa.core.frontend.commons.table.DummyModelDataProvider;
import krasa.core.frontend.pages.BasePage;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.Model;

/**
 * @author Vojtech Krasa
 */
public class IntelliJMainPage extends BasePage {

	public IntelliJMainPage() {
		PluginUploadForm progressUploadForm = new PluginUploadForm("progressUpload");
		progressUploadForm.add(new UploadProgressBar("progress", progressUploadForm,
				progressUploadForm.getFileUploadField()));
		queue(progressUploadForm);

		queue(new BookmarkablePageLink<>("repoUrl", IntelliJEnterprisePluginRepositoryPage.class));

		List<IColumn<PluginDefinition, String>> iColumns = new ArrayList<IColumn<PluginDefinition, String>>();
		iColumns.add(new PropertyColumn<PluginDefinition, String>(Model.of("id"), "id"));
		iColumns.add(new PropertyColumn<PluginDefinition, String>(Model.of("version"), "version"));
		iColumns.add(new PropertyColumn<PluginDefinition, String>(Model.of("fileName"), "fileName"));
		queue(new AjaxFallbackDefaultDataTable<PluginDefinition, String>("table", iColumns,
				new DummyModelDataProvider<PluginDefinition>(new IntelliJPluginsModel()), 50));
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new EmptyPanel(id);
	}
}
