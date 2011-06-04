/**
 * Copyright (c) 2008-2010 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package org.cytoscape.paperwing.internal;

import java.util.Random;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.DataMode;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.ui.text.BasicText;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.scenegraph.CompileOptions;
import com.ardor3d.util.scenegraph.RenderDelegate;
import com.ardor3d.util.scenegraph.SceneCompiler;

/**
 * Illustrates creating a display list of intrinsic shapes (e.g. Box, Cone, Torus).
 */
@Purpose(htmlDescriptionKey = "com.ardor3d.example.renderer.DisplayListExample", //
thumbnailPath = "com/ardor3d/example/media/thumbnails/renderer_DisplayListExample.jpg", //
maxHeapMemory = 64)
public class DisplayListExample extends ExampleBase {
    private int wrapCount;
    private int index;
    private BasicText _text;
    private BasicText _fpsLabel;
    private final Node _shapeRoot = new Node("shapeRoot");
    private RenderDelegate _delegate;
    private boolean first = true;
    private double counter = 0;
    private int frames = 0;

    private static final int NODE_COUNT = 20000;
    private static final float LARGE_SPHERE_RADIUS = 26.0f;
    private static final float SMALL_SPHERE_RADIUS = 0.032f;

    private DrawnNode[] nodes;

    private class DrawnNode {
        public float x;
        public float y;
        public float z;
    }
    
    private Sphere movingSphere;

    public static void main(final String[] args) {
        start(DisplayListExample.class);
    }

    @Override
    protected void renderExample(final Renderer renderer) {
        if (first) {
            final CompileOptions options = new CompileOptions();
            options.setDisplayList(true);
            SceneCompiler.compile(_shapeRoot, _canvas.getCanvasRenderer().getRenderer(), options);
            first = false;
            _delegate = _shapeRoot.getRenderDelegate(ContextManager.getCurrentContext().getGlContextRep());
        }

        super.renderExample(renderer);
    }

    @Override
    protected void updateExample(final ReadOnlyTimer timer) {
        counter += timer.getTimePerFrame();
        frames++;
        if (counter > 1) {
            final double fps = (frames / counter);
            counter = 0;
            frames = 0;
            _fpsLabel.setText("FPS: " + Math.round(fps));
            // System.out.printf("%7.1f FPS\n", fps);
        }
    }

    @Override
    protected void initExample() {
        _canvas.setTitle("Display List Example");

        _shapeRoot.getSceneHints().setDataMode(DataMode.VBO);
        _root.attachChild(_shapeRoot);

        final Sphere main = new Sphere("Default", 6, 6, 0.15);
        Sphere generated;

        generateNodes();
        for (int i = 0; i < NODE_COUNT; i++) {
            generated = new Sphere("Sphere" + i, 6, 6, 0.15);
            generated.setMeshData(main.getMeshData());
            generated.setModelBound(main.getModelBound());
            // generated.set
            addMesh(generated);
        }
        
        movingSphere = new Sphere("Sphere", 6, 6, 0.15);
        movingSphere.setMeshData(main.getMeshData());
        movingSphere.setModelBound(main.getModelBound());
        movingSphere.setTranslation(new Vector3(LARGE_SPHERE_RADIUS * 2, 0, 0));
        _shapeRoot.attachChild(movingSphere);
        
        /*
         * final TextureState ts = new TextureState(); ts.setTexture(TextureManager.load("images/ardor3d_white_256.jpg",
         * Texture.MinificationFilter.Trilinear, TextureStoreFormat.GuessCompressedFormat, true));
         * _shapeRoot.setRenderState(ts);
         */

        final BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        // _shapeRoot.setRenderState(bs);

        final MaterialState ms = new MaterialState();
        ms.setColorMaterial(ColorMaterial.Diffuse);
        _shapeRoot.setRenderState(ms);

        // Set up our label
        _text = BasicText.createDefaultTextLabel("label", "[SPACE] display list on");
        _text.setTranslation(10, 10, 0);
        // _root.attachChild(_text);

        // Set up fps label
        _fpsLabel = BasicText.createDefaultTextLabel("fpsLabel", "");
        _fpsLabel.setTranslation(5, _canvas.getCanvasRenderer().getCamera().getHeight() - 5 - _fpsLabel.getHeight(), 0);
        _fpsLabel.setTextColor(ColorRGBA.WHITE);
        _fpsLabel.getSceneHints().setOrthoOrder(-1);
        _root.attachChild(_fpsLabel);

        // Test combining the meshes then drawing;
        /*
         * final Node combined = new Node(); combined.attachChild(MeshCombiner.combine(_shapeRoot));
         * 
         * _shapeRoot.detachAllChildren(); _root.attachChild(combined);
         */
    }

    @Override
    protected void registerInputTriggers() {
        super.registerInputTriggers();
        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.SPACE), new TriggerAction() {
            private final boolean useDL = true;

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                // useDL = !useDL;
                if (useDL) {
                    _text.setText("[SPACE] display list on");
                    _shapeRoot.setRenderDelegate(_delegate, ContextManager.getCurrentContext().getGlContextRep());
                } else {
                    _text.setText("[SPACE] display list off");
                    _shapeRoot.setRenderDelegate(null, ContextManager.getCurrentContext().getGlContextRep());
                }
            }
        }));
    }

    private void generateNodes() {
        final Random random = new Random();
        // random.setSeed(nodeSeed);
        // nodeSeed++;
        // 500 should be the default seed

        nodes = new DrawnNode[NODE_COUNT];

        float x, y, z;
        final float radius = LARGE_SPHERE_RADIUS;

        for (int i = 0; i < NODE_COUNT; i++) {
            nodes[i] = new DrawnNode();

            do {
                x = (radius * 2 * random.nextFloat() - radius);
                y = (radius * 2 * random.nextFloat() - radius);
                z = (radius * 2 * random.nextFloat() - radius);
            } while (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) > Math.pow(radius, 2));

            nodes[i].x = x;
            nodes[i].y = y;
            nodes[i].z = z;
        }

        // System.out.println("Last node float: " + random.nextFloat());
    }

    private void addMesh(final Spatial spatial) {
        spatial.setTranslation(nodes[index].x, nodes[index].y, nodes[index].z - 50);
        if (spatial instanceof Mesh) {
            ((Mesh) spatial).updateModelBound();
        }
        _shapeRoot.attachChild(spatial);
        index++;
    }
}
