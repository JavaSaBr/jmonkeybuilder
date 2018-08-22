package com.ss.builder.fx.component.tab;

import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
import javafx.geometry.Side;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;

/**
 * The component to contain editor tool components.
 *
 * @author JavaSaBr
 */
public class EditorToolComponent extends TabToolComponent {

    /**
     * Instantiates a new Editor tool component.
     *
     * @param pane  the pane
     * @param index the index
     */
    public EditorToolComponent(@NotNull final SplitPane pane, final int index) {
        super(pane);
        setSide(Side.RIGHT);
        FXUtils.addClassTo(this, CssClasses.FILE_EDITOR_TOOL_COMPONENT);
    }

    @Override
    protected int getCollapsePosition() {
        return 1;
    }
}
