package com.ss.editor.ui.control.choose;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The named control to choose textures.
 *
 * @author JavaSaBr
 */
public class NamedChooseTextureControl extends ChooseTextureControl {

    private static final double LABEL_PERCENT = 1D - AbstractPropertyControl.CONTROL_WIDTH_PERCENT;
    private static final double FIELD_PERCENT = AbstractPropertyControl.CONTROL_WIDTH_PERCENT;

    /**
     * The name of this control.
     */
    @Nullable
    private final String name;

    /**
     * The label with name.
     */
    @Nullable
    private Label nameLabel;

    /**
     * Instantiates a new Named choose texture control.
     *
     * @param name the name
     */
    public NamedChooseTextureControl(@NotNull final String name) {
        this.name = name + ":";
    }

    @Override
    protected void createComponents() {

        nameLabel = new Label();
        nameLabel.maxWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));
        nameLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        FXUtils.addToPane(nameLabel, this);

        super.createComponents();

        final HBox wrapper = getWrapper();
        wrapper.maxWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        wrapper.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
    }

    /**
     * Sets a width percent of a control.
     *
     * @param percent the percent.
     */
    public void setControlWidthPercent(final double percent) {

        final Label nameLabel = getNameLabel();
        nameLabel.maxWidthProperty().bind(widthProperty().multiply(1D - percent));
        nameLabel.prefWidthProperty().bind(widthProperty().multiply(1D - percent));

        final HBox wrapper = getWrapper();
        wrapper.maxWidthProperty().bind(widthProperty().multiply(percent));
        wrapper.prefWidthProperty().bind(widthProperty().multiply(percent));
    }

    /**
     * @return the label with name.
     */
    @NotNull
    private Label getNameLabel() {
        return notNull(nameLabel);
    }

    @Override
    protected void reload() {
        super.reload();

        final Label nameLabel = getNameLabel();
        nameLabel.setText(getName());
    }

    /**
     * @return the name of this control.
     */
    @NotNull
    private String getName() {
        return name == null ? StringUtils.EMPTY : name;
    }
}
