package com.ss.editor.ui.component;

import com.ss.editor.ui.css.CSSClasses;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import rlib.ui.util.FXUtils;

/**
 * The component for containing tool components.
 *
 * @author JavaSaBr.
 */
public class TabToolComponent extends TabPane implements ScreenComponent {

    /**
     * The split pane.
     */
    private final SplitPane pane;

    /**
     * The index.
     */
    private final int index;

    private boolean collapsed;

    public TabToolComponent(final SplitPane pane, final int index) {
        this.pane = pane;
        this.index = index;
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::processMouseClick);
    }

    public void addComponent(final Region component, final String name) {

        final Tab tab = new Tab(name);
        tab.setContent(component);
        tab.setClosable(false);

        FXUtils.addClassTo(tab, CSSClasses.MAIN_FONT_12);

        getTabs().add(tab);

        FXUtils.bindFixedHeight(component, heightProperty());
    }

    /**
     * Handle a click to a tab.
     */
    private void processMouseClick(final MouseEvent event) {
        final EventTarget target = event.getTarget();
        if (!(target instanceof Node)) return;
        final Node node = (Node) target;
        if (!(node instanceof Text || node.getStyleClass().contains("tab-container"))) return;

        final double minWidth = getMinWidth();
        final double width = getWidth();

        if (width <= minWidth) {
            expand();
        } else {
            collapse();
        }
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Collapse selected tab.
     */
    private void collapse() {
        collapsed = true;
        switch (index) {
            case 0: {
                pane.setDividerPosition(0, 0);
                break;
            }
            case 1: {
                pane.setDividerPosition(0, 1);
                break;
            }
        }
    }

    /**
     * Expand selected tab.
     */
    private void expand() {
        collapsed = false;
        switch (index) {
            case 0: {
                pane.setDividerPosition(0, 0.2);
                break;
            }
            case 1: {
                pane.setDividerPosition(0, 0.8);
                break;
            }
        }
    }
}
