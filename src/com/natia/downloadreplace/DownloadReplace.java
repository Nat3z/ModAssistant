package com.natia.downloadreplace;

import java.awt.Dimension;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class DownloadReplace {
    public static boolean deletePrevious = false;
    public static String newName = "";
    public static void main(String[] args) {
        // get the downloadURI, path, fileName, and matchesWith from the args
        String downloadURI = args[0];
        File path = new File(args[1]);
        String fileName = args[2];
        String matchesWith = args[3];
        String sha256 = args[4];
        if (args.length > 5) {
            deletePrevious = Boolean.parseBoolean(args[5]);
            newName = args[6];
        }
        System.out.println(downloadURI);
        System.out.println(path);
        System.out.println(fileName);
        System.out.println(matchesWith);
        System.out.println(sha256);

        downloadReplaceWindow(downloadURI, path, fileName, matchesWith, sha256);
    }

    public static void downloadReplaceWindow(String downlaodURI, File path, String fileName, String matchesWith) {
        downloadReplaceWindow(downlaodURI, path, fileName, matchesWith, null);
    }

    private static String generateSHA256Hash(Path filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
            DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);

            byte[] buffer = new byte[4096];
            while (digestInputStream.read(buffer) != -1) {
                // Reading the file content
            }

            digestInputStream.close();
            byte[] hashBytes = digest.digest();

            // Convert the byte array to a hexadecimal representation
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void downloadReplaceWindow(String downlaodURI, File path, String fileName, String matchesWith, String sha256) {
        FrameMaker maker = new FrameMaker("Mod Assistant - " + fileName, new Dimension(600, 280), 0, false);
        JFrame frame = maker.pack();
        AtomicBoolean suc = new AtomicBoolean(false);
        maker.addText("Updating " + fileName, 10, 10, 20, false);
        JButton close = maker.addButton("Close Minecraft", 10, 210, 170, e -> {
            suc.set(true);
            frame.dispose();
        });
        close.setEnabled(false);
        JTextArea field = maker.addTextArea(10, 50, 560, 150);
        field.setEditable(false);
        field.setText("Finding all past versions of " + matchesWith);
        maker.override();
        try {
            byte[] lastUpdate;
            File replaceThis = new File(path.getAbsolutePath() + "\\" + fileName);
            System.out.println(replaceThis.getAbsolutePath());
            for (File file : (File[])Objects.<File[]>requireNonNull(path.listFiles())) {
                if (file != null &&
                        file.getName().contains(matchesWith))
                    replaceThis = file;
            }
            field.setText(field.getText() + "\nGetting last update data....");
            if (replaceThis.exists()) {
                lastUpdate = Files.readAllBytes(replaceThis.toPath());
            } else {
                lastUpdate = new byte[0];
            }
            if (!replaceThis.exists()) {
                replaceThis.createNewFile();
                Files.write(replaceThis.toPath(), new byte[0], new java.nio.file.OpenOption[0]);
            }

            if (deletePrevious) {
                field.setText(field.getText() + "\nDeleting previous versions of " + matchesWith);
                for (File file : (File[])Objects.<File[]>requireNonNull(path.listFiles())) {
                    if (file != null &&
                            file.getName().contains(matchesWith))
                        file.delete();
                }

                if (new File(path.getAbsolutePath() + "\\" + newName).createNewFile()) {
                    field.setText(field.getText() + "\nStaged new update files");
                }
            }

            field.setText(field.getText() + "\nDownloading latest update....");
            File result = downloader(path, downlaodURI, !newName.equals("") ? newName : fileName);
            field.setText(field.getText() + "\nUpdate has been completed.");
            if (sha256 != null && !Objects.equals(sha256, "")) {
                field.setText(field.getText() + "\nChecking file hash of update...");
                String actualHash = generateSHA256Hash(result.toPath());
                System.out.println(sha256);
                System.out.println(actualHash);
                if (sha256.equals(actualHash)) {
                    field.setText(field.getText() + "\nFile hash has been confirmed.");
                } else {
                    field.setText(field.getText() + "\nFile hash could not be confirmed. The update will fallback to it's last release.");
                    field.setText(field.getText() + "\nStarting fallback....");
                    File optOutPreRelease = new File("vicious\\opt-out-pre.txt");
                    if (!optOutPreRelease.exists())
                        optOutPreRelease.createNewFile();
                    String disabledReleases = FileReader.readFile(optOutPreRelease);
                    FileReader.writeFile(optOutPreRelease.getPath(), disabledReleases + downlaodURI + "\n");
                    Files.write(result.toPath(), lastUpdate, new java.nio.file.OpenOption[0]);
                    if (lastUpdate.length < 1) {
                        field.setText(field.getText() + "\nLast fallback does not contain any bytes. Please update manually.");
                    } else {
                        field.setText(field.getText() + "\nFallback completed and update has been blacklisted.");
                    }
                }
            }
            close.setEnabled(true);
            maker.override();
            while (!suc.get())
                TimeUnit.SECONDS.sleep(1L);
        } catch (IOException | InterruptedException ex) {
            close.setEnabled(true);
            maker.override();

            ex.printStackTrace();
        }
    }

    private static boolean isRedirected(Map<String, List<String>> header) {
        for (String hv : header.get(null)) {
            if (hv.contains(" 301 ") || hv
                    .contains(" 302 "))
                return true;
        }
        return false;
    }

    private static File downloader(File modsFolder, String link, String fileName) throws IOException {
        URL url = new URL(link);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        Map<String, List<String>> header = http.getHeaderFields();
        while (isRedirected(header)) {
            link = ((List<String>)header.get("Location")).get(0);
            url = new URL(link);
            http = (HttpURLConnection)url.openConnection();
            header = http.getHeaderFields();
        }
        InputStream input = http.getInputStream();
        byte[] buffer = new byte[4096];
        int n = -1;
        if (modsFolder.isFile()) {
            OutputStream output = new FileOutputStream(modsFolder);
            while ((n = input.read(buffer)) != -1)
                output.write(buffer, 0, n);
            output.close();
        } else {
            OutputStream output = new FileOutputStream(new File(modsFolder.getPath() + "\\" + fileName));
            while ((n = input.read(buffer)) != -1)
                output.write(buffer, 0, n);
            output.close();
        }

        // return the resulting file
        return new File(modsFolder.getPath() + "\\" + fileName);
    }

    private static String encodeHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++)
            sb.append(Integer.toString((digest[i] & 0xFF) + 256, 16).substring(1));
        return sb.toString();
    }

    public static String digest(String alg, String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(alg);
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();
            return encodeHex(digest);
        } catch (NoSuchAlgorithmException|java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
