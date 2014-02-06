package krasa.release;

import java.io.IOException;

import krasa.core.frontend.pages.BasePage;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.ComponentDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Vojtech Krasa
 */
public class TokenizationPage extends BasePage {
	private String branchNameSuffix;
	private String json;

	@SpringBean
	TokenizationService tokenizationService;

	public TokenizationPage() throws IOException {
		reset();

		final Form<Object> form = new Form<>("form");
		add(form);
		form.add(new TextField<>("branchNameSuffix", new ComponentDetachableModel<Object>()));
		form.add(new TextField<>("json", new ComponentDetachableModel<Object>()));
		form.add(new AjaxButton("reset") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				reset();
			}
		});
		form.add(new AjaxButton("setAsDefault") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					FileUtils.writeStringToFile(new ClassPathResource("tokenizationTemplate.json").getFile(), json);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		form.add(new AjaxButton("execute") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				final String tokenize = tokenizationService.tokenize(branchNameSuffix, json);
				final PageParameters parameters = new PageParameters();
				parameters.add("logName", tokenize);
				// setResponsePage(FileSystemLogPage.class, parameters);
			}
		});

	}

	private void reset() {
		try {
			json = FileUtils.readFileToString(new ClassPathResource("tokenizationTemplate.json").getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
