package com.ss.editor.ui.component.tab;

import com.ss.editor.ui.css.CSSIds;

import javafx.geometry.Side;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;

/**
 * The component to contain global left tool components.
 *
 * @author JavaSaBr
 */
public class GlobalLeftToolComponent extends TabToolComponent {

    /**
     * Instantiates a new Global left tool component.
     *
     * @param pane the pane
     */
    public GlobalLeftToolComponent(@NotNull final SplitPane pane) {
        super(pane);
        setId(CSSIds.GLOBAL_LEFT_TOOL_COMPONENT);
        setSide(Side.LEFT);
    }
}