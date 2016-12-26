package com.ss.editor.ui.component.log;

import com.ss.editor.ui.css.CSSClasses;

import javafx.scene.control.TextArea;
import rlib.logging.LoggerListener;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;

/**
 * Implement the view for showing log messages from the editor.
 *
 * @author JavaSaBr
 */
public class LogView extends TextArea implements LoggerListener {

    private static final LogView INSTANCE = new LogView();

    public static LogView getInstance() {
        return INSTANCE;
    }

    public LogView() {
        setWrapText(true);
        LoggerManager.addListener(this);
        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_14);
    }

    @Override
    public void println(final String text) {
        appendText(text + "\n");
    }
}
