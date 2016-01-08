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

import com.mongodb.*;
import me.konglong.momei.base.Optional;
import me.konglong.momei.diagnostics.logging.Logger;
import me.konglong.momei.diagnostics.logging.Loggers;
import me.konglong.momei.mongodb.config.MongoConfig;
import me.konglong.momei.mongodb.script.NamedMongoScript;
import me.konglong.momei.mongodb.script.convert.Converter;
import me.konglong.momei.util.Assert;

import static me.konglong.momei.mongodb.script.convert.NamedMongoScriptConverters.DBObjectToNamedMongoScriptCoverter;
import static me.konglong.momei.mongodb.script.convert.NamedMongoScriptConverters.NamedMongoScriptToDBObjectConverter;

/**
 * An executor that executes the given mongodb javascript.
 *
 * @author chenlong
 */
public final class MongoScriptExecutor implements Disposable {

    private static final Logger logger = Loggers.getLogger(MongoScriptExecutor.class);

    private static final String ID_FIELD = "_id";
    private static final String SCRIPT_COLLECTION_NAME = "system.js";

    private final MongoClientFactory mongoClientFactory;
    private final DB db;

    private final Converter<NamedMongoScript, DBObject> scriptToDBObjConverter;
    private final Converter<DBObject, NamedMongoScript> dbObjToScriptConverter;

    public MongoScriptExecutor() {
        MongoConfig mongoConfig = MongoConfig.loadFromFile();
        this.mongoClientFactory = MongoClientFactory.builder()
                .setHost(mongoConfig.host())
                .setPort(mongoConfig.port())
                .setReplicaSetSeeds(mongoConfig.servers())
                .setCredentials(mongoConfig.credentials())
                .build();
        this.db = this.mongoClientFactory.getInstance().getDB(mongoConfig.db());

        this.scriptToDBObjConverter = NamedMongoScriptToDBObjectConverter.INSTANCE;
        this.dbObjToScriptConverter = DBObjectToNamedMongoScriptCoverter.INSTANCE;
    }

    @Override
    public void destroy() throws Exception {
        this.mongoClientFactory.destroy();
        logger.info("Destroyed MongoScriptExecutor successfully!");
    }

    public NamedMongoScript register(NamedMongoScript script) {
        Assert.notNull(script, "Script must not be null!");
        save(script, SCRIPT_COLLECTION_NAME);
        return script;
    }

    public Object execute(final NamedMongoScript script) {
        Assert.notNull(script, "Script must not be null!");

        return this.execute(new DbCallback<Object>() {
            @Override
            public Object doInDB(DB db) throws MongoException {
                return db.eval(script.getCode());
            }
        });
    }

    public Object call(final String scriptName) {
        Assert.hasText(scriptName, "ScriptName must not be null or empty!");

        return this.execute(new DbCallback<Object>() {
            @Override
            public Object doInDB(DB db) throws MongoException {
                return db.eval(String.format("%s()", scriptName));
            }
        });
    }

    private void save(NamedMongoScript scriptToSave, String collectionName) {
        Assert.notNull(scriptToSave);
        Assert.hasText(collectionName);

        saveDBObject(collectionName,
                scriptToDBObjConverter.convert(scriptToSave),
                Optional.<WriteConcern>absent());
    }

    private Object saveDBObject(final String collectionName,
                                final DBObject dbDoc, final Optional<WriteConcern> writeConcern) {
        return execute(collectionName, new CollectionCallback<Object>() {
            @Override
            public Object doInCollection(DBCollection collection) throws MongoException {
                if (writeConcern.isPresent()) {
                    collection.save(dbDoc, writeConcern.get());
                } else {
                    collection.save(dbDoc);
                }
                return dbDoc.get(ID_FIELD);
            }
        });
    }

    private <T> T execute(DbCallback<T> action) {
        Assert.notNull(action);
        DB db = this.getDb();
        return action.doInDB(db);
    }

    private <T> T execute(String collectionName, CollectionCallback<T> callback) {
        Assert.notNull(callback);
        DBCollection collection = getAndPrepareCollection(getDb(), collectionName);
        return callback.doInCollection(collection);
    }

    private DBCollection getAndPrepareCollection(DB db, String collectionName) {
        DBCollection collection = db.getCollection(collectionName);
        prepareCollection(collection);
        return collection;
    }

    /**
     * Prepare the collection before any processing is done using it.
     * This allows a convenient way to apply settings like slaveOk() etc.
     *
     * @param collection
     */
    private void prepareCollection(DBCollection collection) {
        // do nothing
    }

    private DB getDb() {
        return this.db;
    }
}

