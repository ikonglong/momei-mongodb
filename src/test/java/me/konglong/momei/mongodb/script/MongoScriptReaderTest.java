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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author chenlong
 */
@RunWith(JUnit4.class)
public class MongoScriptReaderTest {

    @Test
    public void readScript() throws Exception {
        List<NamedMongoScript> scripts = MongoScriptReader.read(
                "MongoScriptReaderTest.js", MongoScriptReaderTest.class);

        String expectedFunc1 = "function() {for (var i = 1; i <= 100; i++) {"
                + "db.books.insert({ _id: ObjectId(), name: (\"Thinking in Java \"+i) });}}";
        assertEquals("prepare4_findBooks", scripts.get(0).getName());
        assertEquals(expectedFunc1, scripts.get(0).getCode());


        String expectedFunc2 = "function() {db.books.remove({name: {$regex: /Thinking in Java/}});}";
        assertEquals("cleanup4_findBooks", scripts.get(1).getName());
        assertEquals(expectedFunc2, scripts.get(1).getCode());
    }
}