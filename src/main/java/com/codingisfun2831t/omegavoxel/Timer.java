package com.codingisfun2831t.omegavoxel;

public class Timer {
    private static final double TICK_TIME = 1.0 / 20.0;

    private long lastTime;
    private double accumulator;
    private double tickTime;

    public Timer(int tps) {
        lastTime = System.nanoTime();
        tickTime = 1.0 / tps;
    }

    public int update() {
        long now = System.nanoTime();
        double delta = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        accumulator += delta;

        int ticks = 0;

        while (accumulator >= tickTime) {
            accumulator -= tickTime;
            ticks++;
        }

        return ticks;
    }

    public float getPartialTick() {
        return (float) (accumulator / tickTime);
    }
}