package krasa.utils;

/**
 * @author Vojtech Krasa
 */
public class PathUtils {

    public static String getParentPath(String value) {
        int i = value.lastIndexOf("/");
        if (i == -1) {
            return value;
        }
        return value.substring(0, i - 1);
    }

    public static String getBranchPath(String value) {
        String substring = value.substring(1, value.length());
        int i = substring.indexOf("/");
        if (i == -1) {
            return value;
        }
        return substring.substring(i, substring.length());
    }
}
