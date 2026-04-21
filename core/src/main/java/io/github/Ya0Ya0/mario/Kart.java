package io.github.Ya0Ya0.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class Kart {
    private ModelInstance instance;
    private Vector3 position;
    private float velocity = 0;
    private float yRotation = 0;

    private final float acceleration = 30f;
    private final float deceleration = 15f;
    private final float maxVelocity = 40f;
    private final float turnSpeed = 120f;

    public Kart(ModelInstance instance) {
        this.instance = instance;
        this.position = new Vector3();
        instance.transform.getTranslation(position);
    }

    public void update(float deltaTime) {
        handleInput(deltaTime);

        // Calculate forward direction based on Y rotation
        float radians = (float) Math.toRadians(yRotation);
        Vector3 direction = new Vector3(
            (float) Math.sin(radians),
            0,
            (float) Math.cos(radians)
        );

        // Move position
        position.x += direction.x * velocity * deltaTime;
        position.z += direction.z * velocity * deltaTime;

        // Update transform with new position and rotation
        instance.transform.idt();
        instance.transform.translate(position);
        instance.transform.rotate(Vector3.Y, yRotation);

        // Apply friction
        float currentFriction = 8f;
        if (velocity > 0) {
            velocity -= currentFriction * deltaTime;
            if (velocity < 0) velocity = 0;
        }
        if (velocity < 0) {
            velocity += currentFriction * deltaTime;
            if (velocity > 0) velocity = 0;
        }
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getRotation() {
        return yRotation;
    }

    public void setPosition(Vector3 newPosition) {
        position.set(newPosition);
    }

    private void handleInput(float deltaTime) {
        // Acceleration
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity += acceleration * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity -= deceleration * deltaTime;
        }

        // Clamp velocity
        if (velocity > maxVelocity) velocity = maxVelocity;
        if (velocity < -maxVelocity / 2) velocity = -maxVelocity / 2;

        // Turning - only when moving
        if (Math.abs(velocity) > 0.5f) {
            float turning = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                turning = turnSpeed * deltaTime;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                turning = -turnSpeed * deltaTime;
            }

            // Reverse turning when going backwards
            if (velocity < 0) turning = -turning;

            // Apply turn with speed factor
            float turnFactor = Math.min(1.0f, Math.abs(velocity) / 15f);
            yRotation += turning * turnFactor;

            // Normalize rotation
            while (yRotation > 360) yRotation -= 360;
            while (yRotation < 0) yRotation += 360;
        }
    }
}
