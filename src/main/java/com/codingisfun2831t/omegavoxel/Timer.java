package com.codingisfun2831t.omegavoxel;

/**
 * Time keeping class.
 */
public class Timer {
    private long lastTime; /// time since last update
    private double accumulator; // accumulator for time - ticks
    private double tickTime; // how long for a single tick

    public Timer(int tps) {
        lastTime = System.nanoTime();
        tickTime = 1.0 / tps;
    }

    /**
     * Update the clock, returns number of ticks needed to simulate.
     */
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

    /**
     * Returns a fraction of tick time representing the time that can't be considered a tick yet.
     * @return
     */
    public float getPartialTick() {
        return (float) (accumulator / tickTime);
    }
}