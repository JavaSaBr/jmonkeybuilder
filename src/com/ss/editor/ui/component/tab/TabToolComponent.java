package com.ss.editor.ui.component.tab;

import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.css.CSSClasses;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @NotNull
    protected final SplitPane pane;

    /**
     * Is collapsed.
     */
    @NotNull
    private final BooleanProperty collapsed;

    /**
     * The last expand position.
     */
    private double expandPosition;

    /**
     * Is changing tab.
     */
    private boolean changingTab;

    TabToolComponent(@NotNull final SplitPane pane) {
        this.collapsed = new SimpleBooleanProperty(this, "collapsed", false);
        this.collapsed.bind(widthProperty().lessThanOrEqualTo(minWidthProperty()));
        this.pane = pane;
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::processMouseClick);
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changedTab(oldValue));
    }

    /**
     * Add a change tab index listener.
     *
     * @param listener the listener.
     */
    public void addChangeListener(@NotNull final ChangeListener<? super Number> listener) {
        getSelectionModel().selectedIndexProperty().addListener(listener);
    }

    /**
     * @param expandPosition the last expand position.
     */
    public void setExpandPosition(final double expandPosition) {
        this.expandPosition = expandPosition;
    }

    /**
     * @return the last expand position.
     */
    private double getExpandPosition() {
        return expandPosition;
    }

    /**
     * @param changingTab true if now is changing tab.
     */
    private void setChangingTab(final boolean changingTab) {
        this.changingTab = changingTab;
    }

    /**
     * @return true if now is changing tab.
     */
    private boolean isChangingTab() {
        return changingTab;
    }

    /**
     * Handle a changed tab.
     */
    private void changedTab(@Nullable final Tab oldValue) {
        if (isCollapsed()) expand();
        setChangingTab(oldValue != null);
    }

    /**
     * Add a new component to this tool container.
     */
    public void addComponent(@NotNull final Region component, @NotNull final String name) {

        final Tab tab = new Tab(name);
        tab.setContent(component);
        tab.setClosable(false);

        FXUtils.addClassTo(tab, CSSClasses.SPECIAL_FONT_14);

        getTabs().add(tab);

        FXUtils.bindFixedHeight(component, heightProperty());
    }

    /**
     * Handle a click to a tab.
     */
    private void processMouseClick(@NotNull final MouseEvent event) {
        final EventTarget target = event.getTarget();
        if (!(target instanceof Node)) return;
        final Node node = (Node) target;
        if (!(node instanceof Text || node.getStyleClass().contains("tab-container"))) return;

        if (isChangingTab()) {
            setChangingTab(false);
            return;
        }

        final double minWidth = getMinWidth();
        final double width = getWidth();

        if (width <= minWidth) {
            expand();
        } else {
            collapse();
        }
    }

    /**
     * @return true if this is collapsed.
     */
    public boolean isCollapsed() {
        return collapsed.get();
    }

    /**
     * Collapse selected tab.
     */
    public void collapse() {
        pane.setDividerPosition(getDividerIndex(), getCollapsePosition());
    }

    protected int getCollapsePosition() {
        return 0;
    }

    /**
     * Expand selected tab.
     */
    private void expand() {

        final int dividerIndex = getDividerIndex();
        final double position = getExpandPosition();

        expand(dividerIndex, position);
    }

    private int getDividerIndex() {
        return 0;
    }

    /**
     * Expand selected tab.
     */
    private void expand(final int dividerIndex, final double position) {
        pane.setDividerPosition(dividerIndex, position);
    }
}
