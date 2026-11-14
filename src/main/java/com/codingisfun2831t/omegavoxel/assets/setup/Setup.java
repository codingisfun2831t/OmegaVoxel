package com.codingisfun2831t.omegavoxel.assets.setup;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipFile;

public class Setup {
    private static final Logger logger = LoggerFactory.getLogger(Setup.class);
    private static Gson GSON = new Gson();
    private static HttpClient HTTP = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

    private Path assets;
    private Path mcFolder;

    private Path versionsManifest;

    private static String MIT_LICENSE = "Copyright 2026 codingisfun2831t\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of\n" +
            "this software and associated documentation files (the “Software”), to deal in\n" +
            "the Software without restriction, including without limitation the rights to\n" +
            "use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies\n" +
            "of the Software, and to permit persons to whom the Software is furnished to do\n" +
            "so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
            "SOFTWARE.\n";

    private static String assetsREADME =
            "THIS FOLDER CONTAINS ASSETS EXTRACTED FROM THE OFFICAL MINECRAFT JARS!\n" +
                    "It is a violation of Minecraft's EULA to share these assets anywhere.\n" +
                    "These assets are not made by anyone on OmegaVoxel.\n\n" +
                    "MIT License\n\n" + MIT_LICENSE;

    private static String mcFolderREADME =
            "THIS FOLDER CONTAINS OFFICAL MINECRAFT JARS AND MANIFESTS!!\n" +
                    "It is a violation of Minecraft's EULA to share these jars anywhere.\n\n" +
                    "MIT License\n\n" + MIT_LICENSE;

    public Setup(Path dataDir) throws IOException {
        assets = dataDir.resolve("assets");
        mcFolder = dataDir.resolve("mc");
        versionsManifest = mcFolder.resolve("versions.json");
    }

    private void setupDirectories() throws IOException {
        boolean anyCreated = false;

        if (!Files.isDirectory(assets)) {
            Files.createDirectories(assets);
            anyCreated = true;
        }

        if (!Files.isDirectory(mcFolder)) {
            Files.createDirectories(mcFolder);
            anyCreated = true;
        }

        Path readmeAssets = assets.resolve("README.txt");
        Path readmeMcFolder = mcFolder.resolve("README.txt");

        if (!Files.isRegularFile(readmeAssets)) {
            Files.writeString(readmeAssets, assetsREADME);
            anyCreated = true;
        }

        if (!Files.isRegularFile(readmeMcFolder)) {
            Files.writeString(readmeMcFolder, mcFolderREADME);
            anyCreated = true;
        }

        if (anyCreated) {
            logger.info("Assets/MC folders have been fully created.");
        }
    }

    private JDialog dialog;

    private static SpritesheetAssetExtractor.SpritesheetPart terrain(String file, int x, int y) {
        return new SpritesheetAssetExtractor.SpritesheetPart(file, x * 16, y * 16, 16, 16);
    }

    //(1).register(1, "stone");
    //(0, 3, 2).register(2, "grass");
    //(2).register(3, "dirt");
    //(16).register(4, "cobblestone");
    //(21, 20, 21).register(17, "log");
    //(4).register(5, "planks");
    //(18).register(12, "sand");
    //(19).register(13, "gravel");
    //(17).register(7, "bedrock");
    //(49).register(20, "glass");
    private static List<AssetExtractor> extractors = List.of(
            new FilesAssetExtractor("b1.7.3", List.of("terrain.png",
                    "font/default.png",
                    "gui/gui.png",
                    "mob/char.png")),
            new SpritesheetAssetExtractor("b1.7.3", "terrain.png", "block", List.of(
                    terrain("stone", 1, 0),
                    terrain("grass_top", 0, 0),
                    terrain("grass_side", 3, 0),
                    terrain("dirt", 2, 0),
                    terrain("cobblestone", 0, 1),
                    terrain("log_top", 5, 1),
                    terrain("log_side", 4, 1),
                    terrain("planks", 4, 0),
                    terrain("sand", 2, 1),
                    terrain("gravel", 3, 1),
                    terrain("bedrock", 1, 1),
                    terrain("glass", 1, 3)
            ))
    );

    private static URI versionManifestURL;

    static {
        try {
            versionManifestURL = new URI("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject versionsJSON;
    private JsonArray versionsArray;

    public boolean run() throws IOException, InterruptedException, URISyntaxException, NoSuchAlgorithmException {
        setupDirectories();

        Set<String> neededVersions = new HashSet<>();

        for (AssetExtractor extractor : extractors) {
            if (!extractor.shouldExtract(assets)) continue;
            neededVersions.add(extractor.jarVersion);
        }

        boolean hasVersions = Files.isRegularFile(versionsManifest);
        if (hasVersions) loadVersions();

        Map<String, ZipFile> versionJars = new HashMap<>();

        try {
            for (String ver : neededVersions) {
                Path jar = mcFolder.resolve(ver + ".jar");
                if (!Files.isRegularFile(jar)) {
                    if (!hasVersions) {
                        downloadVersions();
                        loadVersions();
                        hasVersions = true;
                    }

                    downloadVersion(ver);
                }

                versionJars.put(ver, new ZipFile(jar.toFile()));
            }

            for (AssetExtractor extractor : extractors) {
                if (!extractor.shouldExtract(assets)) continue;
                extractor.extract(assets, versionJars.get(extractor.jarVersion));
            }

        } finally {
            for (ZipFile file : versionJars.values()) {
                file.close();
            }
        }


//        dialog = new JDialog();
//        dialog.setTitle("Minecraft Clone Setup");
//        dialog.setSize(500, 200);
//        dialog.setModal(true);
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        dialog.setLocationRelativeTo(null);

        return true;
    }

    private void downloadVersions() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(versionManifestURL).GET().build();
        HttpResponse<Path> response = HTTP.send(request, HttpResponse.BodyHandlers.ofFile(versionsManifest));
    }

    private void loadVersions() throws IOException {
        versionsJSON = JsonParser.parseString(Files.readString(versionsManifest)).getAsJsonObject();
        versionsArray = versionsJSON.getAsJsonArray("versions");
    }

    private JsonObject getVersionJson(String id) {
        for (JsonElement elem : versionsArray.asList()) {
            if (!(elem instanceof JsonObject obj)) continue;

            String objID = obj.get("id").getAsString();

            if (objID.equals(id)) return obj;
        }

        return null;
    }

    public static String getSha1(Path path) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        try (InputStream is = Files.newInputStream(path);
             DigestInputStream dis = new DigestInputStream(is, digest)) {
            dis.transferTo(OutputStream.nullOutputStream());
        }

        return HexFormat.of().formatHex(digest.digest());
    }

    private void downloadVersion(String id) throws URISyntaxException, IOException, InterruptedException, NoSuchAlgorithmException {
        JsonObject ver = getVersionJson(id);
        if (ver == null) {
            throw new RuntimeException("Version " + id + " not found in manifest.");
        }

        Path versionJson = mcFolder.resolve(id + ".json");
        JsonObject client;
        if (!Files.isRegularFile(versionJson)) {
            URI url = new URI(ver.get("url").getAsString());

            HttpRequest request = HttpRequest.newBuilder(url).GET().build();
            HttpResponse<Path> response = HTTP.send(request, HttpResponse.BodyHandlers.ofFile(versionJson));

            String versionComputedSha1 = getSha1(response.body());
            String versionExpectedSha1 = ver.get("sha1").getAsString();
            if (!versionComputedSha1.equals(versionExpectedSha1)) {
                throw new RuntimeException("SHA1 of version " + id + " JSON is invalid!\n" +
                        "Expected: " + versionExpectedSha1 + "\n" +
                        "Got: " + versionComputedSha1);
            }
        }

        JsonObject json = JsonParser.parseString(Files.readString(versionJson)).getAsJsonObject();
        client = json.getAsJsonObject("downloads").getAsJsonObject("client");

        String jarExpectedSha1 = client.get("sha1").getAsString();
        URI jarURI = new URI(client.get("url").getAsString());

        Path versionJar = mcFolder.resolve(id + ".jar");

        HttpRequest request = HttpRequest.newBuilder(jarURI).GET().build();
        HttpResponse<Path> response = HTTP.send(request, HttpResponse.BodyHandlers.ofFile(versionJar));

        String jarComputedSha1 = getSha1(versionJar);
        if (!jarComputedSha1.equals(jarExpectedSha1)) {
            throw new RuntimeException("SHA1 of version " + id + " jar is invalid!\n" +
                    "Expected: " + jarExpectedSha1 + "\n" +
                    "Got: " + jarComputedSha1);
        }

        logger.info("Version {} downloaded!", id);
    }
}
