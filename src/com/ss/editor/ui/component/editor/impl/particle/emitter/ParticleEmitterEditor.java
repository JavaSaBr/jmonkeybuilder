package com.ss.editor.ui.component.editor.impl.particle.emitter;

import com.ss.editor.file.PostFilterViewFile;
import com.ss.editor.serializer.PostFilterViewSerializer;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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

    }

    @Override
    public EditorDescription getDescription() {
        return null;
    }
}
