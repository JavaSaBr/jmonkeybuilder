# jMonkeyEngine 3 SpaceShift Editor 0.9.11 #
## License: Apache Version 2.0 ##

* [Wiki](https://bitbucket.org/JavaSabr/jme3-spaceshift-editor/wiki/Home)
* [Download](https://yadi.sk/d/UuKcJBNgqbV3a)
* [Gitter](https://gitter.im/jME3-SpaceShift-Editor/Lobby?source=orgpage)
* [Official jMonkey thread](https://hub.jmonkeyengine.org/t/jme3-spaceshift-editor/35179)
* [Youtube channel](https://www.youtube.com/playlist?list=PLNdOH0eRoQMBkLPBvTIDn02UFhcTJWsh7)

## [Video about this editor](https://youtu.be/h6azH-D28qk) ##

## ver. 0.9.11 ##
* -The project was migrated to gradle.
* -Implemented a plugin system. Now you can develop your own plugin for the Editor.
* -Added new UI theme.
* -Fixed bugs with flying camera.
* -Optimized the mechanism of resizing render window.
* -Updated settings of default j3s scene.
* -Fixed bugs with focus in scene editor.
* -Updated some property controls.
* -Implemented local/global/view transformation tools.
* -Optimized performance of terrain editing controls.
* -Fixed some problems with saving files.
* -Fixed copying files from the Editor to Nautilus(file explorer in Ubuntu).
* -Integrated global/local water filters to scene editor.
* -Fixed some bugs.

## ver. 0.9.10 ##
* -Added supporting macOS.
* -Updated icon-set to use only SVG icons and removed icons which were under GPLv2 license.
* -Added actions to a menu bar: About and Exit.
* -Implemented new light/dark themes and added an option to a settings dialog to choose it,
* -Added an option to a settings dialog to choose OpenGL version of jME render.
* -Added an option to a settings dialog to disable/enable stop a render on lost focus of a window of.
* -Improved auto-synchronizing of a workspace to handle more cases of external changes.
* -Fixed some bugs.

## ver. 0.9.9 ##
* -Implemented integration with built-in of jME3 particle system.
* -Added to send anonymous google analytics only about start/close application events if a user disable analytics.
* -Added an action to create a new material definition with a fragment and a vertex shader.
* -Updated UI.
* -Added handling shader errors to avoid application crashes, so now a user can edit shaders without worrying about an application crash.
* -Implemented autorefrsh opened material in the Material Editor when a user change a material defintion in the MD Editor or shaders in the GLSL editor.
* -Updated all dialogs.
* -Fixed some bugs.

## ver. 0.9.8 ##
* -Added an action to reset particle emitters.
* -Updated toneg0d.emitterNode to 2.2.2, updated the integration with this lib.
* -Updated UI.
* -Added an action 'Pause' to animation nodes, added information about animation length.
* -Started to use new extension library to integrate custom classes with the editor: https://github.com/JavaSaBr/jme3-spaceshift-extension
* -Fixed some bugs.

## ver. 0.9.7 ##
* -Fixed some bugs.

## ver. 0.9.6 ##
* -Updated jME libraries.
* -Updated LWJGL libraries to 3.1.2 version.
* -Added support the AssetLinkNode.
* -Added DE language.
* -Added auto checking new versions of the Editor on application start.
* -Added a button to show/hide render statistics in the model/scene editors.
* -Changed the logic of using material names in exporting materials during converting models.
* -Implemented the first part of scripting. Added scripting panel to the model/scene editor.
* -Fixed some bugs.

## ver. 0.9.5 ##
* -Updated jME libraries.
* -Updated styles of all dialogs.
* -Implemented image view/preview of .dds and .hdr images.
* -Added tooltips for some actions in an editor toolbar.
* -Small UI fixes for Material Editor.
* -Implemented flying on a scene using WASD + middle mouse button.
* -Added a dialog with some settings of converting models.
* -Moved the Log View to bottom.
* -Some updates of representation a motion control.
* -Reduced cell height in the asset tree and the model tree.
* -Added detail information about meshes.
* -Implemented D&D models from the asset tree to the model tree.

## ver. 0.9.4 ##
* [Video](https://www.youtube.com/watch?v=d-7HdPSl1BE&feature=youtu.be)
* -Updated jME libraries.
* -Added an action to switch off|on showing audio/light models in the scene editor.
* -Added an action to create terrain.
* -Implemented terrain editing.
* -Updated the toneg0d.emitterNode library with some optimizations.
* -Updated working with particle nodes.
* -Fixed some bugs.

## ver. 0.9.3 ##
* [Video](https://www.youtube.com/watch?v=FdGnkPj_qX0&feature=youtu.be)
* -Added a bullet app state to scene editor.
* -Added supporting debug physics to a bullet app state.
* -Added supporting editing rigid/vehicle/character controls.
* -Added actions to make collision shapes.
* -Added supporting creating/editing vehicle wheels.
* -Updated working with layers in scene editor.
* -Some fixes and improvements.

## ver. 0.9.2 ##
* [Video](https://www.youtube.com/watch?v=CPNaI9jDoOk&feature=youtu.be)
* -Implemented scene filters editing and provided API to support custom filters.
* -Added some build in filters.
* -Fixed problems with custom classpath.
* -Implemented D&D models from asset tree to model/scene editor.
* -Implemented renaming lights in model/scene editor.
* -Re-implemented working with scene layers.
* -Implemented more user-friendly light editing in scene/model editor.
* -Updated API for custom app states.
* -Updated file selector dialogs, added some actions to a context menu.
* -Updated a grid in scene/model editor.
* -Added auto move editor's camera to selected node.
* -Implemented D&D materials from asset tree to model/scene editor.
* -Implemented D&D textures from asset tree to material editor.
* -Added hotkeys(R/G/S) to switch a manipulator mode in scene/model editor.
* -Added hotkey(delete) to delete a selected node from scene/model editor.
* -Implemented D&D audio files from asset tree to audio data property in model/scene editor.
* -Implemented control editing and provided API to support custom controls.

## ver. 0.9.1 ##
* [Video](https://www.youtube.com/watch?v=UkG3blab-xg&feature=youtu.be)
* -Add some settings to setting dialog.
* -Added shift+ctrl value scrolling.
* -Added environments folder to settings dialog.
* -Started implementing a scene editor.
* -Updated jME libraries.
* -Implemented layers in a scene editor.
* -Implemented scene states editing and provided API to support custom states.
* -Integrated lighting and sky states from SimFX library.
* -Implemented user data editing.
* -Added some performance optimizations with jME render inside JavaFX.

## ver. 0.9.0 ##
* [Video](https://www.youtube.com/watch?v=LNrDesrg7zc&feature=youtu.be)
* -Implemented soft particles emitterNode.
* -Fixed gamma correction and updated fast envs in the ModelEditor.
* -Fixed some problems with standard shaders.
* -Fixed the some bugs with editing influencers.
* -Implemented auto-refreshing files which was edited from external editors.
* -Implemented memory optimizations of the toneg0d emitters.
* -Added lifetime of an emitterNode node.
* -Added control to edit geometry list of a physics influencer.

## ver. 0.8.9 ##
* [Video](https://www.youtube.com/watch?v=xCSUIia-jh4&feature=youtu.be)
* -Added the Apache License.
* -Added an action to create a single color texture.
* -Added an AudioViewer and implement supporting AudioNode in the ModelEditor.
* -Added an action to convert a .xbuf model to .j3o.
* -Updated the list of embedded environment textures.
* -Implemented a LoD Generator.
* -Added a frame rate setting to settings dialog.
* -Updated a style of a ColorPicker control.
* -Updated an ImageViewer.
* -Added an action to extract sub-animations.

## ver. 0.8.8 ##
* -Implemented drawing jME application inside JavaFX Canvas.
* -Migrated from the ReflectionAllocator to the JEmallocAllocator.
* -Added actions to convert .scene/.mesh.xml/.fbx/.obj to .j3o.
* -Updated context menus in the AssetTree and ModelNodeTree.
* -Added showing animation tracks in the ModelNodeTree.
* -Added some actions to animation nodes in the ModelEditor.
* -Bugfixes.

## ver. 0.8.7 ##
* -Implemented saving/loading particle emitterNode nodes.
* -Added a LogView panel.

## ver. 0.8.6 ##
* -Fixed opening a renamed file in a new editor.
* -Added google analytics, you can disable this in a settings dialog.

## ver. 0.8.5 ##
* -Finished implementing editing particle influencers.
* -Fixed material definition duplicates in the material editor.
* -Changed fonts.
* -Fixed some errors with standard shaders.

## ver. 0.7.8 ##
* -Implemented saving states of window, resource tree and split panels.
* -Updated LWJGL to 3.1.0
* -Updated UI.
* -Fixed some bugs.

## ver. 0.7.7 ##
* -Updated JME platform to 3.2 master branch.
* -Updated JFX and switched integration JavaFX with JME to using ImageView like a viewport of JME.
* -Updated UI.
* -Fixed some bugs.

## ver. 0.7.5 ##
* -Fixed auto synchronize of sub dirrectories in a workspace.

## ver. 0.7.4 ##
* -Updated JME from 3.1 to 3.2.
* -Updated LWJGL from 3.0.0b to 3.0.0 release.

## ver. 0.7.3 ##
* -Updated icons.
* -Updated the vector and the rotation controls for model editor.
* -Implemented controling of camera using keys num1, num2, num3, num4, num6, num7, num8, num9 like the Blender.

## ver. 0.7.2 ##
* -Updated the editor camera for working with large objects.

## ver. 0.7.1 ##
* -Implemented additional classpath folder in Settings -> Other settings.
* -Implemented autosynchronize asset folder with filesystem.

## ver. 0.7.0 ##
* -Implemented loading classes from every *.jars in opened asset folder.

## ver. 0.6.9 ##
* -Updated JME3 libraries.
* -Bug fixes.

## ver. 0.6.7 ##
* -Fixed the preview of TGA textures.
* -Changed the cache of preview of texture.
* -Added the player of animation in the ModelEditor.

## ver. 0.6.6 ##
* -Changed the size of batch of light pass to 5.
* -Updated the JME to 3.1.alpha4.
* -Applyed the last fix for PBR.

## ver. 0.6.5 ##
* -Fixed the problems with sticking cursor while rotating camera of editor.
* -Fixed the crash when loading model with bad material. 
* -Fixed the using custom cursors on LWJGL3.
* -Added beta support of resizing window.
* -Added the functionality for works with a light in Model Editor. 

## ver. 0.6.2 ##
* -Fixed the problems with mouse events. 

## ver. 0.6.1 ##
* -Fixed the missing of tangents in models in Material Editor.
* -Added the option of gamma correction and option of ToneMapFilter to Graphics Settings.
* -Fixed the problems with sticking cursor while rotating camera of editor. 

## ver. 0.6.0 ##
* -Implemented undo-redo(ctrl+z/ctrl+y) in Material Editor and Model Editor.

## ver. 0.5.2 ##
* -Implemented the autocomplete for type of material in material creator.
* -Fixed the problems with focus in dialogs of editor.
* -Added the animation of loading for editors.

## ver. 0.5.0 ##
* -Fixed the exception with LwjglWindow on Windows;
* -Implemented the camera like camera of Blender.
* -Fixed the text input.
* -Implemented the hotkey 'delete' to delete files in tree.
* -Implemented the manipulators in Model Editor.
* -Updated the action for generating tangents.
* -Changed rotation property of model to Euler in Model Editor.

## ver. 0.4.5 ##
* -Added the text editor for GLSL with highlighting.
* -Added the reloading materials when change shaders.
* -Updated the logic for change of material type.
* -Fixed problems with menu bar.
* -Added the action for closing editor to menu bar.
* -Added tooltip with fullpath for root folder in asset tree.
* -Added the action for reopening last asset folders.
* -Implemented the saving of opened files.
* -Updated the jME to alpha 3 with PBR.
* -Migrated the editor from LWJGL 2.9 to LWJGL 3.0
* -Added the combobox with Queue Bucket for model of material preview to material editor.
* -Implemented the save on ctrl+S hotkey.

## ver. 0.4.0 ##
* -Added the action for creating a folder.
* -Added the action for creating an empty file.
* -Implemented image viewer.
* -Added actions for creating node, box, sphere and quad in ModelEditor.
* -Added action for creating SkyBox.
* -Added action for loading other model to model in Model Editor.
* -Fixed severals bugs.

## ver. 0.3.7 ##
* -Implemented the fullscreen mode.
* -Implemented the preview of channels of image.
* -Implemented the cache of preview of image.
* -Changed the sensitivity of zoom.

## ver. 0.3.5 ##
* -Implemeted open file in external editor.
* -Finished implementing the operations of moving/renaming/cut-copy-paste for files in Asset Tree.
* -Added dialog for handling exception.

## ver. 0.3.0 ##
* -Implemented Simple Model Editor.
* -Added action for converting .blend to .j3o.
* -Updated UI.

## ver. 0.2.0 ##
* -Implemented dialogs for creating new material and post filter view file.
* -Implemented dialog of graphic settings.
* -Updated the sky in material editor.
* -Upgraded jME3 libraries to 3.1 alpha 2 with PBR.

## ver. 0.1.1 ##
* -Implemented preview of images in resource selector.
* -Migrated editor to jME 3.1 with PBR.
* -Added support PBR to Material Editor.
* -Finished implementation PostFilterViewer.
## ver. 0.1 ##
* -Implemented the base actions in AssetTree.
* -Implemented MaterialEditor.
* -Added English localization.