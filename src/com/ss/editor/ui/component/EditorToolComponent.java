package com.ss.editor.ui.component;

import com.ss.editor.ui.css.CSSIds;

import javafx.geometry.Side;
import javafx.scene.control.SplitPane;

/**
 * The component for containing editor tool components.
 *
 * @author JavaSaBr.
 */
public class EditorToolComponent extends TabToolComponent {

    public EditorToolComponent(final SplitPane pane, final int index) {
        super(pane, index);
        setId(CSSIds.FILE_EDITOR_TOOL_COMPONENT);
        setSide(Side.RIGHT);
    }
}
