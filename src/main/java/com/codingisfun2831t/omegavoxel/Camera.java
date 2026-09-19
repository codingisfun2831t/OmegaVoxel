package com.codingisfun2831t.omegavoxel;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Camera {
    public Vector3f position;
    public float yaw;
    public float pitch;

    public Matrix4f projection;
    public Matrix4f view;

    private float[] matrixBuf;

    private Matrix4f projViewMatrix = new Matrix4f();
    private FrustumIntersection frustum = new FrustumIntersection();

    public Camera() {
        this.position = new Vector3f(0, 0, 0);
        this.yaw = 0;
        this.pitch = 0;
        this.projection = new Matrix4f();
        this.view = new Matrix4f();
        this.matrixBuf = new float[16];
    }

    public void applyProjection(int width, int height) {
        if (height == 0) height = 1;

        float aspect = (float) width / (float) height;

        projection.identity()
                .perspective(
                        (float) Math.toRadians(70.0f),
                        aspect,
                        0.05f,
                        1000.0f
                );

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadMatrixf(projection.get(this.matrixBuf));

        projViewMatrix.set(projection).mul(view);
        frustum.set(projViewMatrix);
    }

    public boolean boxInFrustum(float x0, float y0, float z0, float x1, float y1, float z1) {
        return frustum.testAab(x0, y0, z0, x1, y1, z1);
    }

    public void applyViewMatrix() {
        view.identity()
                .rotateX((float) Math.toRadians(-pitch))
                .rotateY((float) Math.toRadians(-yaw))
                .translate(-position.x, -position.y, -position.z);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadMatrixf(view.get(this.matrixBuf));
    }

    public void move(long window) {
        float speed = 0.2F;

        float yawRad = (float) Math.toRadians(yaw);
        float forwardX = -(float) Math.sin(yawRad);
        float forwardZ = -(float) Math.cos(yawRad);

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.x += forwardX * speed;
            position.z += forwardZ * speed;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.x -= forwardX * speed;
            position.z -= forwardZ * speed;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            position.x += forwardZ * speed;
            position.z -= forwardX * speed;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            position.x -= forwardZ * speed;
            position.z += forwardX * speed;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            position.y += speed;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
                GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) {
            position.y -= speed;
        }
    }



    public Vector3f getDirection() {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        return new Vector3f(
                -(float) Math.sin(yawRad) * (float) Math.cos(pitchRad),
                (float) Math.sin(pitchRad),
                -(float) Math.cos(yawRad) * (float) Math.cos(pitchRad)
        );
    }
}