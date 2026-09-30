package com.codingisfun2831t.omegavoxel;

import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import org.lwjgl.Version;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

public final class CrashReport {
    private CrashReport() {}
    private static void appendLine(StringBuilder builder, String line) {
        builder.append(line).append(System.lineSeparator());
    }

    private static void addScreenInfo(StringBuilder sb, Screen screen) {
        appendLine(sb, "Screen class: " + screen.getClass().getName());
        appendLine(sb, "Total control count: " + screen.getDescendantCount());
    }

    public static String generateCrashReport(Game g, Throwable t) {
        StringBuilder sb = new StringBuilder();
        appendLine(sb, "--- OmegaVoxel crash report ---");
        appendLine(sb, "Version: " + Game.VERSION);
        appendLine(sb, "Java: " + System.getProperty("java.version"));
        appendLine(sb, "OS: " + System.getProperty("os.name") + " " +
                System.getProperty("os.version") + " " +
                System.getProperty("os.arch"));
        appendLine(sb, "LWJGL version: " + Version.getVersion());
        appendLine(sb, "Time: " + Instant.now());
        appendLine(sb, "Data directory: " + g.getDataDir());

        appendLine(sb, "");
        appendLine(sb, "--- Exception ---");

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        sb.append(sw);
        appendLine(sb, "");
        appendLine(sb, "--- Game State ---");

        Level lvl = g.getLevel();
        if (lvl == null) {
            appendLine(sb, "Not in game");
        } else {
            Player plr = g.getPlayer();
            appendLine(sb, "World of size " + lvl.getWidth() +
                    "x" + lvl.getHeight() + "x" + lvl.getDepth());
            appendLine(sb, "XYZ: " + plr.x + " / " + plr.y + " / " + plr.z);
            appendLine(sb, "Facing: yaw " + plr.yaw + " pitch" + plr.pitch);

            appendLine(sb, "Hotbar: "
                    + plr.hotbar[0].getId() + " " + plr.hotbar[1].getId() + " "
                    + plr.hotbar[2].getId() + " " + plr.hotbar[3].getId() + " "
                    + plr.hotbar[4].getId() + " " + plr.hotbar[5].getId() + " "
                    + plr.hotbar[6].getId() + " " + plr.hotbar[7].getId() + " "
                    + plr.hotbar[8].getId());

            appendLine(sb, "Selected slot: index "
                    + plr.selectedSlot + " block "
                    + plr.hotbar[plr.selectedSlot].getId());
        }

        appendLine(sb, "");
        appendLine(sb, "-- HUD --");
        addScreenInfo(sb, g.getHud());

        appendLine(sb, "");
        appendLine(sb, "--- Current Screen ---");
        if (g.getCurrentScreen() == null) {
            appendLine(sb, "No screen opened");
        } else {
            addScreenInfo(sb, g.getCurrentScreen());
        }

        return sb.toString();
    }
}
