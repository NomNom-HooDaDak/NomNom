//package com.p1.nomnom.common.util;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//public class PasswordEncoderUtil {
//
//    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    public static String encodePassword(String rawPassword) {
//        return encoder.encode(rawPassword);
//    }
//
//    public static void main(String[] args) {
//        // 테스트용: 비밀번호 10개를 암호화하여 출력합니다.
//        String[] passwords = {
//                "Password1!", "Password2!", "Password3!", "Password4!", "Password5!",
//                "Password6!", "Password7!", "Password8!", "Password9!", "Password10!"
//        };
//
//        for (String password : passwords) {
//            String encodedPassword = encodePassword(password);
//            System.out.println("Raw: " + password + " => Encoded: " + encodedPassword);
//        }
//    }
//}
