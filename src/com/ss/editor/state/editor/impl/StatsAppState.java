package com.ss.editor.state.editor.impl;

import static java.util.Objects.requireNonNull;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Statistics;
import com.jme3.system.Timer;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorAppState;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.ArrayUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The app state to get and to show statistics of rendering.
 *
 * @author JavaSaBr
 */
public class StatsAppState extends AbstractAppState implements EditorAppState {

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final AtomicInteger STATISTICS_ENABLED = new AtomicInteger(0);

    /**
     * The parent node.
     */
    @NotNull
    private final Pane parent;

    /**
     * The stats parent.
     */
    @NotNull
    private final GridPane statsContainer;

    /**
     * The frame buffer M field.
     */
    @Nullable
    private Label frameBuffersMField;

    /**
     * The frame buffer F field.
     */
    @Nullable
    private Label frameBuffersFField;

    /**
     * The frame buffer S field.
     */
    @Nullable
    private Label frameBuffersSField;

    /**
     * The textures M field.
     */
    @Nullable
    private Label texturesMField;

    /**
     * The textures F field.
     */
    @Nullable
    private Label texturesFField;

    /**
     * The textures S field.
     */
    @Nullable
    private Label texturesSField;

    /**
     * The shaders M field.
     */
    @Nullable
    private Label shadersMField;

    /**
     * The shaders F field.
     */
    @Nullable
    private Label shadersFField;

    /**
     * The shaders S field.
     */
    @Nullable
    private Label shadersSField;

    /**
     * The objects field.
     */
    @Nullable
    private Label objectsField;

    /**
     * The uniforms field.
     */
    @Nullable
    private Label uniformsField;

    /**
     * The triangles field.
     */
    @Nullable
    private Label trianglesField;

    /**
     * The vertices S field.
     */
    @Nullable
    private Label verticesField;

    /**
     * The filed to show FPS.
     */
    @Nullable
    private Label fpsField;

    /**
     * The current application.
     */
    @Nullable
    private Application application;

    /**
     * The statistics.
     */
    @Nullable
    private Statistics statistics;

    /**
     * The container of stats data.
     */
    @Nullable
    private int[] statsData;

    /**
     * The container of prev stats data.
     */
    @Nullable
    private int[] prevStatsData;

    private float secondCounter;
    private int frameCounter;

    private int fps;
    private int prevFps;

    /**
     * Instantiates a new Stats app state.
     *
     * @param parent the parent
     */
    public StatsAppState(@NotNull final Pane parent) {
        this.parent = parent;
        this.statsContainer = new GridPane();
        this.frameCounter = 0;
        this.secondCounter = 0.0f;
        this.prevFps = -1;
        createComponents();
        setEnabled(false);
    }

    /**
     * Create stats fields.
     */
    private void createComponents() {

        final Label frameBuffersMLabel = new Label("FrameBuffers (M)");
        frameBuffersMField = new Label();

        final Label frameBuffersFLabel = new Label("FrameBuffers (F)");
        frameBuffersFField = new Label();

        final Label frameBuffersSLabel = new Label("FrameBuffers (S)");
        frameBuffersSField = new Label();

        final Label texturesMLabel = new Label("Textures (M)");
        texturesMField = new Label();

        final Label texturesFLabel = new Label("Textures (F)");
        texturesFField = new Label();

        final Label texturesSLabel = new Label("Textures (S)");
        texturesSField = new Label();

        final Label shadersMLabel = new Label("Shaders (M)");
        shadersMField = new Label();

        final Label shadersFLabel = new Label("Shaders (F)");
        shadersFField = new Label();

        final Label shadersSLabel = new Label("Shaders (S)");
        shadersSField = new Label();

        final Label objectsLabel = new Label("Objects");
        objectsField = new Label();

        final Label uniformsLabel = new Label("Uniforms");
        uniformsField = new Label();

        final Label trianglesLabel = new Label("Triangles");
        trianglesField = new Label();

        final Label verticesLabel = new Label("Vertices");
        verticesField = new Label();

        final Label fpsLabel = new Label("Fps");
        fpsField = new Label();

        statsContainer.setId(CSSIds.STATS_APP_STATE_STATS_CONTAINER);
        statsContainer.add(frameBuffersMLabel, 0, 0);
        statsContainer.add(frameBuffersMField, 1, 0);
        statsContainer.add(frameBuffersFLabel, 0, 1);
        statsContainer.add(frameBuffersFField, 1, 1);
        statsContainer.add(frameBuffersSLabel, 0, 2);
        statsContainer.add(frameBuffersSField, 1, 2);
        statsContainer.add(texturesMLabel, 0, 3);
        statsContainer.add(texturesMField, 1, 3);
        statsContainer.add(texturesFLabel, 0, 4);
        statsContainer.add(texturesFField, 1, 4);
        statsContainer.add(texturesSLabel, 0, 5);
        statsContainer.add(texturesSField, 1, 5);
        statsContainer.add(shadersMLabel, 0, 6);
        statsContainer.add(shadersMField, 1, 6);
        statsContainer.add(shadersFLabel, 0, 7);
        statsContainer.add(shadersFField, 1, 7);
        statsContainer.add(shadersSLabel, 0, 8);
        statsContainer.add(shadersSField, 1, 8);
        statsContainer.add(objectsLabel, 0, 9);
        statsContainer.add(objectsField, 1, 9);
        statsContainer.add(uniformsLabel, 0, 10);
        statsContainer.add(uniformsField, 1, 10);
        statsContainer.add(trianglesLabel, 0, 11);
        statsContainer.add(trianglesField, 1, 11);
        statsContainer.add(verticesLabel, 0, 12);
        statsContainer.add(verticesField, 1, 12);
        statsContainer.add(fpsLabel, 0, 13);
        statsContainer.add(fpsField, 1, 13);

        FXUtils.applyToChildren(statsContainer, node -> FXUtils.addClassTo(node, CSSClasses.SPECIAL_FONT_13));
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        this.application = application;
        this.statistics = application.getRenderer().getStatistics();
        this.statsData = new int[statistics.getLabels().length];
        this.prevStatsData = new int[statistics.getLabels().length];

        statistics.setEnabled(STATISTICS_ENABLED.incrementAndGet() > 0);

        EXECUTOR_MANAGER.addFXTask(() -> FXUtils.addToPane(statsContainer, parent));
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        statsContainer.setVisible(enabled);
    }

    /**
     * @return the current application.
     */
    @NotNull
    private Application getApplication() {
        return requireNonNull(application);
    }

    /**
     * @return the statistics.
     */
    @NotNull
    private Statistics getStatistics() {
        return requireNonNull(statistics);
    }

    /**
     * @return the container of stats data.
     */
    @NotNull
    private int[] getStatsData() {
        return requireNonNull(statsData);
    }

    /**
     * @return the container of prev stats data.
     */
    @Nullable
    private int[] getPrevStatsData() {
        return prevStatsData;
    }

    /**
     * @return the filed to show FPS.
     */
    @NotNull
    private Label getFpsField() {
        return requireNonNull(fpsField);
    }

    /**
     * @return the vertices S field.
     */
    @NotNull
    private Label getVerticesField() {
        return requireNonNull(verticesField);
    }

    /**
     * @return the triangles field.
     */
    @NotNull
    private Label getTrianglesField() {
        return requireNonNull(trianglesField);
    }

    /**
     * @return the uniforms field.
     */
    @NotNull
    private Label getUniformsField() {
        return requireNonNull(uniformsField);
    }

    /**
     * @return the objects field.
     */
    @NotNull
    private Label getObjectsField() {
        return requireNonNull(objectsField);
    }

    /**
     * @return the shaders F field.
     */
    @NotNull
    private Label getShadersFField() {
        return requireNonNull(shadersFField);
    }

    /**
     * @return the shaders M field.
     */
    @NotNull
    private Label getShadersMField() {
        return requireNonNull(shadersMField);
    }

    /**
     * @return the shaders S field.
     */
    @NotNull
    private Label getShadersSField() {
        return requireNonNull(shadersSField);
    }

    /**
     * @return the textures F field.
     */
    @NotNull
    private Label getTexturesFField() {
        return requireNonNull(texturesFField);
    }

    /**
     * @return the textures M field.
     */
    @NotNull
    private Label getTexturesMField() {
        return requireNonNull(texturesMField);
    }

    /**
     * @return the textures S field.
     */
    @NotNull
    private Label getTexturesSField() {
        return requireNonNull(texturesSField);
    }

    /**
     * @return the frame buffer F field.
     */
    @NotNull
    private Label getFrameBuffersFField() {
        return requireNonNull(frameBuffersFField);
    }

    /**
     * @return the frame buffer M field.
     */
    @NotNull
    private Label getFrameBuffersMField() {
        return requireNonNull(frameBuffersMField);
    }

    /**
     * @return the frame buffer S field.
     */
    @NotNull
    private Label getFrameBuffersSField() {
        return requireNonNull(frameBuffersSField);
    }

    @Override
    public void update(final float tpf) {
        if (!isEnabled()) return;

        final Application application = getApplication();
        final Timer timer = application.getTimer();

        secondCounter += timer.getTimePerFrame();
        frameCounter++;

        if (secondCounter >= 1.0f) {
            fps = (int) (frameCounter / secondCounter);
            if (fps != prevFps) {
                secondCounter = 0.0f;
                frameCounter = 0;
                updateFps(fps);
                prevFps = fps;
            }
        }

        final int[] statsData = getStatsData();

        final Statistics statistics = getStatistics();
        statistics.getData(statsData);

        final int[] prevStatsData = getPrevStatsData();

        if (Arrays.equals(statsData, prevStatsData)) {
            return;
        }

        ArrayUtils.copyTo(statsData, prevStatsData);

        final int vertices = statsData[0];
        final int triangles = statsData[1];
        final int uniforms = statsData[2];
        final int objects = statsData[3];
        final int shadersS = statsData[4];
        final int shadersF = statsData[5];
        final int shadersM = statsData[6];
        final int texturesS = statsData[7];
        final int texturesF = statsData[8];
        final int texturesM = statsData[9];
        final int frameBuffersS = statsData[10];
        final int frameBuffersF = statsData[11];
        final int frameBuffersM = statsData[12];

        EXECUTOR_MANAGER.addFXTask(() -> {
            getVerticesField().setText(Integer.toString(vertices));
            getTrianglesField().setText(Integer.toString(triangles));
            getUniformsField().setText(Integer.toString(uniforms));
            getObjectsField().setText(Integer.toString(objects));
            getShadersSField().setText(Integer.toString(shadersS));
            getShadersFField().setText(Integer.toString(shadersF));
            getShadersMField().setText(Integer.toString(shadersM));
            getTexturesSField().setText(Integer.toString(texturesS));
            getTexturesFField().setText(Integer.toString(texturesF));
            getTexturesMField().setText(Integer.toString(texturesM));
            getFrameBuffersSField().setText(Integer.toString(frameBuffersS));
            getFrameBuffersFField().setText(Integer.toString(frameBuffersF));
            getFrameBuffersMField().setText(Integer.toString(frameBuffersM));
        });
    }

    /**
     * Update the FPS value.
     */
    private void updateFps(final int fps) {
        EXECUTOR_MANAGER.addFXTask(() -> getFpsField().setText(Integer.toString(fps)));
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Statistics statistics = getStatistics();
        statistics.setEnabled(STATISTICS_ENABLED.decrementAndGet() == 0);

        EXECUTOR_MANAGER.addFXTask(() -> FXUtils.removeFromParent(statsContainer, parent));
    }
}
