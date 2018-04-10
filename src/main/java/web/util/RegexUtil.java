package web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author wangchen
 * @date 2018/4/10 16:19
 */
public class RegexUtil {
    public static String StringFilter(String str) throws PatternSyntaxException {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
