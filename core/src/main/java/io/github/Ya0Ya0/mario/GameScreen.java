package io.github.Ya0Ya0.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameScreen {
    private PerspectiveCamera cam;
    private ModelBatch modelBatch;
    private Array<Model> models = new Array<>();
    private Array<ModelInstance> instances = new Array<>();
    private ModelInstance kartInstance;
    private Environment environment;
    private Kart kart;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private int roundCounter = 0;
    private boolean crossedStartLine = false;
    private float startLineZ = -80f;

    public GameScreen() {
        create();
    }

    private void create() {
        // Enable depth testing for 3D
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, -65f);
        cam.lookAt(0, 1, -80);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.up.set(0, 1, 0);
        cam.update();

        createTrack();
        createKart();

        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(3f);
    }

    private void createTrack() {
        ModelBuilder modelBuilder = new ModelBuilder();

        // Ground - large grass field
        Model groundModel = modelBuilder.createBox(500f, 0.5f, 500f,
            new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.4f, 0.2f, 1f))),
            Usage.Position | Usage.Normal);
        models.add(groundModel);
        instances.add(new ModelInstance(groundModel, 0, -0.25f, 0));

        // OVAL TRACK - Multiple sections
        float trackWidth = 30f;
        float trackHeight = 0.6f;
        Color trackColor = new Color(0.3f, 0.3f, 0.3f, 1f);

        // Straight sections
        Model straightModel = modelBuilder.createBox(trackWidth, trackHeight, 200f,
            new Material(ColorAttribute.createDiffuse(trackColor)),
            Usage.Position | Usage.Normal);
        models.add(straightModel);
        instances.add(new ModelInstance(straightModel, -70, 0, 0)); // Left straight
        instances.add(new ModelInstance(straightModel, 70, 0, 0));  // Right straight

        // Top and bottom connecting sections
        Model connectModel = modelBuilder.createBox(140f, trackHeight, trackWidth,
            new Material(ColorAttribute.createDiffuse(trackColor)),
            Usage.Position | Usage.Normal);
        models.add(connectModel);
        instances.add(new ModelInstance(connectModel, 0, 0, -100)); // Top
        instances.add(new ModelInstance(connectModel, 0, 0, 100));  // Bottom

        // Corner fills to make smooth oval
        Model cornerModel = modelBuilder.createBox(trackWidth, trackHeight, trackWidth,
            new Material(ColorAttribute.createDiffuse(trackColor)),
            Usage.Position | Usage.Normal);
        models.add(cornerModel);
        instances.add(new ModelInstance(cornerModel, -70, 0, -100)); // Top-left
        instances.add(new ModelInstance(cornerModel, 70, 0, -100));  // Top-right
        instances.add(new ModelInstance(cornerModel, -70, 0, 100));  // Bottom-left
        instances.add(new ModelInstance(cornerModel, 70, 0, 100));   // Bottom-right

        // Starting line
        Model startLineModel = modelBuilder.createBox(trackWidth, 0.7f, 4f,
            new Material(ColorAttribute.createDiffuse(Color.WHITE)),
            Usage.Position | Usage.Normal);
        models.add(startLineModel);
        instances.add(new ModelInstance(startLineModel, -70, 0.1f, -80));

        // OUTER BARRIERS
        Model barrierModel = modelBuilder.createBox(3f, 4f, 220f,
            new Material(ColorAttribute.createDiffuse(Color.RED)),
            Usage.Position | Usage.Normal);
        models.add(barrierModel);
        instances.add(new ModelInstance(barrierModel, -86, 2f, 0));  // Left outer
        instances.add(new ModelInstance(barrierModel, 86, 2f, 0));   // Right outer

        Model barrierHModel = modelBuilder.createBox(176f, 4f, 3f,
            new Material(ColorAttribute.createDiffuse(Color.RED)),
            Usage.Position | Usage.Normal);
        models.add(barrierHModel);
        instances.add(new ModelInstance(barrierHModel, 0, 2f, -116)); // Top outer
        instances.add(new ModelInstance(barrierHModel, 0, 2f, 116));  // Bottom outer

        // INNER BARRIERS
        Model innerBarrierModel = modelBuilder.createBox(3f, 4f, 180f,
            new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
            Usage.Position | Usage.Normal);
        models.add(innerBarrierModel);
        instances.add(new ModelInstance(innerBarrierModel, -54, 2f, 0));  // Left inner
        instances.add(new ModelInstance(innerBarrierModel, 54, 2f, 0));   // Right inner

        Model innerBarrierHModel = modelBuilder.createBox(114f, 4f, 3f,
            new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
            Usage.Position | Usage.Normal);
        models.add(innerBarrierHModel);
        instances.add(new ModelInstance(innerBarrierHModel, 0, 2f, -84));  // Top inner
        instances.add(new ModelInstance(innerBarrierHModel, 0, 2f, 84));   // Bottom inner

        // Decorative elements - trees outside track
        Model treeModel = modelBuilder.createCylinder(3f, 12f, 3f, 8,
            new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.5f, 0.1f, 1f))),
            Usage.Position | Usage.Normal);
        models.add(treeModel);

        // Trees around outer edge
        for (int i = -100; i <= 100; i += 40) {
            instances.add(new ModelInstance(treeModel, -120, 6, i));
            instances.add(new ModelInstance(treeModel, 120, 6, i));
        }
        for (int i = -100; i <= 100; i += 40) {
            instances.add(new ModelInstance(treeModel, i, 6, -140));
            instances.add(new ModelInstance(treeModel, i, 6, 140));
        }
    }

    private void createKart() {
        ModelBuilder modelBuilder = new ModelBuilder();

        // Kart body
        Model kartModel = modelBuilder.createBox(2f, 1f, 4f,
            new Material(ColorAttribute.createDiffuse(Color.RED)),
            Usage.Position | Usage.Normal);
        models.add(kartModel);
        kartInstance = new ModelInstance(kartModel);
        kartInstance.transform.setToTranslation(-70, 1, -80);

        kart = new Kart(kartInstance);
    }

    private void checkStartLineCrossing() {
        Vector3 kartPos = kart.getPosition();
        float kartX = kartPos.x;
        float kartZ = kartPos.z;

        // Check if kart is near the start line position (left side of track)
        if (kartX >= -75f && kartX <= -65f && Math.abs(kartZ - startLineZ) < 5f) {
            if (!crossedStartLine) {
                crossedStartLine = true;
                roundCounter++;
            }
        } else {
            // Reset the flag when kart moves away from start line
            if (Math.abs(kartZ - startLineZ) > 10f) {
                crossedStartLine = false;
            }
        }
    }

    private void checkCollisions() {
        Vector3 kartPos = kart.getPosition();
        Vector3 originalPos = new Vector3(kartPos);

        // Outer boundaries
        float outerLeftX = -85f;
        float outerRightX = 85f;
        float outerTopZ = -115f;
        float outerBottomZ = 115f;

        // Inner boundaries
        float innerLeftX = -55f;
        float innerRightX = 55f;
        float innerTopZ = -85f;
        float innerBottomZ = 85f;

        boolean collision = false;

        // Check outer walls
        if (kartPos.x < outerLeftX) {
            kartPos.x = outerLeftX;
            collision = true;
        }
        if (kartPos.x > outerRightX) {
            kartPos.x = outerRightX;
            collision = true;
        }
        if (kartPos.z < outerTopZ) {
            kartPos.z = outerTopZ;
            collision = true;
        }
        if (kartPos.z > outerBottomZ) {
            kartPos.z = outerBottomZ;
            collision = true;
        }

        // Check inner island (rectangular bounds)
        if (kartPos.x > innerLeftX && kartPos.x < innerRightX &&
            kartPos.z > innerTopZ && kartPos.z < innerBottomZ) {

            // Push kart to nearest edge
            float distLeft = Math.abs(kartPos.x - innerLeftX);
            float distRight = Math.abs(kartPos.x - innerRightX);
            float distTop = Math.abs(kartPos.z - innerTopZ);
            float distBottom = Math.abs(kartPos.z - innerBottomZ);

            float minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

            if (minDist == distLeft) {
                kartPos.x = innerLeftX;
            } else if (minDist == distRight) {
                kartPos.x = innerRightX;
            } else if (minDist == distTop) {
                kartPos.z = innerTopZ;
            } else {
                kartPos.z = innerBottomZ;
            }
            collision = true;
        }

        if (collision) {
            kart.setPosition(kartPos);
        }
    }

    public void render() {
        kart.update(Gdx.graphics.getDeltaTime());
        checkCollisions();
        checkStartLineCrossing();
        updateCamera();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.render(kartInstance, environment);
        modelBatch.end();

        // Render UI
        spriteBatch.begin();
        String roundText = "Round: " + roundCounter;
        float textWidth = 200f;
        float x = (Gdx.graphics.getWidth() - textWidth) / 2f;
        font.draw(spriteBatch, roundText, x, Gdx.graphics.getHeight() - 30);
        spriteBatch.end();
    }

    private void updateCamera() {
        Vector3 kartPos = kart.getPosition();
        float kartRotation = kart.getRotation();

        // Calculate camera position behind kart
        float radians = (float) Math.toRadians(kartRotation);
        float camDistance = 20f;
        float camHeight = 10f;

        // Position camera behind the kart
        float camX = kartPos.x - (float) Math.sin(radians) * camDistance;
        float camZ = kartPos.z - (float) Math.cos(radians) * camDistance;

        cam.position.set(camX, kartPos.y + camHeight, camZ);
        cam.lookAt(kartPos.x, kartPos.y, kartPos.z);
        cam.up.set(0, 1, 0);
        cam.update();
    }

    public boolean shouldReturnToMenu() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    }

    public void dispose() {
        modelBatch.dispose();
        spriteBatch.dispose();
        font.dispose();
        for (Model m : models) {
            m.dispose();
        }
    }
}
