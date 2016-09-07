package com.ss.editor.ui.component.editor.impl.particle.emitter;

import com.jme3.material.Material;
import com.ss.editor.file.PostFilterViewFile;
import com.ss.editor.serializer.PostFilterViewSerializer;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.css.CSSIds;

import java.nio.file.Files;
import java.nio.file.Path;

import emitter.Emitter;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.Util;

import static com.ss.editor.FileExtensions.PARTICLE_EMITTER_VIEW;

/**
 * The implementation of the {@link AbstractFileEditor} for working with particle emitters.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterEditor extends AbstractFileEditor<StackPane>  {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName("Particle Editor");
        DESCRIPTION.setConstructor(ParticleEmitterEditor::new);
        DESCRIPTION.setEditorId(ParticleEmitterEditor.class.getName());
        DESCRIPTION.addExtension(PARTICLE_EMITTER_VIEW);
    }

    /**
     * Список используемых матералов.
     */
    private ListView<Material> emittersView;

    /**
     * The ignore listeners flag.
     */
    private boolean ignoreListeners;

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final PostFilterViewFile currentFile = PostFilterViewSerializer.deserialize(file);
        final byte[] content = Util.safeGet(file, Files::readAllBytes);

        //setOriginalContent(new String(content));
        //setCurrentFile(currentFile);
        setIgnoreListeners(true);
        try {
            //final List<String> materials = currentFile.getMaterials();
            //materials.forEach(assetName -> addRelativeMaterial(Paths.get(assetName)));
        } finally {
            setIgnoreListeners(false);
        }
    }

    private void setIgnoreListeners(boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(final HBox container) {
        FXUtils.addToPane(createSaveAction(), container);
    }

    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void createContent(final StackPane root) {
        root.setAlignment(Pos.TOP_RIGHT);

        final Accordion accordion = new Accordion();

        final VBox parameterContainer = new VBox();
        parameterContainer.setId(CSSIds.MODEL_FILE_EDITOR_PARAMETER_CONTAINER);

        emittersView = new ListView<>();
        //emittersView.setCellFactory(param -> new MaterialListCell(this));
        //emittersView.setOnDragOver(this::dragOver);
        //emittersView.setOnDragDropped(this::dragDropped);
        emittersView.setMinHeight(24);

        final ObservableList<TitledPane> panes = accordion.getPanes();
        //panes.add(emittersView);

        FXUtils.addToPane(accordion, parameterContainer);
        FXUtils.addToPane(parameterContainer, root);

       // accordion.setExpandedPane(modelNodeTree);

        FXUtils.bindFixedHeight(accordion, parameterContainer.heightProperty());
    }

    @Override
    public EditorDescription getDescription() {
        return null;
    }

    public void remove(final Emitter emitter) {

    }
}
