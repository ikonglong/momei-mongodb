/*
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.konglong.momei.mongodb.config;

import me.konglong.momei.base.Optional;
import me.konglong.momei.util.ClassPathResources;
import me.konglong.momei.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author chenlong
 */
public final class MongoConfig {

    private static final String CONFIG_FILE = "mongo.properties";

    private static final String HOST_KEY = "mongo.host";
    private static final String PORT_KEY = "mongo.port";
    private static final String SERVERS_KEY = "mongo.servers";
    private static final String CREDENTIALS_KEY = "mongo.credentials";
    private static final String DB_KEY = "mongo.db";

    private final String host;
    private final Integer port;
    private final String servers;
    private final String credentials;
    private final String db;

    private MongoConfig(Properties properties) {
        this.host = getStringValFrom(properties, HOST_KEY);
        this.port = getIntValFrom(properties, PORT_KEY);
        this.servers = getStringValFrom(properties, SERVERS_KEY);
        this.credentials = getStringValFrom(properties, CREDENTIALS_KEY);
        this.db = getStringValFrom(properties, DB_KEY);

        checkState();
    }

    public static MongoConfig loadFromFile() {
        try {
            InputStream inputStream = new FileInputStream(
                    ClassPathResources.resourceInClassPathRoot(CONFIG_FILE));
            Properties configProps = new Properties();
            configProps.load(inputStream);
            return new MongoConfig(configProps);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    String.format("Config file %s not found", CONFIG_FILE),
                    e);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Error while reading config file %s!", CONFIG_FILE),
                    e);
        }
    }

    public Optional<String> host() {
        return Optional.fromNullable(host);
    }

    public Optional<Integer> port() {
        return Optional.fromNullable(port);
    }

    public Optional<String> servers() {
        return Optional.fromNullable(servers);
    }

    public Optional<String> credentials() {
        return Optional.fromNullable(credentials);
    }

    public String db() {
        return db;
    }

    private String getStringValFrom(Properties props, String key) {
        return StringUtils.trimWhitespace(props.getProperty(key));
    }

    private Integer getIntValFrom(Properties props, String key) {
        String val = getStringValFrom(props, key);
        if (val != null) {
            try {
                return Integer.valueOf(val);
            } catch (NumberFormatException e) {
                throw new RuntimeException(
                        String.format("Value for key (%s) must be int: %s", key, val), e);
            }
        }
        return null;
    }

    private void checkState() {
        if (!StringUtils.hasText(db)) {
            throw new IllegalStateException("mongo.db must be given");
        }
    }
}

