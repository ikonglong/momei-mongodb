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

import me.konglong.momei.TestData;
import me.konglong.momei.mongodb.script.MongoScriptReader;
import me.konglong.momei.mongodb.script.NamedMongoScript;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Prepares mongo scripts for test class and cleans up the scripts after test class executes
 *
 * @author chenlong
 */
public class ScriptAttendantForTestClass extends TestWatcher {

    @Override
    protected void starting(Description description) {
        super.starting(description);

        if (!needToReadScriptFile(description)) {
            return;
        }

        List<NamedMongoScript> scripts = MongoScriptReader.readScriptWith(description.getTestClass());
        HashMap<String, NamedMongoScript> scriptsMap = new HashMap<>(scripts.size());
        for (NamedMongoScript script : scripts) {
            scriptsMap.put(script.getName(), script);
        }
        ScriptExecutionContext.putScriptsMapForTestClass(
                description.getTestClass(), Collections.unmodifiableMap(scriptsMap));
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);
        ScriptExecutionContext.removeScriptsMapForTestClass(description.getTestClass());
    }

    private boolean needToReadScriptFile(Description description) {
        TestData testData = description.getAnnotation(TestData.class);
        return (testData != null && testData.prepare());
    }
}
