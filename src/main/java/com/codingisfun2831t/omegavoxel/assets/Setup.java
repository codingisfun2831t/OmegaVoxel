package com.codingisfun2831t.omegavoxel.assets;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// this code SUCCCKSSSSS man

/**
 * Dialog to extract assets from the Beta JAR.
 */
public class Setup {
    private record Progress(int progress, String status) {}

    private static final Path assets = Path.of("assets");
    private static final Path jar = assets.resolve("b1.7.3.jar");
    private static final String JAR_LOCATION = "https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar";
    private static final byte[] JAR_MD5HASH = {
            (byte)175, (byte)31, (byte)160, (byte)75,
            (byte)128, (byte)6, (byte)211, (byte)239,
            (byte)120, (byte)199, (byte)226, (byte)79,
            (byte)141, (byte)228, (byte)170, (byte)86,
            (byte)244, (byte)57, (byte)167, (byte)77,
            (byte)127, (byte)49, (byte)72, (byte)39,
            (byte)82, (byte)144, (byte)98, (byte)213,
            (byte)186, (byte)182, (byte)219, (byte)76
    };
    private static final String[] TO_EXTRACT = {
            "terrain.png",
            "font/default.png",
            "gui/gui.png"
    };

    private JDialog dialog;
    private JPanel pages;
    private JPanel jarPage;
    private JLabel jarPageLabel;
    private JProgressBar jarPageDownloadProgress;
    private JLabel jarPageDownloadStatus;
    private JProgressBar extractDownloadProgress;
    private JLabel extractDownloadStatus;
    private JButton continueButton;
    private JPanel extractPage;
    private CardLayout layout;

    public Setup() {
        dialog = new JDialog();
        dialog.setTitle("Minecraft Clone Setup");
        dialog.setSize(500, 200);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);

        layout = new CardLayout();
        pages = new JPanel(layout);

        jarPage = new JPanel();
        jarPage.setLayout(new BoxLayout(jarPage, BoxLayout.Y_AXIS));
        jarPage.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jarPageLabel = new JLabel("Getting the Beta 1.7.3 JAR");
        jarPageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jarPageLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, jarPageLabel.getPreferredSize().height));
        jarPage.add(jarPageLabel);
        jarPage.add(Box.createVerticalStrut(10));

        jarPageDownloadProgress = new JProgressBar();
        jarPageDownloadProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, jarPageDownloadProgress.getPreferredSize().height));
        jarPage.add(jarPageDownloadProgress);
        jarPage.add(Box.createVerticalStrut(10));

        jarPageDownloadStatus = new JLabel("...");
        jarPageDownloadStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, jarPageDownloadStatus.getPreferredSize().height));
        jarPage.add(jarPageDownloadStatus);
        jarPage.add(Box.createVerticalStrut(10));


        continueButton = new JButton("Continue");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setEnabled(false);
        jarPage.add(continueButton);

        extractPage = new JPanel();
        extractPage.setLayout(new BoxLayout(extractPage, BoxLayout.Y_AXIS));
        extractPage.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel extractLabel = new JLabel("Extracting textures...");
        extractLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, extractLabel.getPreferredSize().height));
        extractLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        extractPage.add(extractLabel);
        extractPage.add(Box.createVerticalStrut(10));

        extractDownloadProgress = new JProgressBar();
        extractDownloadProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, extractDownloadProgress.getPreferredSize().height));
        extractPage.add(extractDownloadProgress);
        extractPage.add(Box.createVerticalStrut(10));

        extractDownloadStatus = new JLabel("...");
        extractDownloadStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, extractDownloadStatus.getPreferredSize().height));
        extractPage.add(extractDownloadStatus);
        extractPage.add(Box.createVerticalStrut(10));

        pages.add(jarPage, "select");
        pages.add(extractPage, "extract");

        continueButton.addActionListener(e -> {
            layout.show(pages, "extract");
            extractAssets();
        });

        dialog.add(pages);
    }

    private void downloadJar() {
        SwingWorker<Void, Progress> worker = new SwingWorker<Void, Progress>() {
            @Override
            protected Void doInBackground() throws Exception {
                URL url = URI.create(JAR_LOCATION).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int total = connection.getContentLength();

                Path path = jar;
                try (InputStream in = connection.getInputStream();
                     OutputStream out = Files.newOutputStream(path)) {

                    byte[] buffer = new byte[8192];
                    int read;
                    long downloaded = 0;

                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        downloaded += read;

                        if (total > 0) {
                            int percentDone = (int) ((downloaded * 100L) / total);
                            publish(new Progress(percentDone, String.format("Downloading %.2fMB/%.2fMB (%d%%)",
                                    (float)downloaded / 1_000_000f,
                                    (float)total / 1_000_000f, percentDone)));
                        }
                    }
                }

                byte[] hash = getHash(path);
                if (!Arrays.equals(hash, JAR_MD5HASH)) {
                    String stringHash = HexFormat.of().formatHex(hash);
                    String stringHashCorrect = HexFormat.of().formatHex(JAR_MD5HASH);
                    throw new Exception("JAR hash didnt match up.\nOriginal: " + stringHash + "\nCorrect: " + stringHashCorrect);
                }

                return null;
            }

            private byte[] getHash(Path file) throws IOException, NoSuchAlgorithmException {
                MessageDigest md5 = MessageDigest.getInstance("SHA256");

                long total = Files.size(file);
                long processed = 0;

                try (InputStream in = Files.newInputStream(file)) {
                    byte[] buffer = new byte[1024 * 1024];
                    int read;

                    while ((read = in.read(buffer)) != -1) {
                        md5.update(buffer, 0, read);
                        processed += read;

                        int percentDone = (int) ((processed * 100L) / total);
                        publish(new Progress(percentDone, String.format("Hashing %.2fMB/%.2fMB (%d%%)",
                                (float)processed / 1_000_000f,
                                (float)total / 1_000_000f, percentDone)));
                    }
                }

                return md5.digest();
            }

            @Override
            protected void process(java.util.List<Progress> values) {
                Progress progress = values.getLast();
                jarPageDownloadProgress.setValue(progress.progress);
                jarPageDownloadStatus.setText(progress.status);
            }

            @Override
            protected void done() {
                try {
                    get();
                    jarPageDownloadStatus.setText("JAR downloaded!");
                    jarPageDownloadProgress.setValue(0);
                    jarPageDownloadProgress.setEnabled(false);
                    continueButton.setEnabled(true);
                } catch (ExecutionException | InterruptedException e) {
                    Throwable t = e.getCause();
                    if (t == null) {
                        jarPageDownloadStatus.setText("Error.");
                    } else {
                        jarPageDownloadStatus.setText("Error when downloading, please manually enter JAR: \n" + t.getMessage());
                    }

                    JTextArea details = new JTextArea("Error when trying to download from internet: " + e.toString());
                    details.setEditable(false);
                    details.setLineWrap(true);
                    details.setWrapStyleWord(true);

                    JScrollPane scrollPane = new JScrollPane(details);
                    scrollPane.setPreferredSize(new Dimension(500, 200));

                    JOptionPane.showMessageDialog(
                            dialog,
                            scrollPane,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    jarPageDownloadProgress.setValue(0);
                    jarPageDownloadProgress.setEnabled(false);
                }
            }
        };

        worker.execute();
    }

    private void extractAssets() {
        SwingWorker<Void, Progress> worker = new SwingWorker<Void, Progress>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (ZipFile zipFile = new ZipFile(jar.toFile())) {
                    String destDirPath = assets.toFile().getCanonicalPath();

                    for (String relativePath : TO_EXTRACT) {
                        ZipEntry entry = zipFile.getEntry(relativePath);

                        if (entry == null) {
                            throw new Exception("File " + relativePath + " not found!");
                        }

                        File newFile = new File(assets.toFile(), entry.getName());
                        String newFilePath = newFile.getCanonicalPath();

                        if (!newFilePath.startsWith(destDirPath + File.separator)) {
                            throw new IOException("Entry is outside of the target dir: " + entry.getName());
                        }

                        File parent = newFile.getParentFile();
                        if (parent != null && !parent.exists()) {
                            parent.mkdirs();
                        }

                        try (InputStream is = zipFile.getInputStream(entry);
                             FileOutputStream fos = new FileOutputStream(newFile)) {
                            byte[] buffer = new byte[4096];
                            int len;
                            long total = entry.getSize(); // Use long to handle files > 2GB safely
                            long processed = 0;

                            while ((len = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                                processed += len;

                                int percentDone = (total > 0) ? (int) (((double) processed / total) * 100) : 0;

                                publish(new Progress(percentDone, String.format("Extract %s %.2fMB/%.2fMB (%d%%)",
                                        relativePath,
                                        (float)processed / 1_000_000f,
                                        (float)total / 1_000_000f, percentDone)));
                            }
                        }
                        System.out.println("Extracted: " + relativePath);
                    }
                }

                return null;
            }


            @Override
            protected void process(java.util.List<Progress> values) {
                Progress progress = values.getLast();
                extractDownloadProgress.setValue(progress.progress);
                extractDownloadStatus.setText(progress.status);
            }

            @Override
            protected void done() {
                try {
                    get();
                    dialog.dispose();
                } catch (ExecutionException | InterruptedException e) {
                    Throwable t = e.getCause();
                    if (t == null) {
                        extractDownloadStatus.setText("Error.");
                    } else {
                        extractDownloadStatus.setText("Error when extract.");
                    }

                    JTextArea details = new JTextArea("Error when trying to extract: " + e.toString());
                    details.setEditable(false);
                    details.setLineWrap(true);
                    details.setWrapStyleWord(true);

                    JScrollPane scrollPane = new JScrollPane(details);
                    scrollPane.setPreferredSize(new Dimension(500, 200));

                    JOptionPane.showMessageDialog(
                            dialog,
                            scrollPane,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    extractDownloadProgress.setValue(0);
                    extractDownloadProgress.setEnabled(false);
                }
            }
        };

        worker.execute();
    }

    /**
     * Start the dialog.
     */
    public boolean run() {
        try {
            if (!Files.isDirectory(assets)) {
                Files.createDirectory(assets);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to create assets directory.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (Files.isRegularFile(jar)) {
            layout.show(pages, "extract");
            extractAssets();
        } else {
            layout.show(pages, "select");
            downloadJar();
        }

        dialog.setVisible(true);

        return true;
    }
}
