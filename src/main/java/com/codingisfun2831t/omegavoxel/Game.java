package com.codingisfun2831t.omegavoxel;

import com.codingisfun2831t.omegavoxel.assets.Assets;
import com.codingisfun2831t.omegavoxel.assets.Setup;
import com.codingisfun2831t.omegavoxel.assets.Textures;
import com.codingisfun2831t.omegavoxel.level.Block;
import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.rendering.*;
import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;
import com.codingisfun2831t.omegavoxel.ui.screens.HUD;
import com.codingisfun2831t.omegavoxel.ui.screens.MainMenuScreen;
import com.codingisfun2831t.omegavoxel.ui.screens.PauseMenu;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

public class Game {
    public static String VERSION;
    public static final String TITLE;

    static {
        VERSION = "Unknown";

        String path = "/META-INF/maven/com.codingisfun2831t.omegavoxel/OmegaVoxel/pom.properties";

        try (InputStream stream = Game.class.getResourceAsStream(path)) {
            if (stream != null) {
                Properties props = new Properties();
                props.load(stream);

                String version = props.getProperty("version");
                if (version != null) {
                    VERSION = "C" + version;
                }
            }
        } catch (IOException e) {
            System.out.println("Error getting version:");
            e.printStackTrace();
        }

        TITLE = "OmegaVoxel " + VERSION;
    }

    private long window;
    private int width;
    private int height;
    private Renderer r;
    private float mouseX;
    private float mouseY;
    private Camera cam;
    private Timer moveTimer;
    private float deltaX = 0.0f;
    private float deltaY = 0.0f;
    private double lastX = 0.0f;
    private double lastY = 0.0f;
    private boolean firstMouse = true;
    private Assets assets;
    private Textures textures;
    private Level level;
    private LevelRenderer levelRenderer;
    private int frames;
    private long _lastTime = System.currentTimeMillis();
    private FontRenderer text;
    private ScaledResolution res;
    private HitResult hitResult = new HitResult();
    private HUD hud = new HUD();
    private Screen currentScreen = null;
    private UIRenderer uiRenderer;
    private Player player;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void updateUiRes() {
        res = new ScaledResolution(width, height, 2);
        resizeScreen(hud);
    }

    public void run() {
        if (!(new Setup().run())) {
            return;
        }

        mainLoop();
    }

    private void resizeScreen(Screen screen) {
        screen.getRoot().setBounds(res.getRectangle());

        Game me = this;
        screen.fixLayout(new LayoutContext() {
            @Override
            public int measureText(String text) {
                return me.text.measureText(text);
            }
        });
    }

    public void navigateTo(Screen screen) {
        currentScreen = screen;
        if (screen == null) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            return;
        }


        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);

        currentScreen.game = this;
        currentScreen.init();
        resizeScreen(screen);
    }

    private boolean unpaused() {
        if (level == null) return false;

        return currentScreen == null || !currentScreen.pausesGame();
    }

    private static final File levelDat = new File("level.dat");

    public void saveLevel() {
        if (level == null) return;

        try {
            CompoundTag save = new CompoundTag();

            save.put("Player", player.getNBT());

            save.putInt("Width", level.getWidth());
            save.putInt("Height", level.getHeight());
            save.putInt("Depth", level.getDepth());

            save.putByteArray("Blocks", level.getBlocks());
            NBTUtil.write(save, levelDat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        GLFW.glfwSetWindowShouldClose(window, true);
    }

    public void play() {
        if (levelDat.exists()) {
            try {
                NamedTag named = NBTUtil.read(levelDat);
                if (!(named.getTag() instanceof CompoundTag root)) {
                    throw new RuntimeException("Root tag is not compound.");
                }

                ensureNBTValue(root, "Player");
                if (!(root.get("Player") instanceof CompoundTag playerTag)) {
                    throw new RuntimeException("Player tag is not compound.");
                }

                ensureNBTValue(root, "Width");
                ensureNBTValue(root, "Height");
                ensureNBTValue(root, "Depth");
                ensureNBTValue(root, "Blocks");

                this.level = new Level(root.getInt("Width"),
                        root.getInt("Height"), root.getInt("Depth"), root.getByteArray("Blocks"));
                this.player.loadFromNBT(playerTag);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.level = new Level(128, 128, 128);
            this.player.resetPos(level);

            saveLevel();
        }

        this.levelRenderer.setLevel(level);
        this.navigateTo(null);
    }

    public void quitLevel() {
        this.level = null;
        this.levelRenderer.setLevel(null);
        this.navigateTo(new MainMenuScreen());
        System.gc();
    }

    private void mainLoop() {
        GLFW.glfwInit();

        GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, 24);
        window = GLFW.glfwCreateWindow(width, height, TITLE, 0, 0);

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        this.r = Renderer.getInstance();
        this.cam = new Camera();
        this.moveTimer = new Timer(60);
        this.assets = new Assets();
        this.textures = new Textures(assets);
        this.r.useTextures(textures);
        this.text = new FontRenderer(r, textures);
        this.uiRenderer = new UIRenderer(r, text);
        this.levelRenderer = new LevelRenderer(r);
        this.player = new Player();

        this.hud.game = this;
        this.hud.init();

        updateUiRes();
        this.navigateTo(new MainMenuScreen());

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;

            GL11.glViewport(0, 0, width, height);
            updateUiRes();

            resizeScreen(hud);
            if (currentScreen != null) resizeScreen(currentScreen);
        });

        mouseX = 0;
        mouseY = 0;

        GLFW.glfwSetCursorPosCallback(window, (window, x, y) -> {
            if (firstMouse) {
                lastX = x;
                lastY = y;
                firstMouse = false;
            }

            deltaX = (float) (x - lastX);
            deltaY = (float) (lastY - y);

            lastX = x;
            lastY = y;

            mouseX = (float) x / res.scale;
            mouseY = (float) y / res.scale;

            if (currentScreen != null) {
                currentScreen.mouseMove((int) mouseX , (int) mouseY);
            }

            if (unpaused()) {
                player.turn(deltaX, deltaY);
            }
        });

        GLFW.glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            // Check if the Escape key was pressed
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS && level != null) {
                if (currentScreen != null && currentScreen.pausesGame()) {
                    navigateTo(null);
                }
                else {
                    navigateTo(new PauseMenu());
                }
            }
        });

        GLFW.glfwSetMouseButtonCallback(window, (windowHandle, button, action, mods) -> {
            if (currentScreen != null) {
                currentScreen.mouseClick((int) mouseX, (int) mouseY, button);
                return;
            }

            if (action == GLFW.GLFW_PRESS && level != null) {
                switch (button) {
                    case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                        level.setBlockID(
                                hitResult.blockPos.x,
                                hitResult.blockPos.y,
                                hitResult.blockPos.z,
                                (byte) 0
                        );
                        break;

                    case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                        int fx = hitResult.hitFace.getX(hitResult.blockPos.x);
                        int fy = hitResult.hitFace.getY(hitResult.blockPos.y);
                        int fz = hitResult.hitFace.getZ(hitResult.blockPos.z);

                        if (level.getBlockID(fx, fy, fz) != 0) break;
                        level.setBlock(fx, fy, fz, Block.COBBLESTONE);
                        break;
                }
            }
        });

        GLFW.glfwShowWindow(window);
        GLFW.glfwFocusWindow(window);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glFrontFace(GL11.GL_CCW);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        while (!GLFW.glfwWindowShouldClose(window)) {
            Chunk.updatesThisFrame = 0;

            GL11.glClearColor(0.47f, 0.63f, 0.80f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            int ticks = moveTimer.update();
            for (int i = 0; i < ticks; i++) {
                if (unpaused()) {
                    player.tick(level, window);
                }
            }

            if (unpaused()) {
                float t = moveTimer.getPartialTick();

                float x = player.xo + (player.x - player.xo) * t;
                float y = player.yo + (player.y - player.yo) * t;
                float z = player.zo + (player.z - player.zo) * t;

                cam.position = new Vector3f(x, y, z);
                cam.yaw = player.yaw;
                cam.pitch = player.pitch;
            }

            if (level != null) {
                cam.applyProjection(width, height);
                cam.applyViewMatrix();

                Raycast.raycast(cam.position, cam.getDirection(), 5, level, hitResult);

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                levelRenderer.render(cam);
                levelRenderer.drawSelectionBox(hitResult);

                r.flush();
            }

            this.uiRenderer.begin(res);
            if (level != null) hud.render(uiRenderer);
            if (currentScreen != null) currentScreen.render(uiRenderer);
            this.uiRenderer.end();

            r.flush();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();

            frames++;

            long currentTime = System.currentTimeMillis();
            if (currentTime >= this._lastTime + 1000L) {
                this._lastTime = currentTime;
                hud.setDebug(String.format("%s %d fps, %d chunk updates", TITLE, frames, Chunk.totalUpdates));
                this.frames = 0;
                Chunk.totalUpdates = 0;
            }
        }


        saveLevel();
        r.destroy();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new Game(800, 600).run();
    }

    public static void ensureNBTValue(CompoundTag tag, String key) {
        if (!tag.containsKey(key)) {
            throw new RuntimeException("Key \"" + key + "\" doesn't exist.");
        }
    }
}