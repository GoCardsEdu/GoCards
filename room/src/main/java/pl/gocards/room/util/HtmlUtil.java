package pl.gocards.room.util;

import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Grzegorz Ziemski
 */
public class HtmlUtil {

    private final static String DETECT_HTML_PATTERN = "<.*?>";
    private final static Pattern HTML_PATTERN = Pattern.compile(DETECT_HTML_PATTERN);
    private final static String DETECT_FULL_HTML_PATTERN = "(?:<[img|div|span|iframe].*?>)|(?:yt:)";
    private final static Pattern FULL_HTML_PATTERN = Pattern.compile(DETECT_FULL_HTML_PATTERN);
    private final static String DETECT_YT_IFRAME_PATTERN = "yt:(http[^\s\n]*)";
    private final static Pattern YT_IFRAME_PATTERN = Pattern.compile(DETECT_YT_IFRAME_PATTERN);

    private static HtmlUtil INSTANCE;

    @NonNull
    public static synchronized HtmlUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HtmlUtil();
        }
        return INSTANCE;
    }

    public boolean isSimpleHtml(@Nullable String s) {
        boolean ret = false;
        if (s != null) {
            ret = HTML_PATTERN.matcher(s).find();
        }
        return ret;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isFullHtml(@Nullable String s) {
        boolean ret = false;
        if (s != null) {
            ret = FULL_HTML_PATTERN.matcher(s).find();
        }
        return ret;
    }

    public Spanned fromHtml(@NonNull String source) {
        return Html.fromHtml(
                source.replace("\n", "<br/>"),
                Html.FROM_HTML_MODE_COMPACT
        );
    }

    public String replaceYtIframe(String source, Integer width) {
        Matcher matcher = YT_IFRAME_PATTERN.matcher(source);

        while (matcher.find()) {
            int height = (int) (width * 0.5625);
            String full = Objects.requireNonNull(matcher.group(0));
            String url = matcher.group(1);
            String iframe = "<iframe width=\"" + width + "\" height=\"" + height + "\" src=\"" + url + "\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
            source = source.replace(full, iframe);
        }
        return source;
    }
}