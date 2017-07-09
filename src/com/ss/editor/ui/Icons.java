package com.ss.editor.ui;

import com.ss.editor.manager.FileIconManager;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * The interface with all icons of this application.
 *
 * @author JavaSaBr
 */
public interface Icons {

    /**
     * The constant ICON_MANAGER.
     */
    @NotNull
    FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The constant REMOVE_12.
     */
    Image REMOVE_12 = ICON_MANAGER.getImage("/ui/icons/svg/horizontal-line-remove-button.svg", 12, false);
    /**
     * The constant ADD_12.
     */
    Image ADD_12 = ICON_MANAGER.getImage("/ui/icons/svg/add-plus-button.svg", 12, false);

    /**
     * The constant SAVE_16.
     */
    Image SAVE_16 = ICON_MANAGER.getImage("/ui/icons/svg/save-disk.svg", 16);
    /**
     * The constant SCALE_16.
     */
    Image SCALE_16 = ICON_MANAGER.getImage("/ui/icons/svg/resize.svg", 16);
    /**
     * The constant ROTATION_16.
     */
    Image ROTATION_16 = ICON_MANAGER.getImage("/ui/icons/svg/eliptical-arrows.svg", 16);
    /**
     * The constant CUBE_16.
     */
    Image CUBE_16 = ICON_MANAGER.getImage("/ui/icons/svg/hollow-cube.svg");
    /**
     * The constant MOVE_16.
     */
    Image MOVE_16 = ICON_MANAGER.getImage("/ui/icons/svg/move-arrows.svg", 16);
    /**
     * The constant LIGHT_16.
     */
    Image LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/svg/idea.svg");
    /**
     * The constant INFLUENCER_16.
     */
    Image INFLUENCER_16 = ICON_MANAGER.getImage("/ui/icons/svg/enhance-effect.svg", 16);
    /**
     * The constant SPHERE_16.
     */
    Image SPHERE_16 = ICON_MANAGER.getImage("/ui/icons/svg/planet-sphere.svg");
    /**
     * The constant PLANE_16.
     */
    Image PLANE_16 = ICON_MANAGER.getImage("/ui/icons/svg/table.svg", 16);
    /**
     * The constant NODE_16.
     */
    Image NODE_16 = ICON_MANAGER.getImage("/ui/icons/svg/family-tree.svg");
    /**
     * The constant PARTICLES_16.
     */
    Image PARTICLES_16 = ICON_MANAGER.getImage("/ui/icons/svg/molecule_2.svg", 16);
    /**
     * The constant GEOMETRY_16.
     */
    Image GEOMETRY_16 = ICON_MANAGER.getImage("/ui/icons/svg/cube-divisions.svg");
    /**
     * The constant MESH_16.
     */
    Image MESH_16 = ICON_MANAGER.getImage("/ui/icons/svg/grid.svg");
    /**
     * The constant EDIT_16.
     */
    Image EDIT_16 = ICON_MANAGER.getImage("/ui/icons/svg/pencil-edit-button.svg");
    /**
     * The constant AMBIENT_16.
     */
    Image AMBIENT_16 = ICON_MANAGER.getImage("/ui/icons/svg/brightness.svg");
    /**
     * The constant LAMP_16.
     */
    Image LAMP_16 = ICON_MANAGER.getImage("/ui/icons/svg/lantern.svg");
    /**
     * The constant POINT_LIGHT_16.
     */
    Image POINT_LIGHT_16 = ICON_MANAGER.getImage("/ui/icons/svg/light-bulb.svg");
    /**
     * The constant SUN_16.
     */
    Image SUN_16 = ICON_MANAGER.getImage("/ui/icons/svg/sunny-day.svg");
    /**
     * The constant PLAY_16.
     */
    Image PLAY_16 = ICON_MANAGER.getImage("/ui/icons/svg/play-button.svg");
    /**
     * The constant STOP_16.
     */
    Image STOP_16 = ICON_MANAGER.getImage("/ui/icons/svg/stop.svg");
    /**
     * The constant PAUSE_16.
     */
    Image PAUSE_16 = ICON_MANAGER.getImage("/ui/icons/svg/pause.svg");
    /**
     * The constant ANIMATION_16.
     */
    Image ANIMATION_16 = ICON_MANAGER.getImage("/ui/icons/svg/movie-symbol-of-video-camera.svg", 16);
    /**
     * The constant GEAR_16.
     */
    Image GEAR_16 = ICON_MANAGER.getImage("/ui/icons/svg/settings.svg", 16);
    /**
     * The constant BONE_16.
     */
    Image BONE_16 = ICON_MANAGER.getImage("/ui/icons/svg/bone.svg", 16);
    /**
     * The constant AUDIO_16.
     */
    Image AUDIO_16 = ICON_MANAGER.getImage("/ui/icons/svg/audio-volume.svg");
    /**
     * The constant SETTINGS_16.
     */
    Image SETTINGS_16 = ICON_MANAGER.getImage("/ui/icons/svg/settings.svg");
    /**
     * The constant PASTE_16.
     */
    Image PASTE_16 = ICON_MANAGER.getImage("/ui/icons/svg/clipboard-paste-option.svg");
    /**
     * The constant NEW_FILE_16.
     */
    Image NEW_FILE_16 = ICON_MANAGER.getImage("/ui/icons/svg/add-new-file.svg", 16);
    /**
     * The constant CUT_16.
     */
    Image CUT_16 = ICON_MANAGER.getImage("/ui/icons/svg/cut-content-button.svg", 16);
    /**
     * The constant COPY_16.
     */
    Image COPY_16 = ICON_MANAGER.getImage("/ui/icons/svg/copy-file.svg", 16);
    /**
     * The constant TRANSFORMATION_16.
     */
    Image TRANSFORMATION_16 = ICON_MANAGER.getImage("/ui/icons/svg/transformation-of-geometric-shapes-from-cube-to-cone-outlines.svg");
    /**
     * The constant EXTRACT_16.
     */
    Image EXTRACT_16 = ICON_MANAGER.getImage("/ui/icons/svg/extract-image.svg", 16);
    /**
     * The constant SCENE_16.
     */
    Image SCENE_16 = ICON_MANAGER.getImage("/ui/icons/svg/line-segment.svg");
    /**
     * The constant LAYERS_16.
     */
    Image LAYERS_16 = ICON_MANAGER.getImage("/ui/icons/svg/layers.svg", 16);
    /**
     * The constant OPEN_FILE_16.
     */
    Image OPEN_FILE_16 = ICON_MANAGER.getImage("/ui/icons/svg/open-folder-with-document.svg");
    /**
     * The constant EMITTER_16.
     */
    Image EMITTER_16 = ICON_MANAGER.getImage("/ui/icons/svg/atom-symbol.svg", 16);
    /**
     * The constant SKY_16.
     */
    Image SKY_16 = ICON_MANAGER.getImage("/ui/icons/svg/cloudy-day-outlined-weather-interface-symbol.svg");
    /**
     * The constant INVISIBLE_16.
     */
    Image INVISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/invisible.svg", 16);
    /**
     * The constant VISIBLE_16.
     */
    Image VISIBLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/eye-view-interface-symbol.svg");
    /**
     * The constant STATIC_RIGID_BODY_16.
     */
    Image STATIC_RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/svg/brickwall-.svg");
    /**
     * The constant RIGID_BODY_16.
     */
    Image RIGID_BODY_16 = ICON_MANAGER.getImage("/ui/icons/svg/soccer-ball.svg");
    /**
     * The constant REPLAY_16.
     */
    Image REPLAY_16 = ICON_MANAGER.getImage("/ui/icons/svg/replay.svg");
    /**
     * The constant CHARACTER_16.
     */
    Image CHARACTER_16 = ICON_MANAGER.getImage("/ui/icons/svg/user-silhouette.svg", 16);
    /**
     * The constant SKELETON_16.
     */
    Image SKELETON_16 = ICON_MANAGER.getImage("/ui/icons/svg/bones.svg");
    /**
     * The constant VEHICLE_16.
     */
    Image VEHICLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/sports-car.svg");
    /**
     * The constant ATOM_16.
     */
    Image ATOM_16 = ICON_MANAGER.getImage("/ui/icons/svg/molecule.svg", 16);
    /**
     * The constant PHYSICS_16.
     */
    Image PHYSICS_16 = ICON_MANAGER.getImage("/ui/icons/svg/black-hole.svg", 16);
    /**
     * The constant DOLL_16.
     */
    Image DOLL_16 = ICON_MANAGER.getImage("/ui/icons/svg/doll.svg", 16);
    /**
     * The constant CAPSULE_16.
     */
    Image CAPSULE_16 = ICON_MANAGER.getImage("/ui/icons/svg/capsule-black-and-white-variant.svg", 16);
    /**
     * The constant CONE_16.
     */
    Image CONE_16 = ICON_MANAGER.getImage("/ui/icons/svg/cone-geometrical-shape.svg", 16);
    /**
     * The constant CYLINDER_16.
     */
    Image CYLINDER_16 = ICON_MANAGER.getImage("/ui/icons/svg/cylinder.svg");
    /**
     * The constant TERRAIN_16.
     */
    Image TERRAIN_16 = ICON_MANAGER.getImage("/ui/icons/svg/terrain.svg");
    /**
     * The constant WHEEL_16.
     */
    Image WHEEL_16 = ICON_MANAGER.getImage("/ui/icons/svg/wheel.svg");
    /**
     * The constant TRIANGLE_16.
     */
    Image TRIANGLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/triangle.svg");
    /**
     * The constant DOME_16.
     */
    Image DOME_16 = ICON_MANAGER.getImage("/ui/icons/svg/reichstag-dome.svg");
    /**
     * The constant QUAD_16.
     */
    Image QUAD_16 = ICON_MANAGER.getImage("/ui/icons/svg/basic-square.svg");
    /**
     * The constant RHOMB_16.
     */
    Image RHOMB_16 = ICON_MANAGER.getImage("/ui/icons/svg/rhombus.svg");
    /**
     * The constant TORUS_16.
     */
    Image TORUS_16 = ICON_MANAGER.getImage("/ui/icons/svg/circle.svg");
    /**
     * The constant POINTS_16.
     */
    Image POINTS_16 = ICON_MANAGER.getImage("/ui/icons/svg/because-mathematical-symbol.svg");
    /**
     * The constant IMPOSTOR_16.
     */
    Image IMPOSTOR_16 = ICON_MANAGER.getImage("/ui/icons/svg/plus.svg");
    /**
     * The constant REMOVE_16.
     */
    Image REMOVE_16 = ICON_MANAGER.getImage("/ui/icons/svg/horizontal-line-remove-button.svg", 16, false);
    /**
     * The constant ADD_16.
     */
    Image ADD_16 = ICON_MANAGER.getImage("/ui/icons/svg/add-plus-button.svg", 16, false);
    /**
     * The constant MOTION_16.
     */
    Image MOTION_16 = ICON_MANAGER.getImage("/ui/icons/svg/horse-in-running-motion-silhouette.svg", 16, false);
    /**
     * The constant PATH_16.
     */
    Image PATH_16 = ICON_MANAGER.getImage("/ui/icons/svg/map-location.svg", 16, false);
    /**
     * The constant WAY_POINT_16.
     */
    Image WAY_POINT_16 = ICON_MANAGER.getImage("/ui/icons/svg/placeholder.svg", 16, false);
    /**
     * The constant VERTEX_16.
     */
    Image VERTEX_16 = ICON_MANAGER.getImage("/ui/icons/svg/graphene.svg", 16, false);
    /**
     * The constant DATA_16.
     */
    Image DATA_16 = ICON_MANAGER.getImage("/ui/icons/svg/database.svg", 16, false);
    /**
     * The constant LINKED_NODE_16.
     */
    Image LINKED_NODE_16 = ICON_MANAGER.getImage("/ui/icons/svg/link.svg");
    /**
     * The constant LINK_FILE_16.
     */
    Image LINK_FILE_16 = ICON_MANAGER.getImage("/ui/icons/svg/link-folder-with-document.svg");
    /**
     * The constant STATISTICS_16.
     */
    Image STATISTICS_16 = ICON_MANAGER.getImage("/ui/icons/svg/bar-chart.svg");
    /**
     * The constant STATISTICS_16.
     */
    Image DOR_IN_CIRCLE_16 = ICON_MANAGER.getImage("/ui/icons/svg/dot-and-circle.svg");

    /**
     * The constant REFRESH_18.
     */
    Image REFRESH_18 = ICON_MANAGER.getImage("/ui/icons/svg/refresh-button.svg", 18);
    /**
     * The constant WARNING_24.
     */
    Image WARNING_24 = ICON_MANAGER.getImage("/ui/icons/svg/warning.svg", 24);

    /**
     * The constant TERRAIN_LEVEL_32.
     */
    Image TERRAIN_LEVEL_32 = ICON_MANAGER.getImage("/ui/icons/svg/level_terrain.svg", 32);
    /**
     * The constant TERRAIN_PAINT_32.
     */
    Image TERRAIN_PAINT_32 = ICON_MANAGER.getImage("/ui/icons/svg/paint_terrain.svg", 32);
    /**
     * The constant TERRAIN_ROUGH_32.
     */
    Image TERRAIN_ROUGH_32 = ICON_MANAGER.getImage("/ui/icons/svg/rough_terrain.svg", 32);
    /**
     * The constant TERRAIN_SLOPE_32.
     */
    Image TERRAIN_SLOPE_32 = ICON_MANAGER.getImage("/ui/icons/svg/slope_terrain.svg", 32);
    /**
     * The constant TERRAIN_SMOOTH_32.
     */
    Image TERRAIN_SMOOTH_32 = ICON_MANAGER.getImage("/ui/icons/svg/smooth_terrain.svg", 32);
    /**
     * The constant TERRAIN_UP_32.
     */
    Image TERRAIN_UP_32 = ICON_MANAGER.getImage("/ui/icons/svg/raise_terrain.svg", 32);

    /**
     * The constant APPLICATION_64.
     */
    Image APPLICATION_64 = ICON_MANAGER.getImage("/ui/icons/app/SSEd64.png", 64);

    /**
     * The constant PLAY_128.
     */
    Image PLAY_128 = ICON_MANAGER.getImage("/ui/icons/svg/play-button.svg", 128);
    /**
     * The constant PAUSE_128.
     */
    Image PAUSE_128 = ICON_MANAGER.getImage("/ui/icons/svg/pause.svg", 128);
    /**
     * The constant STOP_128.
     */
    Image STOP_128 = ICON_MANAGER.getImage("/ui/icons/svg/stop.svg", 128);

    /**
     * The constant IMAGE_512.
     */
    Image IMAGE_512 = ICON_MANAGER.getImage("/ui/icons/svg/picture.svg", 512);
}
