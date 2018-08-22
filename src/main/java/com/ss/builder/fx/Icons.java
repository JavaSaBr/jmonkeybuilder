package com.ss.builder.fx;

import com.ss.builder.manager.FileIconManager;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * The interface with all icons of this application.
 *
 * @author JavaSaBr
 */
public interface Icons {

    @NotNull FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    @NotNull Image REMOVE_12 = ICON_MANAGER.getImage("/ui/icons/svg/horizontal-line-remove-button.svg", 12, false);
    @NotNull Image ADD_12 = ICON_MANAGER.getImage("/ui/icons/svg/add-plus-button.svg", 12, false);

    @NotNull Image SAVE_16 = ICON_MANAGER.getImage("/ui/icons/svg/save-disk.svg", 16);
    @NotNull Image SCALE_16 = ICON_MANAGER.getImage("/ui/icons/svg/resize.svg", 16);
    @NotNull Image ROTATION_16 = ICON_MANAGER.getImage("/ui/icons/svg/eliptical-arrows.svg", 16);
    @NotNull Image CUBE_16 = ICON_MANAGER.getImage("/ui/icons/svg/hollow-cube.svg");
    @NotNull Image MOVE_16 = ICON_MANAGER.getImage("/ui/icons/svg/move-arrows.svg", 16);
    @NotNull Image LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/svg/idea.svg");
    @NotNull Image INFLUENCER_16 = ICON_MANAGER.getImage("/ui/icons/svg/enhance-effect.svg", 16);
    @NotNull Image SPHERE_16 = ICON_MANAGER.getImage("/ui/icons/svg/planet-sphere.svg");
    @NotNull Image PLANE_16 = ICON_MANAGER.getImage("/ui/icons/svg/table.svg", 16);
    @NotNull Image NODE_16 = ICON_MANAGER.getImage("/ui/icons/svg/family-tree.svg");
    @NotNull Image PARTICLES_16 = ICON_MANAGER.getImage("/ui/icons/svg/molecule_2.svg", 16);
    @NotNull Image DEBUG_16 = ICON_MANAGER.getImage("/ui/icons/svg/debug.svg", 16);
    @NotNull Image GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/svg/cube-divisions.svg");
    @NotNull Image MESH_16 = ICON_MANAGER.getImage("/ui/icons/svg/grid.svg");
    @NotNull Image MATERIAL_16 = ICON_MANAGER.getImage("/ui/icons/svg/draws.svg");
    @NotNull Image EDIT_16 = ICON_MANAGER.getImage("/ui/icons/svg/pencil-edit-button.svg");
    @NotNull Image AMBIENT_16 = ICON_MANAGER.getImage("/ui/icons/svg/brightness.svg");
    @NotNull Image LAMP_16 = ICON_MANAGER.getImage("/ui/icons/svg/lantern.svg");
    @NotNull Image POINT_LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/svg/light-bulb.svg");
    @NotNull Image SUN_16 = ICON_MANAGER.getImage("/ui/icons/svg/sunny-day.svg");
    @NotNull Image PLAY_16 = ICON_MANAGER.getImage("/ui/icons/svg/play-button.svg");
    @NotNull Image STOP_16 = ICON_MANAGER.getImage("/ui/icons/svg/stop.svg");
    @NotNull Image PAUSE_16 = ICON_MANAGER.getImage("/ui/icons/svg/pause.svg");
    @NotNull Image ANIMATION_16 = ICON_MANAGER.getImage("/ui/icons/svg/movie-symbol-of-video-camera.svg", 16);
    @NotNull Image GEAR_16 = ICON_MANAGER.getImage("/ui/icons/svg/settings.svg", 16);
    @NotNull Image EXPORT_16 = ICON_MANAGER.getImage("/ui/icons/svg/scale-symbol.svg", 16);
    @NotNull Image IMPORT_16 = ICON_MANAGER.getImage("/ui/icons/svg/import.svg", 16);
    @NotNull Image EXPLORER_16 = ICON_MANAGER.getImage("/ui/icons/svg/inbox.svg", 16);
    @NotNull Image EDIT_2_16 = ICON_MANAGER.getImage("/ui/icons/svg/font-selection-editor.svg", 16);
    @NotNull Image BONE_16 = ICON_MANAGER.getImage("/ui/icons/svg/bone.svg", 16);
    @NotNull Image AUDIO_16 = ICON_MANAGER.getImage("/ui/icons/svg/audio-volume.svg");
    @NotNull Image SETTINGS_16 = ICON_MANAGER.getImage("/ui/icons/svg/settings.svg");
    @NotNull Image PASTE_16 = ICON_MANAGER.getImage("/ui/icons/svg/clipboard-paste-option.svg");
    @NotNull Image NEW_FILE_16 = ICON_MANAGER.getImage("/ui/icons/svg/add-new-file.svg", 16);
    @NotNull Image CUT_16 = ICON_MANAGER.getImage("/ui/icons/svg/cut-content-button.svg", 16);
    @NotNull Image COPY_16 = ICON_MANAGER.getImage("/ui/icons/svg/copy-file.svg", 16);
    @NotNull Image TRANSFORMATION_16 = ICON_MANAGER.getImage("/ui/icons/svg/transformation-of-geometric-shapes-from-cube-to-cone-outlines.svg");
    @NotNull Image EXTRACT_16 = ICON_MANAGER.getImage("/ui/icons/svg/extract-image.svg", 16);
    @NotNull Image SCENE_16 = ICON_MANAGER.getImage("/ui/icons/svg/line-segment.svg");
    @NotNull Image LAYERS_16 = ICON_MANAGER.getImage("/ui/icons/svg/layers.svg", 16);
    @NotNull Image OPEN_FILE_16 = ICON_MANAGER.getImage("/ui/icons/svg/open-folder-with-document.svg");
    @NotNull Image EMITTER_16 = ICON_MANAGER.getImage("/ui/icons/svg/atom-symbol.svg", 16);
    @NotNull Image SKY_16 = ICON_MANAGER.getImage("/ui/icons/svg/cloudy-day-outlined-weather-interface-symbol.svg");
    @NotNull Image INVISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/invisible.svg", 16);
    @NotNull Image VISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/eye-view-interface-symbol.svg");
    @NotNull Image STATIC_RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/svg/brickwall-.svg");
    @NotNull Image RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/svg/soccer-ball.svg");
    @NotNull Image REPLAY_16 = ICON_MANAGER.getImage("/ui/icons/svg/replay.svg");
    @NotNull Image CHARACTER_16 = ICON_MANAGER.getImage("/ui/icons/svg/user-silhouette.svg", 16);
    @NotNull Image SKELETON_16 = ICON_MANAGER.getImage("/ui/icons/svg/bones.svg");
    @NotNull Image VEHICLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/sports-car.svg");
    @NotNull Image ATOM_16 = ICON_MANAGER.getImage("/ui/icons/svg/molecule.svg", 16);
    @NotNull Image PHYSICS_16 = ICON_MANAGER.getImage("/ui/icons/svg/black-hole.svg", 16);
    @NotNull Image DOLL_16 = ICON_MANAGER.getImage("/ui/icons/svg/doll.svg", 16);
    @NotNull Image CAPSULE_16 = ICON_MANAGER.getImage("/ui/icons/svg/capsule-black-and-white-variant.svg", 16);
    @NotNull Image CONE_16 = ICON_MANAGER.getImage("/ui/icons/svg/cone-geometrical-shape.svg", 16);
    @NotNull Image CYLINDER_16 = ICON_MANAGER.getImage("/ui/icons/svg/cylinder.svg");
    @NotNull Image TERRAIN_16 = ICON_MANAGER.getImage("/ui/icons/svg/terrain.svg");
    @NotNull Image WHEEL_16 = ICON_MANAGER.getImage("/ui/icons/svg/wheel.svg");
    @NotNull Image TRIANGLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/triangle.svg");
    @NotNull Image DOME_16 = ICON_MANAGER.getImage("/ui/icons/svg/reichstag-dome.svg");
    @NotNull Image QUAD_16 = ICON_MANAGER.getImage("/ui/icons/svg/basic-square.svg");
    @NotNull Image RHOMB_16 = ICON_MANAGER.getImage("/ui/icons/svg/rhombus.svg");
    @NotNull Image TORUS_16 = ICON_MANAGER.getImage("/ui/icons/svg/circle.svg");
    @NotNull Image POINTS_16 = ICON_MANAGER.getImage("/ui/icons/svg/because-mathematical-symbol.svg");
    @NotNull Image IMPOSTOR_16 = ICON_MANAGER.getImage("/ui/icons/svg/plus.svg");
    @NotNull Image REMOVE_16 = ICON_MANAGER.getImage("/ui/icons/svg/horizontal-line-remove-button.svg", 16, false);
    @NotNull Image ADD_16 = ICON_MANAGER.getImage("/ui/icons/svg/add-plus-button.svg", 16, false);
    @NotNull Image MOTION_16 = ICON_MANAGER.getImage("/ui/icons/svg/horse-in-running-motion-silhouette.svg", 16, false);
    @NotNull Image PATH_16 = ICON_MANAGER.getImage("/ui/icons/svg/map-location.svg", 16, false);
    @NotNull Image WAY_POINT_16 = ICON_MANAGER.getImage("/ui/icons/svg/placeholder.svg", 16, false);
    @NotNull Image VERTEX_16 = ICON_MANAGER.getImage("/ui/icons/svg/graphene.svg", 16, false);
    @NotNull Image DATA_16 = ICON_MANAGER.getImage("/ui/icons/svg/database.svg", 16, false);
    @NotNull Image LINKED_NODE_16 = ICON_MANAGER.getImage("/ui/icons/svg/link.svg");
    @NotNull Image LINK_FILE_16 = ICON_MANAGER.getImage("/ui/icons/svg/link-folder-with-document.svg");
    @NotNull Image STATISTICS_16 = ICON_MANAGER.getImage("/ui/icons/svg/bar-chart.svg");
    @NotNull Image DOR_IN_CIRCLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/dot-and-circle.svg");
    @NotNull Image PLUGIN_16 = ICON_MANAGER.getImage("/ui/icons/svg/plug-silhouette.svg");
    @NotNull Image FILTER_16 = ICON_MANAGER.getImage("/ui/icons/svg/filter-filled-tool-symbol.svg");
    @NotNull Image UPDATER_16 = ICON_MANAGER.getImage("/ui/icons/svg/bar-chart-reload.svg");
    @NotNull Image FOREST_16 = ICON_MANAGER.getImage("/ui/icons/svg/forest.svg");

    @NotNull Image REFRESH_18 = ICON_MANAGER.getImage("/ui/icons/svg/refresh-button.svg", 18);
    @NotNull Image WARNING_24 = ICON_MANAGER.getImage("/ui/icons/svg/warning.svg", 24);

    @NotNull Image TERRAIN_LEVEL_32 = ICON_MANAGER.getImage("/ui/icons/svg/level_terrain.svg", 32);
    @NotNull Image TERRAIN_PAINT_32 = ICON_MANAGER.getImage("/ui/icons/svg/paint_terrain.svg", 32);
    @NotNull Image TERRAIN_ROUGH_32 = ICON_MANAGER.getImage("/ui/icons/svg/rough_terrain.svg", 32);
    @NotNull Image TERRAIN_SLOPE_32 = ICON_MANAGER.getImage("/ui/icons/svg/slope_terrain.svg", 32);
    @NotNull Image TERRAIN_SMOOTH_32 = ICON_MANAGER.getImage("/ui/icons/svg/smooth_terrain.svg", 32);
    @NotNull Image TERRAIN_UP_32 = ICON_MANAGER.getImage("/ui/icons/svg/raise_terrain.svg", 32);

    @NotNull Image APPLICATION_64 = ICON_MANAGER.getImage("/ui/icons/app/64x64.png", 64);

    @NotNull Image PLAY_128 = ICON_MANAGER.getImage("/ui/icons/svg/play-button.svg", 128);
    @NotNull Image PAUSE_128 = ICON_MANAGER.getImage("/ui/icons/svg/pause.svg", 128);
    @NotNull Image STOP_128 = ICON_MANAGER.getImage("/ui/icons/svg/stop.svg", 128);

    @NotNull Image IMAGE_512 = ICON_MANAGER.getImage("/ui/icons/svg/picture.svg", 512);
}
