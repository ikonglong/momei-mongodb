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

package me.konglong.momei.mongodb.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import me.konglong.momei.base.Optional;
import me.konglong.momei.diagnostics.logging.Logger;
import me.konglong.momei.diagnostics.logging.Loggers;
import me.konglong.momei.mongodb.config.MongoCredentialPropertyEditor;
import me.konglong.momei.mongodb.config.ServerAddressPropertyEditor;
import me.konglong.momei.util.CollectionUtils;
import me.konglong.momei.util.StringUtils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Convenient factory for configuring MongoDB.
 *
 * @author chenlong
 */
public final class MongoClientFactory implements Disposable {

    private static final Logger logger = Loggers.getLogger(MongoClientFactory.class);

    private MongoClientOptions mongoClientOptions;
    private String host;
    private Integer port;
    private List<ServerAddress> replicaSetSeeds;
    private List<MongoCredential> credentials;

    private MongoClient singletonInstance;

    private MongoClientFactory() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public MongoClient getInstance() {
        return singletonInstance;
    }

    @Override
    public void destroy() throws Exception {
        this.singletonInstance.close();
        logger.info("Closed MongoClient successfully!");
    }

    /**
     * Eagerly create the singleton instance
     */
    private void afterPropertiesSet() throws Exception {
        this.singletonInstance = createInstance();
    }

    private MongoClient createInstance() throws Exception {

        if (mongoClientOptions == null) {
            mongoClientOptions = MongoClientOptions.builder().build();
        }

        if (credentials == null) {
            credentials = Collections.emptyList();
        }

        return createMongoClient();
    }

    private MongoClient createMongoClient() throws UnknownHostException {

        if (!CollectionUtils.isEmpty(replicaSetSeeds)) {
            return new MongoClient(replicaSetSeeds, credentials, mongoClientOptions);
        }

        return new MongoClient(createConfiguredOrDefaultServerAddress(), credentials, mongoClientOptions);
    }

    private ServerAddress createConfiguredOrDefaultServerAddress() throws UnknownHostException {
        ServerAddress defaultAddress = new ServerAddress();

        return new ServerAddress(
                StringUtils.hasText(host) ? host : defaultAddress.getHost(),
                port != null ? port.intValue() : defaultAddress.getPort()
        );
    }

    public static final class Builder {

        private final MongoClientFactory factory;

        static {
            PropertyEditorManager.registerEditor(ServerAddress.class, ServerAddressPropertyEditor.class);
            PropertyEditorManager.registerEditor(MongoCredential.class, MongoCredentialPropertyEditor.class);
        }

        private Builder() {
            this.factory = new MongoClientFactory();
        }

        public MongoClientFactory build() {
            try {
                this.factory.afterPropertiesSet();
                return this.factory;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Builder setReplicaSetSeeds(Optional<String> replSetSeedsAsString) {
            if (! replSetSeedsAsString.isPresent()) {
                return this;
            }

            ServerAddress[] replicaSetSeeds = readPropVal(replSetSeedsAsString.get(), ServerAddress.class);
            this.factory.replicaSetSeeds = filterNonNullElementsAsList(replicaSetSeeds);
            return this;
        }

        public Builder setCredentials(Optional<String> credentialsAsString) {
            if (! credentialsAsString.isPresent()) {
                return this;
            }

            MongoCredential[] credentials = readPropVal(credentialsAsString.get(), MongoCredential.class);
            this.factory.credentials = filterNonNullElementsAsList(credentials);
            return this;
        }

        public Builder setMongoClientOptions(MongoClientOptions mongoClientOptions) {
            this.factory.mongoClientOptions = mongoClientOptions;
            return this;
        }

        public Builder setHost(Optional<String> host) {
            if (! host.isPresent()) {
                return this;
            }

            this.factory.host = host.get();
            return this;
        }

        public Builder setPort(Optional<Integer> port) {
            if (! port.isPresent()) {
                return this;
            }

            this.factory.port = port.get();
            return this;
        }

        private <T> T readPropVal(String propValAsString, Class<?> propType) {
            PropertyEditor propEditor = PropertyEditorManager.findEditor(propType);
            propEditor.setAsText(propValAsString);
            return (T) propEditor.getValue();
        }

        /**
         * Returns the given array as {@link List} with all {@literal null} elements removed.
         *
         * @param elements the elements to filter <T>, can be {@literal null}.
         * @return a new unmodifiable {@link List#} from the given elements without {@literal null}s.
         */
        private static <T> List<T> filterNonNullElementsAsList(T[] elements) {

            if (elements == null) {
                return Collections.emptyList();
            }

            List<T> candidateElements = new ArrayList<T>();

            for (T element : elements) {
                if (element != null) {
                    candidateElements.add(element);
                }
            }

            return Collections.unmodifiableList(candidateElements);
        }
    }

}

