package com.ss.editor.ui.component.log;

import static java.util.Collections.singleton;

import com.jme3x.jfx.util.JFXPlatform;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.BufferOverflowException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rlib.ui.util.FXUtils;

/**
 * Implement the view for showing log messages from the editor.
 *
 * @author JavaSaBr
 */
public class LogView extends CodeArea {

    private static final LogView INSTANCE = new LogView();

    public static LogView getInstance() {
        return INSTANCE;
    }

    private static final String[] FRAMEWORKS = {
            "log4j",
            "com.jme3.util.",
            "com.jme3.material.",
            "com.ss.editor.ui.",
            "com.ss.editor.model.",
    };

    private static final String[] CLASSES = {
            BufferOverflowException.class.getName(),
            NullPointerException.class.getName(),

            BufferOverflowException.class.getSimpleName(),
            NullPointerException.class.getSimpleName(),
    };

    private static final String[] SEVERITIES = {
            "WARN", "INFO", "ERROR", "DEBUG", "WARNING"
    };

    private static final String SEVERITY_PATTERN = "\\b(" + String.join("|", SEVERITIES) + ")\\b";
    private static final String FRAMEWORK_PATTERN = "\\b(" + String.join("|", FRAMEWORKS) + ")\\b";
    private static final String CLASS_PATTERN = "\\b(" + String.join("|", CLASSES) + ")\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<SEVERITY>" + SEVERITY_PATTERN + ")"
                    + "|(?<FRAMEWORK>" + FRAMEWORK_PATTERN + ")"
                    + "|(?<CLASS>" + CLASS_PATTERN + ")"
    );

    private static StyleSpans<Collection<String>> computeHighlighting(final String text) {

        final Matcher matcher = PATTERN.matcher(text);
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastKwEnd = 0;

        while (matcher.find()) {

            String styleClass = matcher.group("SEVERITY") != null ? "log-severity" : null;

            if (styleClass == null) {
                styleClass = matcher.group("FRAMEWORK") != null ? "log-framework" : null;
            }

            if (styleClass == null) {
                styleClass = matcher.group("CLASS") != null ? "log-class" : null;
            }

            spansBuilder.add(singleton("plain-code"), matcher.start() - lastKwEnd);
            spansBuilder.add(singleton(styleClass), matcher.end() - matcher.start());

            lastKwEnd = matcher.end();
        }

        spansBuilder.add(singleton("plain-code"), text.length() - lastKwEnd);

        return spansBuilder.create();
    }

    /**
     * The stream err wrapper.
     */
    private final OutputStreamWrapper streamErrWrapper;

    public LogView() {
        setId(CSSIds.LOG_VIEW);
        setWrapText(true);
        richChanges().subscribe(change -> setStyleSpans(0, computeHighlighting(getText())));

        this.streamErrWrapper = new OutputStreamWrapper(System.err, externalAppendText());

        System.setErr(streamErrWrapper);

        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_13);
    }

    @NotNull
    protected Consumer<String> externalAppendText() {
        return stringConsumer -> JFXPlatform.runInFXThread(() -> appendText(stringConsumer));
    }
}
