package com.ss.editor.ui.component.log;

import static com.jme3x.jfx.util.JFXPlatform.runInFXThread;
import static java.util.Collections.singleton;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ColorSpace;
import com.ss.editor.ui.css.CSSIds;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.BufferOverflowException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implement the view for showing log messages from the editor.
 *
 * @author JavaSaBr
 */
public class LogView extends CodeArea {

    @NotNull
    private static final LogView INSTANCE = new LogView();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static LogView getInstance() {
        return INSTANCE;
    }

    @NotNull
    private static final String[] FRAMEWORKS = {
            "log4j",
            "java.lang.",
            "java.net.",
            "com.jme3.asset.",
            "com.jme3.export.",
            "com.jme3.util.",
            "com.jme3.material.",
            "com.ss.editor.ui.",
            "com.ss.editor.model.",
    };

    @NotNull
    private static final String[] CLASSES = {
            BufferOverflowException.class.getName(),
            NullPointerException.class.getName(),
            Material.class.getName(),
            ColorSpace.class.getName(),
            Texture.class.getName(),
            Texture2D.class.getName(),
            TextureCubeMap.class.getName(),
            BufferOverflowException.class.getSimpleName(),
            NullPointerException.class.getSimpleName(),
            Material.class.getSimpleName(),
            ColorSpace.class.getSimpleName(),
            Texture.class.getSimpleName(),
            Texture2D.class.getSimpleName(),
            TextureCubeMap.class.getSimpleName(),
    };

    @NotNull
    private static final String[] SEVERITIES = {
            "WARN", "INFO", "ERROR", "DEBUG", "WARNING"
    };

    @NotNull
    private static final String SEVERITY_PATTERN = "\\b(" + String.join("|", SEVERITIES) + ")\\b";

    @NotNull
    private static final String FRAMEWORK_PATTERN = "\\b(" + String.join("|", FRAMEWORKS) + ")\\b";

    @NotNull
    private static final String CLASS_PATTERN = "\\b(" + String.join("|", CLASSES) + ")\\b";

    @NotNull
    private static final Pattern PATTERN = Pattern.compile(
            "(?<SEVERITY>" + SEVERITY_PATTERN + ")"
                    + "|(?<FRAMEWORK>" + FRAMEWORK_PATTERN + ")"
                    + "|(?<CLASS>" + CLASS_PATTERN + ")"
    );

    @NotNull
    private static StyleSpans<Collection<String>> computeHighlighting(@NotNull final String text) {

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
     * Instantiates a new Log view.
     */
    public LogView() {
        setId(CSSIds.LOG_VIEW);
        setWrapText(true);
        setEditable(false);
        richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> setStyleSpans(0, computeHighlighting(getText())));
        System.setErr(new OutputStreamWrapper(System.err, externalAppendText()));
    }

    @NotNull
    private Consumer<String> externalAppendText() {
        return stringConsumer -> runInFXThread(() -> appendText(stringConsumer));
    }
}
