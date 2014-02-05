package krasa.merge.frontend.component.merge;

import krasa.core.frontend.components.BasePanel;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.service.MergeService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * @author Vojtech Krasa
 */
public class DiffPanel extends BasePanel {

	@SpringBean
	protected MergeService mergeService;

	public DiffPanel(String id, final IModel<SVNLogEntry> revision, final IModel<MergeInfoResultItem> model) {
		super(id);
		final Label diff = new Label("diff", getDiffModel(revision, model));
		diff.setOutputMarkupPlaceholderTag(true);
		add(diff);
	}

	protected IModel<String> getDiffModel(final IModel<SVNLogEntry> revision, final IModel<MergeInfoResultItem> model) {
		IModel<String> diff = new AbstractReadOnlyModel<String>() {
			@Override
			public String getObject() {
				return mergeService.getDiff(model.getObject(), revision.getObject());
			}
		};
		return diff;
	}

}
