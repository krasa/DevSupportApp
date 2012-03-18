package krasa.frontend.pages.components;

/**
 * @author Vojtech Krasa
 */
public class SelectionWrapper<T> {
    private boolean selected;
    private T object;

    public SelectionWrapper(T object) {
        this.object = object;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
