package krasa.svn.frontend.component.table;

import java.util.*;

import krasa.core.frontend.commons.table.ButtonColumn;
import krasa.svn.backend.domain.SvnFolder;

import krasa.svn.backend.facade.SvnFacade;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class SelectedBranchesTablePanel extends BranchesTablePanel {

	@SpringBean
	private SvnFacade facade;

	public SelectedBranchesTablePanel(String id) {
		super(id);
	}

	@Override
	protected List<IColumn<SvnFolder, String>> createColumns() {
		List<IColumn<SvnFolder, String>> columns = new ArrayList<>();
		columns.add(createNameColumn());
		columns.add(createSearchFromColumn());
		columns.add(createDeleteColumn());
		return columns;
	}

	private ButtonColumn<SvnFolder> createDeleteColumn() {
		return new ButtonColumn<SvnFolder>(new Model<>("Delete")) {

			@Override
			protected void onSubmit(IModel<SvnFolder> model, AjaxRequestTarget target) {
				facade.updateSelectionOfSvnFolder(model.getObject(), false);
				target.add(form);
			}
		};
	}
}
