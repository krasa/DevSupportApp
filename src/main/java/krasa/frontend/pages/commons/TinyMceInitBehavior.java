package krasa.frontend.pages.commons;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;


public class TinyMceInitBehavior extends AbstractBehavior {
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see org.apache.wicket.behavior.AbstractBehavior#renderHead(
org.apache.wicket.markup.html.IHeaderResponse)
     */
    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        // you'll need to change this
        response.renderJavaScriptReference("/tiny_mce/tiny_mce.js");

        // initialize tinyMCE
        StringBuilder initBuilder = new StringBuilder();
        initBuilder.append("tinyMCE.init({\n")
                .append("theme: \"advanced\",\n")
                .append("theme_advanced_toolbar_location : \"top\",\n")
                .append("theme_advanced_toolbar_align : \"left\",\n")
                .append("theme_advanced_buttons1 : fontselect,fontsizeselect,undo,redo,separator,forecolor,separator,bold,italic,underline,separator,justifyleft,justifycenter,justifyright,separator,bullist,numlist,separator,outdent,indent\",\n")
                .append("theme_advanced_buttons2 : cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor\",\n")
                .append("theme_advanced_buttons3 : \"\",\n")
                .append("theme_advanced_buttons4 : \"\",\n")
                .append("mode : \"exact\"\n")
                .append("});");
        response.renderJavaScript(initBuilder.toString(), "init");
    }
}
//   <script type="text/javascript" src="/tiny_mce/tiny_mce.js"></script>
//        <script type="text/javascript">
//            tinyMCE.init({
//                // General options
//                mode : "textareas",
//                theme : "advanced",
//                plugins : "safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager,filemanager",
//
//                // Theme options
//                theme_advanced_buttons1 : "save,newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,styleselect,formatselect,fontselect,fontsizeselect",
//                theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor",
//                theme_advanced_buttons3 : "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen",
//                theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,spellchecker,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,blockquote,pagebreak,|,insertfile,insertimage",
//                theme_advanced_toolbar_location : "top",
//                theme_advanced_toolbar_align : "left",
//                theme_advanced_statusbar_location : "bottom",
//                theme_advanced_resizing : true,
//
//                // Example content CSS (should be your site CSS)
//                content_css : "/css/main.css",
//
//                // Drop lists for link/image/media/template dialogs
//                template_external_list_url : "js/template_list.js",
//                external_link_list_url : "js/link_list.js",
//                external_image_list_url : "js/image_list.js",
//                media_external_list_url : "js/media_list.js"
//
//            });