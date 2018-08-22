package com.ss.builder.editor.part3d.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Statistics;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.ui.css.CssClasses;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
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
public class Stats3dPart extends AbstractEditor3dPart {

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
    @NotNull
    private final Label frameBuffersMField;

    /**
     * The frame buffer F field.
     */
    @NotNull
    private final Label frameBuffersFField;

    /**
     * The frame buffer S field.
     */
    @NotNull
    private final Label frameBuffersSField;

    /**
     * The textures M field.
     */
    @NotNull
    private final Label texturesMField;

    /**
     * The textures F field.
     */
    @NotNull
    private final Label texturesFField;

    /**
     * The textures S field.
     */
    @NotNull
    private final Label texturesSField;

    /**
     * The shaders M field.
     */
    @NotNull
    private final Label shadersMField;

    /**
     * The shaders F field.
     */
    @NotNull
    private final Label shadersFField;

    /**
     * The shaders S field.
     */
    @NotNull
    private final Label shadersSField;

    /**
     * The objects field.
     */
    @NotNull
    private final Label objectsField;

    /**
     * The uniforms field.
     */
    @NotNull
    private final Label uniformsField;

    /**
     * The triangles field.
     */
    @NotNull
    private final Label trianglesField;

    /**
     * The vertices S field.
     */
    @NotNull
    private final Label verticesField;

    /**
     * The filed to show FPS.
     */
    @NotNull
    private final Label fpsField;

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

    public Stats3dPart(@NotNull Pane parent) {
        this.parent = parent;
        this.statsContainer = new GridPane();
        this.frameCounter = 0;
        this.secondCounter = 0.0f;
        this.prevFps = -1;
        this.frameBuffersMField = new Label();
        this.frameBuffersFField = new Label();
        this.frameBuffersSField = new Label();
        this.texturesMField = new Label();
        this.texturesFField = new Label();
        this.texturesSField = new Label();
        this.shadersMField = new Label();
        this.shadersFField = new Label();
        this.shadersSField = new Label();
        this.objectsField = new Label();
        this.uniformsField = new Label();
        this.trianglesField = new Label();
        this.verticesField = new Label();
        this.fpsField = new Label();
        createComponents();
        setEnabled(false);
    }

    /**
     * Create stats fields.
     */
    @FxThread
    private void createComponents() {

        var frameBuffersMLabel = new Label("FrameBuffers (M)");
        var frameBuffersFLabel = new Label("FrameBuffers (F)");
        var frameBuffersSLabel = new Label("FrameBuffers (S)");

        var texturesMLabel = new Label("Textures (M)");
        var texturesFLabel = new Label("Textures (F)");
        var texturesSLabel = new Label("Textures (S)");

        var shadersMLabel = new Label("Shaders (M)");
        var shadersFLabel = new Label("Shaders (F)");
        var shadersSLabel = new Label("Shaders (S)");

        var objectsLabel = new Label("Objects");
        var uniformsLabel = new Label("Uniforms");
        var trianglesLabel = new Label("Triangles");
        var verticesLabel = new Label("Vertices");

        var fpsLabel = new Label("Fps");

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

        this.statistics = application.getRenderer()
                .getStatistics();

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

    @Override
    @JmeThread
    public void update(float tpf) {

        if (!isEnabled()) {
            return;
        }

        var application = requireApplication();
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
            verticesField.setText(Integer.toString(vertices));
            trianglesField.setText(Integer.toString(triangles));
            uniformsField.setText(Integer.toString(uniforms));
            objectsField.setText(Integer.toString(objects));
            shadersSField.setText(Integer.toString(shadersS));
            shadersFField.setText(Integer.toString(shadersF));
            shadersMField.setText(Integer.toString(shadersM));
            texturesSField.setText(Integer.toString(texturesS));
            texturesFField.setText(Integer.toString(texturesF));
            texturesMField.setText(Integer.toString(texturesM));
            frameBuffersSField.setText(Integer.toString(frameBuffersS));
            frameBuffersFField.setText(Integer.toString(frameBuffersF));
            frameBuffersMField.setText(Integer.toString(frameBuffersM));
        });
    }

    /**
     * Update the FPS value.
     */
    @JmeThread
    private void updateFps(int fps) {
        ExecutorManager.getInstance()
                .addFxTask(() -> fpsField.setText(Integer.toString(fps)));
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
