package com.ss.editor.ui.component.tab;

import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.geometry.Side;
import javafx.scene.control.SplitPane;

/**
 * The component to contain editor tool components.
 *
 * @author JavaSaBr
 */
public class EditorToolComponent extends TabToolComponent {

    public EditorToolComponent(@NotNull final SplitPane pane, final int index) {
        super(pane);
        setId(CSSIds.FILE_EDITOR_TOOL_COMPONENT);
        setSide(Side.RIGHT);
    }

    @Override
    protected int getCollapsePosition() {
        return 1;
    }
}
