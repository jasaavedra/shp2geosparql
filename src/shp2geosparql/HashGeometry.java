/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shp2geosparql;

import java.security.MessageDigest;

/**
 *
 * @author root
 */
public class HashGeometry {

    public HashGeometry() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println(HashGeometry.getHash("<htl>algunacosa"));
    }

    public static String getHash(String message) {
        MessageDigest md;
        byte[] buffer, digest;
        String hash = "";

        try {
            buffer = message.getBytes("UTF-8");
            md = MessageDigest.getInstance("SHA1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        md.update(buffer);
        digest = md.digest();
        for (byte aux : digest) {
            int b = aux & 0xff;
            String s = Integer.toHexString(b);
            if (s.length() == 1) {
                hash += "0";
            }
            hash += s;
        }
        return hash;
    }
}
