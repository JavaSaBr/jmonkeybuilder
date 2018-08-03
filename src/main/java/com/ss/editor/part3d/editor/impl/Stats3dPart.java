package com.ss.editor.part3d.editor.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Statistics;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The 3D state to get and to show statistics of rendering.
 *
 * @author JavaSaBr
 */
public class Stats3dPart extends AbstractEditor3dPart<FileEditor> {

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

    public Stats3dPart(@NotNull FileEditor fileEditor, @NotNull Pane parent) {
        super(fileEditor);
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
    @FxThread
    private void createComponents() {

        var frameBuffersMLabel = new Label("FrameBuffers (M)");
        frameBuffersMField = new Label();

        var frameBuffersFLabel = new Label("FrameBuffers (F)");
        frameBuffersFField = new Label();

        var frameBuffersSLabel = new Label("FrameBuffers (S)");
        frameBuffersSField = new Label();

        var texturesMLabel = new Label("Textures (M)");
        texturesMField = new Label();

        var texturesFLabel = new Label("Textures (F)");
        texturesFField = new Label();

        var texturesSLabel = new Label("Textures (S)");
        texturesSField = new Label();

        var shadersMLabel = new Label("Shaders (M)");
        shadersMField = new Label();

        var shadersFLabel = new Label("Shaders (F)");
        shadersFField = new Label();

        var shadersSLabel = new Label("Shaders (S)");
        shadersSField = new Label();

        var objectsLabel = new Label("Objects");
        objectsField = new Label();

        var uniformsLabel = new Label("Uniforms");
        uniformsField = new Label();

        var trianglesLabel = new Label("Triangles");
        trianglesField = new Label();

        var verticesLabel = new Label("Vertices");
        verticesField = new Label();

        var fpsLabel = new Label("Fps");
        fpsField = new Label();

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

        FxUtils.addClass(statsContainer, CssClasses.STATS_3D_STATE);
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);

        this.application = application;
        this.statistics = application.getRenderer().getStatistics();
        this.statsData = new int[statistics.getLabels().length];
        this.prevStatsData = new int[statistics.getLabels().length];

        statistics.setEnabled(STATISTICS_ENABLED.incrementAndGet() > 0);

        ExecutorManager.getInstance()
                .addFxTask(() -> FxUtils.addChild(parent, statsContainer));
    }

    @Override
    @JmeThread
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        ExecutorManager.getInstance()
                .addFxTask(() -> statsContainer.setVisible(enabled));
    }

    /**
     * Get the current application.
     *
     * @return the current application.
     */
    @FromAnyThread
    private @NotNull Application getApplication() {
        return notNull(application);
    }

    /**
     * Get the statistics.
     *
     * @return the statistics.
     */
    @FromAnyThread
    private @NotNull Statistics getStatistics() {
        return notNull(statistics);
    }

    /**
     * Get the container of stats data.
     *
     * @return the container of stats data.
     */
    @FromAnyThread
    private @NotNull int[] getStatsData() {
        return notNull(statsData);
    }

    /**
     * Get the container of prev stats data.
     *
     * @return the container of prev stats data.
     */
    @FromAnyThread
    private @Nullable int[] getPrevStatsData() {
        return prevStatsData;
    }

    /**
     * Get the filed to show FPS.
     *
     * @return the filed to show FPS.
     */
    @FxThread
    private @NotNull Label getFpsField() {
        return notNull(fpsField);
    }

    /**
     * Get the vertices S field.
     *
     * @return the vertices S field.
     */
    @FxThread
    private @NotNull Label getVerticesField() {
        return notNull(verticesField);
    }

    /**
     * Get the triangles field.
     *
     * @return the triangles field.
     */
    @FxThread
    private @NotNull Label getTrianglesField() {
        return notNull(trianglesField);
    }

    /**
     * Get the uniforms field.
     *
     * @return the uniforms field.
     */
    @FxThread
    private @NotNull Label getUniformsField() {
        return notNull(uniformsField);
    }

    /**
     * Get the objects field.
     *
     * @return the objects field.
     */
    @FxThread
    private @NotNull Label getObjectsField() {
        return notNull(objectsField);
    }

    /**
     * Get the shaders F field.
     *
     * @return the shaders F field.
     */
    @FxThread
    private @NotNull Label getShadersFField() {
        return notNull(shadersFField);
    }

    /**
     * Get the shaders M field.
     *
     * @return the shaders M field.
     */
    @FxThread
    private @NotNull Label getShadersMField() {
        return notNull(shadersMField);
    }

    /**
     * Get the shaders S field.
     *
     * @return the shaders S field.
     */
    @FxThread
    private @NotNull Label getShadersSField() {
        return notNull(shadersSField);
    }

    /**
     * Get the textures F field.
     *
     * @return the textures F field.
     */
    @FxThread
    private @NotNull Label getTexturesFField() {
        return notNull(texturesFField);
    }

    /**
     * Get the textures M field.
     *
     * @return the textures M field.
     */
    @FxThread
    private @NotNull Label getTexturesMField() {
        return notNull(texturesMField);
    }

    /**
     * Get the textures S field.
     *
     * @return the textures S field.
     */
    @FxThread
    private @NotNull Label getTexturesSField() {
        return notNull(texturesSField);
    }

    /**
     * Get the frame buffer F field.
     *
     * @return the frame buffer F field.
     */
    @FxThread
    private @NotNull Label getFrameBuffersFField() {
        return notNull(frameBuffersFField);
    }

    /**
     * Get the frame buffer M field.
     *
     * @return the frame buffer M field.
     */
    @FxThread
    private @NotNull Label getFrameBuffersMField() {
        return notNull(frameBuffersMField);
    }

    /**
     * Get the frame buffer S field.
     *
     * @return the frame buffer S field.
     */
    @FxThread
    private @NotNull Label getFrameBuffersSField() {
        return notNull(frameBuffersSField);
    }

    @Override
    @JmeThread
    public void update(float tpf) {

        if (!isEnabled()) {
            return;
        }

        var application = getApplication();
        var timer = application.getTimer();

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

        var statsData = getStatsData();

        var statistics = getStatistics();
        statistics.getData(statsData);

        var prevStatsData = getPrevStatsData();

        if (Arrays.equals(statsData, prevStatsData)) {
            return;
        }

        ArrayUtils.copyTo(statsData, prevStatsData);

        var vertices = statsData[0];
        var triangles = statsData[1];
        var uniforms = statsData[2];
        var objects = statsData[3];
        var shadersS = statsData[4];
        var shadersF = statsData[5];
        var shadersM = statsData[6];
        var texturesS = statsData[7];
        var texturesF = statsData[8];
        var texturesM = statsData[9];
        var frameBuffersS = statsData[10];
        var frameBuffersF = statsData[11];
        var frameBuffersM = statsData[12];

        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {
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
    @JmeThread
    private void updateFps(int fps) {
        ExecutorManager.getInstance()
                .addFxTask(() -> getFpsField().setText(Integer.toString(fps)));
    }

    @Override
    @JmeThread
    public void cleanup() {
        super.cleanup();

        getStatistics().setEnabled(STATISTICS_ENABLED.decrementAndGet() == 0);

        ExecutorManager.getInstance()
                .addFxTask(() -> FxUtils.removeChild(parent, statsContainer));
    }
}
