package com.forestfull.router.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Configuration
public class UtilsConfig {

    private static class EncryptionSetting {
        public static void main(String[] args) {

        }

        private static StringEncryptor initEncryptor(String key) {
            final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            encryptor.setPassword(key);
            encryptor.setKeyObtentionIterations(1000);
            encryptor.setPoolSize(1);
            return encryptor;
        }

        private static final BiConsumer<String, String> enc = (key, str)
                -> System.out.println(str + " : ENC(" + initEncryptor(key).encrypt(str) + ")");
        private static final BiConsumer<String, String> dec = (key, str)
                -> System.out.println(str + " : " + initEncryptor(key).decrypt(str));
    }

    @Value("${spring.profiles.active}")
    private String key;

    @Bean("jasyptStringEncryptor")
    StringEncryptor stringEncryptor() {
        return EncryptionSetting.initEncryptor(key);
    }
}