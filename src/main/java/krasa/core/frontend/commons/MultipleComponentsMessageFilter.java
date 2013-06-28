package krasa.core.frontend.commons;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.util.lang.Objects;

public class MultipleComponentsMessageFilter implements IFeedbackMessageFilter {
	private static final long serialVersionUID = 1L;

	/**
	 * The component to accept feedback messages for
	 */
	private final Set<Component> components = new HashSet<>();
	// protection for duplicite messages
	private final Set<Serializable> messages = new HashSet<>();

	/**
	 * Constructor
	 * 
	 * @param components
	 *            The component to filter on
	 */
	public MultipleComponentsMessageFilter(Component... components) {
		Collections.addAll(this.components, components);
	}

	public MultipleComponentsMessageFilter() {
	}

	/**
	 * Constructor
	 * 
	 * @param component
	 *            The component to filter on
	 */
	public MultipleComponentsMessageFilter(Component component) {
		components.add(component);
	}

	/**
	 * @see org.apache.wicket.feedback.IFeedbackMessageFilter#accept(org.apache.wicket.feedback.FeedbackMessage)
	 */
	@Override
	public boolean accept(FeedbackMessage message) {
		// protection for duplicite messages

		// for (Serializable m : messages) {
		// if (Objects.equal(m, message.getMessage())) {
		// return false;
		// }
		// if (m.equals(message.getMessage())) {
		// return false;
		// }
		// }
		for (Component component : components) {
			if (Objects.equal(component, message.getReporter())) {
				// messages.add(message.getMessage());
				return true;
			}
		}
		return false;
	}

	public void addComponent(Component component) {
		components.add(component);
	}

	public void removeComponent(Component component) {
		components.remove(component);
	}
}
