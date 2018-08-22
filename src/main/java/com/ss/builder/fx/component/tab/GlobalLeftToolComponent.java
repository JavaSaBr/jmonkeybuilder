package com.ss.builder.fx.component.tab;

import com.ss.builder.fx.css.CssIds;
import com.ss.builder.fx.css.CssIds;

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
        setId(CssIds.GLOBAL_LEFT_TOOL_COMPONENT);
        setSide(Side.LEFT);
    }
}