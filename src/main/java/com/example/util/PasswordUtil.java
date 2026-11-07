
package com.example.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    // Mã hóa mật khẩu bằng SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] bytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Kiểm tra mật khẩu
    public static boolean checkPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    public static void main(String[] args) {
    String matKhau = "Admin@123";
    System.out.println(hashPassword(matKhau));
}
}
