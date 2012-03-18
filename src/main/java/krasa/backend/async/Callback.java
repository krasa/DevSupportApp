package krasa.backend.async;

import java.io.Serializable;

/**
 * @author Vojtech Krasa
 */
public interface Callback<T> extends Serializable {
    void process(T t);
}
