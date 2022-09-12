package io.wangler.artinaut.security;

import java.util.Base64;

public abstract class AuthenticationTestUtil {

    public static final String KEY_BASE_AUTH = "artinaut.test.admin.basic-auth-header.value";
    public static final String KEY_BASE_AUTH_PWD = "artinaut.test.admin.password";

    public static String basicAuth(String user, String password) {
        return "Basic " + Base64.getEncoder().encodeToString("%s:%s".formatted(user, password).getBytes());
    }

    public static String basicAuthAdmin(String password) {
        return basicAuth("admin", password);
    }
}
