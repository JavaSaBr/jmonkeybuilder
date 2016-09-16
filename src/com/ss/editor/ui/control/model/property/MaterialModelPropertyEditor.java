package com.ss.editor.ui.control.model.property;

import com.jme3.asset.MaterialKey;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link MaterialKey}.
 *
 * @author JavaSaBr
 */
public class MaterialModelPropertyEditor<T extends Spatial> extends ModelPropertyControl<T, MaterialKey> {

    public static final String NO_MATERIAL = Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;
    public static final Insets BUTTON_OFFSET = new Insets(0, 0, 0, 3);

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    private static final Array<String> MATERIAL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MATERIAL_EXTENSIONS.add(FileExtensions.JME_MATERIAL);
    }

    /**
     * The label with name of the material.
     */
    private Label materialLabel;

    /**
     * The button for choosing other material.
     */
    private Button changeButton;

    /**
     * The button for editing the material.
     */
    private Button editButton;

    public MaterialModelPropertyEditor(@Nullable final MaterialKey element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        materialLabel = new Label(NO_MATERIAL);
        materialLabel.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_LABEL);

        changeButton = new Button();
        changeButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        changeButton.setGraphic(new ImageView(Icons.ADD_24));
        changeButton.setOnAction(event -> processChange());

        editButton = new Button();
        editButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        editButton.setGraphic(new ImageView(Icons.EDIT_16));
        editButton.disableProperty().bind(materialLabel.textProperty().isEqualTo(NO_MATERIAL));
        editButton.setOnAction(event -> processEdit());

        FXUtils.addToPane(materialLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(editButton, container);

        HBox.setMargin(changeButton, BUTTON_OFFSET);
        HBox.setMargin(editButton, BUTTON_OFFSET);

        FXUtils.addClassTo(changeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(editButton, CSSClasses.TOOLBAR_BUTTON);
    }

    /**
     * Show dialog for choosing another material.
     */
    private void processChange() {

        final EditorFXScene scene = EDITOR.getScene();

        final AssetEditorDialog dialog = new AssetEditorDialog(this::addMaterial);
        dialog.setExtensionFilter(MATERIAL_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    /**
     * Add the mew material.
     */
    private void addMaterial(@NotNull final Path file) {

        final Path assetFile = getAssetFile(file);
        final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));

        changed(materialKey, getPropertyValue());

        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Open this material in the material editor.
     */
    private void processEdit() {

        final MaterialKey element = getPropertyValue();
        if (element == null) return;

        final String assetPath = element.getName();
        if (StringUtils.isEmpty(assetPath)) return;

        final Path assetFile = Paths.get(assetPath);
        final Path realFile = EditorUtil.getRealFile(assetFile);
        if (!Files.exists(realFile)) return;

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(realFile);

        FX_EVENT_MANAGER.notify(event);
    }

    /**
     * @return the label with name of the material.
     */
    private Label getMaterialLabel() {
        return materialLabel;
    }

    @Override
    protected void reload() {
        final MaterialKey element = getPropertyValue();
        final Label materialLabel = getMaterialLabel();
        materialLabel.setText(element == null || StringUtils.isEmpty(element.getName()) ? NO_MATERIAL : element.getName());
    }
}
