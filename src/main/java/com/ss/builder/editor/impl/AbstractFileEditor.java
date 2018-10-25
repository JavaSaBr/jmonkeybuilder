package com.ss.builder.editor.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import com.ss.builder.analytics.google.GAEvent;
import com.ss.builder.analytics.google.GAnalytics;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.editor.FileEditor;
import com.ss.builder.editor.event.*;
import com.ss.builder.editor.impl.control.EditorControl;
import com.ss.builder.editor.impl.control.impl.FxInputEditorControl;
import com.ss.builder.fx.editor.layout.EditorLayout;
import com.ss.builder.fx.editor.part.ui.EditorUiPart;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.FileChangedEvent;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.jme.editor.part3d.Editor3dPart;
import com.ss.builder.jme.editor.part3d.ExtendableEditor3dPart;
import com.ss.builder.jme.editor.part3d.event.Editor3dPartEvent;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The base implementation of a file editor.
 *
 * @param <L> the editor layout's type.
 * @author JavaSaBr
 */
public abstract class AbstractFileEditor<L extends EditorLayout> implements FileEditor {

    protected static final Logger LOGGER = LoggerManager.getLogger(FileEditor.class);

    /**
     * The executor manager.
     */
    @NotNull
    protected final ExecutorManager executorManager;

    /**
     * The FX event manager.
     */
    @NotNull
    protected final FxEventManager fxEventManager;

    /**
     * The array of editor's 3d parts.
     */
    @NotNull
    private final Array<Editor3dPart> editor3dParts;

    /**
     * The array of editor's Ui parts.
     */
    @NotNull
    private final Array<EditorUiPart> editorUiParts;

    /**
     * The array of file editor controls.
     */
    @NotNull
    private final Array<EditorControl> controls;

    /**
     * The file changes listener.
     */
    @NotNull
    private final EventHandler<FileChangedEvent> fileChangedHandler;

    /**
     * The dirty property.
     */
    @NotNull
    private final BooleanProperty dirtyProperty;

    /**
     * The flat about that this editor was initialized.
     */
    @NotNull
    private final AtomicBoolean initialized;

    /**
     * The editor's layout.
     */
    @NotNull
    private final L layout;

    /**
     * The time when this editor was showed.
     */
    @NotNull
    private volatile LocalTime showedTime;

    /**
     * The save callback.
     */
    @Nullable
    private volatile CompletableFuture<FileEditor> saveCallback;

    /**
     * The editing file.
     */
    @Nullable
    private volatile Path file;

    /**
     * True if the saving process is running now.
     */
    private volatile boolean saving;

    protected AbstractFileEditor() {
        this.executorManager = ExecutorManager.getInstance();
        this.fxEventManager = FxEventManager.getInstance();
        this.showedTime = LocalTime.now();
        this.editor3dParts = Array.ofType(Editor3dPart.class);
        this.editorUiParts = Array.ofType(EditorUiPart.class);
        this.controls = Array.ofType(EditorControl.class);
        this.dirtyProperty = new SimpleBooleanProperty(this, "dirty", false);
        this.initialized = new AtomicBoolean();
        this.fileChangedHandler = this::handleChangedFile;
        this.layout = createLayout();

        if (needListenEventsFromPage()) {
            addEditorControl(new FxInputEditorControl(this));
        }
    }

    /**
     * Add the new editor's 3D part.
     *
     * @param editor3dPart the editor's 3D part.
     */
    @FromAnyThread
    protected void addEditor3dPart(@NotNull Editor3dPart editor3dPart) {
        this.editor3dParts.add(editor3dPart);
    }

    /**
     * Add the new editor's Ui part.
     *
     * @param editorUiPart the editor's Ui part.
     */
    @FromAnyThread
    protected void addEditorUiPart(@NotNull EditorUiPart editorUiPart) {
        this.editorUiParts.add(editorUiPart);
    }

    /**
     * Add the new editor's control.
     *
     * @param control the editor's control.
     */
    @FromAnyThread
    protected void addEditorControl(@NotNull EditorControl control) {
        this.controls.add(control);
    }

    /**
     * Set the editing file.
     *
     * @param file the editing file.
     */
    @FxThread
    protected void setFile(@NotNull Path file) {
        this.file = file;
    }

    @FxThread
    protected void initialize() {
        buildUi(layout);
        controls.forEach(EditorControl::initialize);
    }

    /**
     * Create editor's Ui.
     *
     * @param layout the editor's layout.
     */
    @FromAnyThread
    protected void buildUi(@NotNull L layout) {
        editorUiParts.forEach(layout, EditorUiPart::buildUi);
    }

    /**
     * Return true if need to listen to events from root page of this editor.
     *
     * @return true if need to listen to events from root page of this editor.
     */
    protected boolean needListenEventsFromPage() {
        return true;
    }

    @Override
    @FromAnyThread
    public @NotNull CompletableFuture<FileEditor> save() {
        var result = new CompletableFuture<FileEditor>();
        executorManager.addFxTask(() -> checkAndSaveIfNeed(result));
        return result;
    }

    /**
     * Check and if need to save the current state of this editor.
     *
     * @param callback the callback.
     */
    @FxThread
    protected void checkAndSaveIfNeed(@NotNull CompletableFuture<FileEditor> callback) {

        if (isSaving() || !isDirty()) {
            callback.complete(this);
            return;
        }

        this.saveCallback = callback;

        notifyStartSaving();

        executorManager.addBackgroundTask(this::saveInBackground);
    }

    @BackgroundThread
    protected void saveInBackground() {

        var editorId = getDescriptor().getEditorId();
        var tempFile = FileUtils.createTempFile(editorId, "toSave.tmp");

        var stamp = EditorUtils.renderLock();
        try {

            doSave(tempFile);

            try (var out = Files.newOutputStream(getFile(), TRUNCATE_EXISTING)) {
                Files.copy(tempFile, out);
            } finally {
                FileUtils.delete(tempFile);
            }

        } catch (Throwable e) {
            EditorUtils.handleException(LOGGER, this, e);
            executorManager.addFxTask(this::notifyFinishSaving);
        } finally {
            EditorUtils.renderUnlock(stamp);
        }

        executorManager.addFxTask(this::postSave);
    }

    /**
     * Save new changes.
     *
     * @param toStore the file to store.
     * @throws IOException if was some problem during writing.
     */
    @BackgroundThread
    protected void doSave(@NotNull Path toStore) throws Throwable {
    }

    /**
     * Do some actions after saving.
     */
    @FxThread
    protected void postSave() {
        setDirty(false);
    }

    /**
     * Create an editor's layout.
     *
     * @return the editor's layout.
     */
    @BackgroundThread
    protected abstract @NotNull L createLayout();

    @Override
    @FxThread
    public @NotNull Pane getUiPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    @FxThread
    public @NotNull EditorLayout getLayout() {
        return layout;
    }

    @Override
    @FxThread
    public @NotNull Path getFile() {
        return notNull(file);
    }

    @Override
    @FxThread
    public @NotNull String getFileName() {
        return getFile().getFileName()
                .toString();
    }

    @Override
    @BackgroundThread
    public void openFile(@NotNull Path file) {

        if (initialized.compareAndSet(false, true)) {
            initialize();
        }

        fxEventManager.addEventHandler(FileChangedEvent.EVENT_TYPE, fileChangedHandler);

        this.file = file;
        this.showedTime = LocalTime.now();

        var description = getDescriptor();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_OPENED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendPageView(description.getEditorId(), null, "/editing/" + description.getEditorId());
    }

    @Override
    @FxThread
    public @NotNull BooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

    @Override
    @FromAnyThread
    public boolean isDirty() {
        synchronized (dirtyProperty) {
            return dirtyProperty.get();
        }
    }

    /**
     * Set the flag of dirty of this editor.
     *
     * @param dirty the dirty
     */
    @FromAnyThread
    protected void setDirty(boolean dirty) {
        synchronized (dirtyProperty) {
            dirtyProperty.setValue(dirty);
        }
    }

    @Override
    @FxThread
    public @NotNull Array<Editor3dPart> get3dParts() {
        return editor3dParts;
    }

    @Override
    @FxThread
    public @NotNull Array<EditorUiPart> getUiParts() {
        return editorUiParts;
    }

    @Override
    @FromAnyThread
    public void notify(@NotNull FileEditorEvent event) {

        if (Platform.isFxApplicationThread()) {
            notifyImpl(event);
        } else {
            executorManager.addFxTask(() -> notifyImpl(event));
        }

        if (event instanceof Editor3dPartEvent) {

            Editor3dPartEvent editor3dPartEvent = (Editor3dPartEvent) event;

            editorUiParts.stream()
                    .filter(ExtendableEditor3dPart.class::isInstance)
                    .map(ExtendableEditor3dPart.class::cast)
                    .forEach(part -> part.notify(editor3dPartEvent));
        }
    }

    @JmeThread
    protected void notifyImpl(@NotNull FileEditorEvent event) {

        if (event instanceof ShowedFileEditorEvent) {
            notifyShowed();
        } else if (event instanceof HideFileEditorEvent) {
            notifyHide();
        } else if (event instanceof ClosedFileEditorEvent) {
            notifyClosed();
        } else if (event instanceof FileMovedFileEditorEvent) {
            var movedEvent = (FileMovedFileEditorEvent) event;
            notifyFileMoved(movedEvent.getPrevFile(), movedEvent.getNewFile());
        } else if (event instanceof FileRenamedFileEditorEvent) {
            var movedEvent = (FileRenamedFileEditorEvent) event;
            notifyFileRenamed(movedEvent.getPrevFile(), movedEvent.getNewFile());
        }
    }

    @Override
    @FromAnyThread
    public void notify(@NotNull Editor3dPartEvent event) {

    }

    /**
     * Notify about renamed files.
     *
     * @param prevFile the prev file
     * @param newFile  the new file
     */
    @FxThread
    protected void notifyFileRenamed(@NotNull Path prevFile, @NotNull Path newFile) {
        notifyFilePathChanged(prevFile, newFile);
    }

    /**
     * Notify about moved file.
     *
     * @param prevFile the prev file
     * @param newFile  the new file
     */
    @FxThread
    protected void notifyFileMoved(@NotNull Path prevFile, @NotNull Path newFile) {
        notifyFilePathChanged(prevFile, newFile);
    }

    /**
     * Notify about changed the editing file.
     *
     * @param prevFile the previous file.
     * @param newFile  the new file.
     */
    @FxThread
    private void notifyFilePathChanged(@NotNull Path prevFile, @NotNull Path newFile) {

        var currentFile = getFile();

        if (currentFile.equals(prevFile)) {
            setFile(newFile);
            return;
        }

        if (!currentFile.startsWith(prevFile)) {
            return;
        }

        var relativeFile = currentFile.subpath(prevFile.getNameCount(), currentFile.getNameCount());
        var resultFile = newFile.resolve(relativeFile);

        setFile(resultFile);
    }

    /**
     * Notify that this editor was showed.
     */
    @FxThread
    protected void notifyShowed() {
        this.showedTime = LocalTime.now();

        var description = getDescriptor();

        GAnalytics.sendPageView(description.getEditorId(), null,
                "/editing/" + description.getEditorId());
    }

    /**
     * Notify that this editor was hide.
     */
    @FxThread
    protected void notifyHide() {

        var duration = Duration.between(showedTime, LocalTime.now());
        var seconds = (int) duration.getSeconds();

        var description = getDescriptor();

        GAnalytics.sendTiming(GAEvent.Category.EDITOR, GAEvent.Label.WORKING_ON_AN_EDITOR,
                seconds, description.getEditorId());
    }

    /**
     * Notify that this editor was closed.
     */
    @FxThread
    protected void notifyClosed() {

        fxEventManager.removeEventHandler(FileChangedEvent.EVENT_TYPE, fileChangedHandler);

        var duration = Duration.between(showedTime, LocalTime.now());
        var seconds = (int) duration.getSeconds();

        var description = getDescriptor();

        GAnalytics.sendEvent(GAEvent.Category.EDITOR, GAEvent.Action.EDITOR_CLOSED,
                description.getEditorId() + "/" + getFileName());

        GAnalytics.sendTiming(GAEvent.Category.EDITOR, GAEvent.Label.WORKING_ON_AN_EDITOR,
                seconds, description.getEditorId());
    }

    /**
     * Handle an event about some changed file.
     *
     * @param event the file changed event.
     */
    @FxThread
    protected void handleChangedFile(@NotNull FileChangedEvent event) {

        var file = event.getFile();
        var openedFile = getFile();

        if (!file.equals(openedFile)) {
            return;
        }

        handleChangedFileImpl(event);

        if (isSaving()) {
            notifyFinishSaving();
            return;
        }

        handleExternalChanges();
    }

    /**
     * Handle an event about that the opened file was changed.
     *
     * @param event the file changed event.
     */
    @FxThread
    protected void handleChangedFileImpl(@NotNull FileChangedEvent event) {
    }

    /**
     * Handle external changes of the opened file.
     */
    @FxThread
    protected void handleExternalChanges() {

    }

    /**
     * Return true if saving process is running now.
     *
     * @return true if saving process is running now.
     */
    @FxThread
    protected boolean isSaving() {
        return saving;
    }

    /**
     * Set true if saving process is running now.
     *
     * @param saving true if saving process is running now.
     */
    @FxThread
    protected void setSaving(boolean saving) {
        this.saving = saving;
    }

    /**
     * Notify start saving.
     */
    @FxThread
    protected void notifyStartSaving() {
        UiUtils.incrementLoading();
        setSaving(true);
    }

    /**
     * Notify finish saving.
     */
    @FxThread
    protected void notifyFinishSaving() {
        setSaving(false);

        UiUtils.decrementLoading();

        if (saveCallback != null) {
            saveCallback.complete(this);
            saveCallback = null;
        }
    }

    @Override
    public String toString() {
        return "AbstractFileEditor{" +
                "dirtyProperty=" + dirtyProperty.get() +
                ", file=" + file +
                '}';
    }

}
