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
import me.konglong.momei.base.Optional;
import me.konglong.momei.diagnostics.logging.Logger;
import me.konglong.momei.diagnostics.logging.Loggers;
import me.konglong.momei.mongodb.script.NamedMongoScript;
import me.konglong.momei.util.Assert;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Prepares mongo data for test method and cleans up the data after test method executes.
 *
 * @author chenlong
 */
public class DataAttendantForTestMethod extends TestWatcher {

    private static final Logger logger = Loggers.getLogger(DataAttendantForTestMethod.class);

    @Override
    protected void starting(Description description) {
        super.starting(description);

        Optional<TestData> testDataAnn = findTestDataAnnOnTestMethod(description);
        if (testDataAnn.isPresent() && testDataAnn.get().prepare()) {
            NamedMongoScript prepareScript = ScriptExecutionContext.getPrepareScript(description);
            Assert.state((prepareScript != null),
                    "Not found preparing script {} for test method {}",
                    prepareScript.getName(), description.getDisplayName());
            ScriptExecutionContext.scriptExecutor().execute(prepareScript);
            logger.info("Executed preparing script {} for test method {}",
                    prepareScript.getName(), description.getDisplayName());
        }
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);

        Optional<TestData> testDataHelp = findTestDataAnnOnTestMethod(description);
        if (testDataHelp.isPresent() && testDataHelp.get().cleanup()) {
            NamedMongoScript cleanupScript = ScriptExecutionContext.getCleanupScript(description);

            Assert.state((cleanupScript != null),
                    "Not found cleaning-up script {} for test method {}",
                    cleanupScript.getName(), description.getDisplayName());
            ScriptExecutionContext.scriptExecutor().execute(cleanupScript);
            logger.info("Executed cleaning-up script {} for test method {}",
                    cleanupScript.getName(), description.getDisplayName());
        }
    }

    private Optional<TestData> findTestDataAnnOnTestMethod(Description desc) {
        try {
            TestData testData = desc.getTestClass()
                    .getMethod(desc.getMethodName()).getAnnotation(TestData.class);
            return Optional.fromNullable(testData);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found: " + desc.getMethodName(), e);
        }
    }
}
