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

package me.konglong.momei.junit.mongoext;

import me.konglong.momei.mongodb.core.MongoScriptExecutor;
import me.konglong.momei.mongodb.script.NamedMongoScript;
import me.konglong.momei.mongodb.script.ScriptConstants;
import org.junit.runner.Description;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenlong
 */
abstract class ScriptExecutionContext {

    private static final MongoScriptExecutor mongoScriptExecutor;

    static {
        mongoScriptExecutor = new MongoScriptExecutor();

        final Thread shutdownHook = new Thread() {
            @Override
            public void run() {
                try {
                    mongoScriptExecutor.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private static final Map<String, Map<String, NamedMongoScript>>
            testClassToScriptsMap = new HashMap<>();

    static void putScriptsMapForTestClass(Class<?> testClass, Map<String, NamedMongoScript> scriptsMap) {
        testClassToScriptsMap.put(testClass.getSimpleName(), scriptsMap);
    }

    static void removeScriptsMapForTestClass(Class<?> testClass) {
        testClassToScriptsMap.remove(testClass.getSimpleName());
    }

    static MongoScriptExecutor scriptExecutor() {
        return mongoScriptExecutor;
    }

    static NamedMongoScript getPrepareScript(Description desc) {
        return testClassToScriptsMap.get(testClassSimpleName(desc))
                .get(ScriptConstants.PREPARE_FUNC_NAME_PREFIX + testMethodName(desc));
    }

    static NamedMongoScript getCleanupScript(Description desc) {
        return testClassToScriptsMap.get(testClassSimpleName(desc))
                .get(ScriptConstants.CLEANUP_FUNC_NAME_PREFIX + testMethodName(desc));
    }

    private static String testClassSimpleName(Description desc) {
        return desc.getTestClass().getSimpleName();
    }

    private static String testMethodName(Description desc) {
        return desc.getMethodName();
    }
}