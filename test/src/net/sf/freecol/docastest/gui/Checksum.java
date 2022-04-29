package net.sf.freecol.docastest.gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class Checksum {

    public static String checkSum(String path){
        try {

            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            //Using MessageDigest update() method to provide input
            byte[] buffer = new byte[256];
            int numOfBytesRead;
            while( (numOfBytesRead = fis.read(buffer)) < 0){
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] digest = md.digest();

            return bytesToHex(buffer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
    public static String checksum2(Path path) {
        return checksum2(path.toString());
    }
    public static String checksum2(String path) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest();

            FileInputStream fis = new FileInputStream(path);
            return Long.toString(getChecksumCRC32(fis, 256));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    // From https://www.baeldung.com/java-checksums
    public static long getChecksumCRC32(InputStream stream, int bufferSize)
            throws IOException {
        CheckedInputStream checkedInputStream = new CheckedInputStream(stream, new CRC32());
        byte[] buffer = new byte[bufferSize];
        while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {}
        return checkedInputStream.getChecksum().getValue();
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
