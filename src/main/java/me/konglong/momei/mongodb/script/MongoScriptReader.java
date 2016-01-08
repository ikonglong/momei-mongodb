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

package me.konglong.momei.mongodb.script;

import me.konglong.momei.diagnostics.logging.Logger;
import me.konglong.momei.diagnostics.logging.Loggers;
import me.konglong.momei.util.ClassPathResources;

import java.io.*;
import java.util.List;

/**
 * Reads the {@literal JavaScript} file used for running test
 *
 * @author chenlong
 */
public class MongoScriptReader {

    private static final Logger logger = Loggers.getLogger(MongoScriptReader.class);

    private static final String SCRIPT_PREFIX = ".js";

    public static List<NamedMongoScript> readScriptWith(Class<?> clazz) {
        String scriptName = clazz.getSimpleName() + SCRIPT_PREFIX;

        try {
            return read(scriptName, clazz);
        } finally {
            logger.info(String.format("Read script file %s for test class %s", scriptName, clazz.getSimpleName()));
        }
    }

    public static List<NamedMongoScript> read(String scriptName, Class<?> withClass) {
        BufferedReader reader = null;
        JsReadingContext readingCtx = new JsReadingContext();

        try {
            File scriptFile = ClassPathResources.resourceInSameDirWith(withClass, scriptName);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFile), "UTF-8"));
            String jsCodeLine = "";

            while (jsCodeLine != null) {
                readingCtx.append(jsCodeLine);
                jsCodeLine = reader.readLine();
            }

            readingCtx.onReadingFinished();
        } catch (IOException e) {
            throw new RuntimeException("Error while reading script file: " + scriptName, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //logger.error("Error while closing file: " + jsonFile, e);
                }
            }
        }

        return readingCtx.getNamedMongoScripts();
    }
}