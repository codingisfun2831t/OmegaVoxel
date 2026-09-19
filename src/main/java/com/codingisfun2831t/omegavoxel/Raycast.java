package com.codingisfun2831t.omegavoxel;

import com.codingisfun2831t.omegavoxel.level.Level;
import org.joml.Vector3f;

public class Raycast {
    private Raycast() {}

    public static void raycast(Vector3f origin, Vector3f direction, float maxDistance, Level level, HitResult out) {
        out.hit = false;

        int mapX = (int) Math.floor(origin.x);
        int mapY = (int) Math.floor(origin.y);
        int mapZ = (int) Math.floor(origin.z);

        float deltaDistX = direction.x == 0 ? Float.POSITIVE_INFINITY : Math.abs(1.0f / direction.x);
        float deltaDistY = direction.y == 0 ? Float.POSITIVE_INFINITY : Math.abs(1.0f / direction.y);
        float deltaDistZ = direction.z == 0 ? Float.POSITIVE_INFINITY : Math.abs(1.0f / direction.z);

        int stepX, stepY, stepZ;
        float sideDistX, sideDistY, sideDistZ;

        if (direction.x < 0) {
            stepX = -1;
            sideDistX = (origin.x - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0f - origin.x) * deltaDistX;
        }

        if (direction.y < 0) {
            stepY = -1;
            sideDistY = (origin.y - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0f - origin.y) * deltaDistY;
        }

        if (direction.z < 0) {
            stepZ = -1;
            sideDistZ = (origin.z - mapZ) * deltaDistZ;
        } else {
            stepZ = 1;
            sideDistZ = (mapZ + 1.0f - origin.z) * deltaDistZ;
        }

        int lastSide = 0;
        float distanceTraveled = 0;

        while (distanceTraveled < maxDistance) {
            if (sideDistX < sideDistY && sideDistX < sideDistZ) {
                distanceTraveled = sideDistX;
                sideDistX += deltaDistX;
                mapX += stepX;
                lastSide = 0;
            } else if (sideDistY < sideDistZ) {
                distanceTraveled = sideDistY;
                sideDistY += deltaDistY;
                mapY += stepY;
                lastSide = 1;
            } else {
                distanceTraveled = sideDistZ;
                sideDistZ += deltaDistZ;
                mapZ += stepZ;
                lastSide = 2;
            }

            if (distanceTraveled > maxDistance) break;

            if (level.getBlockID(mapX, mapY, mapZ) != 0) {
                out.hit = true;
                out.blockPos.set(mapX, mapY, mapZ);

                if (lastSide == 0) {
                    out.hitFace = stepX > 0 ? Face.LEFT : Face.RIGHT;
                } else if (lastSide == 1) {
                    out.hitFace = stepY > 0 ? Face.BOTTOM : Face.TOP;
                } else {
                    out.hitFace = stepZ > 0 ? Face.BACK : Face.FRONT;
                }

                return;
            }
        }
    }
}