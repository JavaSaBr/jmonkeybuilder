package com.ss.editor.ui.control.code;

import com.ss.editor.annotation.FXThread;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.undo.UndoManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The base implementation of code area control for this editor.
 *
 * @author JavaSaBr
 */
public class BaseCodeArea extends CodeArea {

    public BaseCodeArea() {
        richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> setStyleSpans(0, calculateStyleSpans(getText())));
    }

    /**
     * Gets style spans.
     *
     * @param text the text
     * @return the style spans
     */
    @FXThread
    protected @NotNull StyleSpans<? extends Collection<String>> calculateStyleSpans(@NotNull final String text) {
        throw new RuntimeException("unsupported");
    }

    @FXThread
    public void loadContent(@NotNull final String content) {
        appendText(content);

        final UndoManager undoManager = getUndoManager();
        undoManager.forgetHistory();
    }

    @FXThread
    public void reloadContent(@NotNull final String content) {
        final String currentContent = getText();
        replaceText(0, currentContent.length(), content);
    }
}
