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
    private final static String DETECT_FULL_HTML_PATTERN = "(?:<[img|div|span|iframe].*?>)|(?:yt:)|(?:ytp:)";
    private final static Pattern FULL_HTML_PATTERN = Pattern.compile(DETECT_FULL_HTML_PATTERN);
    @SuppressWarnings("EscapedSpace")
    private final static String DETECT_YT_IFRAME_PATTERN = "yt:(http[^\s\n]*)";
    private final static Pattern YT_IFRAME_PATTERN = Pattern.compile(DETECT_YT_IFRAME_PATTERN);
    @SuppressWarnings("EscapedSpace")
    private final static String DETECT_YT_PORTRAIT_IFRAME_PATTERN = "ytp:(http[^\s\n]*)";
    private final static Pattern YT_PORTRAIT_IFRAME_PATTERN = Pattern.compile(DETECT_YT_PORTRAIT_IFRAME_PATTERN);

    private static HtmlUtil INSTANCE;

    @NonNull
    public static synchronized HtmlUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HtmlUtil();
        }
        return INSTANCE;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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

    public String replaceYtIframe(String source, int width, int height) {
        if (height > width) {
            int newWidth = width - 20;
            int newHeight = (int) (newWidth * 0.5625);
            return replaceYtIframe(source, YT_IFRAME_PATTERN, newWidth, newHeight);
        } else {
            int newHeight = (int) (0.50 * height);
            int newWidth =  (int) (1.77 * newHeight);
            return replaceYtIframe(source, YT_IFRAME_PATTERN, newWidth, newHeight);
        }
    }

    public String replaceYtPortraitIframe(String source, int height) {
        int newHeight = (int) (0.80 * height);
        int newWidth =  (int) (0.5625 * newHeight);
        return replaceYtIframe(source, YT_PORTRAIT_IFRAME_PATTERN, newWidth, newHeight);
    }

    private String replaceYtIframe(String source, Pattern pattern, int width, int height) {
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            String full = Objects.requireNonNull(matcher.group(0));
            String url = matcher.group(1);
            String iframe = youtubeTemplate(url, width, height);
            source = source.replace(full, iframe);
        }
        return source;
    }

    private String youtubeTemplate(String url, int width, int height) {
        return "<iframe width=\"" + width + "\" height=\"" + height + "\" src=\"" + url + "\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
    }
}