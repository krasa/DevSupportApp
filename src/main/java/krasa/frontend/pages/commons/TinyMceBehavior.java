package krasa.frontend.pages.commons;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.JavaScriptUtils;

public class TinyMceBehavior extends AbstractDefaultAjaxBehavior {
    private static final long serialVersionUID = 1L;

    boolean ajax;

    /**
     *
     */
    public TinyMceBehavior(boolean ajax) {
        this.ajax = ajax;
    }

    /* (non-Javadoc)
     * @see
org.apache.wicket.behavior.AbstractAjaxBehavior#onComponentRendered()
     */

    @Override
    protected void onComponentRendered() {
        super.onComponentRendered();
        if (ajax) {
            // load editor script
            Response response = getComponent().getResponse();
            JavaScriptUtils.writeJavaScript(response, getCallbackScript(),
                    "load");
        } else {
            Response response = getComponent().getResponse();
            JavaScriptUtils.writeJavaScript(response,
                    "tinyMCE.execCommand('mceAddControl', true, '" +
                            getComponent().getMarkupId() + "');");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.ajax.AbstractDefaultAjaxBehavior#respond(
org.apache.wicket.ajax.AjaxRequestTarget)
     */

    @Override
    protected void respond(AjaxRequestTarget target) {
        StringBuilder builder = new StringBuilder();
        builder.append("\ntinyMCE.execCommand('mceAddControl', true, '"
                + getComponent().getMarkupId() + "');");

        //target.appendJavascript(JavascriptUtils.SCRIPT_OPEN_TAG);
        target.appendJavaScript(builder.toString());
        //target.appendJavascript(JavascriptUtils.SCRIPT_CLOSE_TAG);
    }

}