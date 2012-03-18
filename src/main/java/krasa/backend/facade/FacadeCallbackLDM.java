package krasa.backend.facade;

import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * @author Vojtech Krasa
 */
public abstract class FacadeCallbackLDM<T> extends LoadableDetachableModel<T> {
    @SpringBean
    transient Facade facade;

    @Override
    protected T load() {
        if (facade == null) {
            SpringComponentInjector.get().inject(this);
        }
        return loadObject();
    }

    protected Facade getFacade() {
        return facade;
    }

    protected abstract T loadObject();
}
