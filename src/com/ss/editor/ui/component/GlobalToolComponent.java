package com.ss.editor.ui.component;

import com.ss.editor.ui.css.CSSIds;

import javafx.geometry.Side;
import javafx.scene.control.SplitPane;

/**
 * The component for containing global tool components.
 *
 * @author JavaSaBr.
 */
public class GlobalToolComponent extends TabToolComponent {

    public GlobalToolComponent(final SplitPane pane, final int index) {
        super(pane, index);
        setId(CSSIds.GLOBAL_TOOL_COMPONENT);
        setSide(Side.LEFT);
    }
}
