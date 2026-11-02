package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.OmegaVoxel;
import com.codingisfun2831t.omegavoxel.Player;
import com.codingisfun2831t.omegavoxel.Translations;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;

class DebugInfoRenderer {
    private UIRenderer r = null;

    public void useRenderer(UIRenderer r) {
        this.r = r;
    }

    private int debugY;

    private void leftString(String fmt, Object... args) {
        r.drawTextWithShadow(2, debugY, String.format(fmt, args));
        debugY += 10;
    }

    private void rightString(String fmt, Object... args) {
        String str = String.format(fmt, args);
        r.drawTextWithShadow(r.getWidth() - 2 - r.measureText(str), debugY, str);
        debugY += 10;
    }

    private void sep() {
        debugY += 12;

    }

    public void render(OmegaVoxel game) {
        debugY = 12;

        Player plr = game.getPlayer();

        sep();
        leftString(Translations.get("debug.xyz"), plr.x, plr.y, plr.z);

        String directionKey = "";
        String axisKey = "";

        if (plr.yaw >= 135 || plr.yaw < -135) {
            directionKey = "debug.north";
            axisKey = "debug.nz";
        } else if (plr.yaw >= -135 && plr.yaw < -45) {
            directionKey = "debug.east";
            axisKey = "debug.px";
        } else if (plr.yaw >= -45 && plr.yaw < 45) {
            directionKey = "debug.south";
            axisKey = "debug.pz";
        } else {
            directionKey = "debug.west";
            axisKey = "debug.nx";
        }


        leftString(Translations.get("debug.facing"), Translations.get(directionKey), Translations.get(axisKey), plr.yaw, plr.pitch);

        sep();
        leftString(Translations.get("debug.chunks"), game.getStatChunksBuilt(),
                game.getChunkCount(), game.getStatChunksRendered());
        leftString(Translations.get("debug.vertices.level"), game.getLevelVertexCount());
        leftString(Translations.get("debug.vertices.total"), game.getLastFrameVertexCount());

        debugY = 2;


        Runtime runtime = Runtime.getRuntime();

        long mb = 1024 * 1024;
        long totalMemory = runtime.totalMemory() / mb;
        long freeMemory = runtime.freeMemory() / mb;
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory() / mb;

        rightString(Translations.get("debug.mem"), usedMemory, maxMemory);
        rightString(Translations.get("debug.allocated") ,totalMemory, maxMemory);
    }
}
