package com.inventario.config;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConfig {
    
    private static String env(String k) {
        String v = System.getenv(k);
        if (v == null || v.isBlank())
            throw new IllegalStateException("Missing env var: " + k);
        return v;
    }

    public static String tnsAdmin() {
        return env("ORACLE_TNS_ADMIN");
    }

    public static String alias() {
        return env("ORACLE_TNS_ALIAS");
    }

    public static String user() {
        return env("ORACLE_USER");
    }

    public static String password() {
        return env("ORACLE_PASSWORD");
    }

    public static String eventGridTopicEndpoint() {
        return env("EVENTGRID_TOPIC_ENDPOINT");
    }

    public static String eventGridAccessKey() {
        return env("EVENTGRID_ACCESS_KEY");
    }

    public static String acsEmailqConnectionString() {
        return env("ACS_EMAIL_CONNECTION_STRING");
    }

    public static String acsEmailSender() {
        return env("ACS_EMAIL_SENDER");
    }
}
