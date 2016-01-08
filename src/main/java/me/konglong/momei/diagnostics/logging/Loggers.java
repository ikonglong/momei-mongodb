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

package me.konglong.momei.diagnostics.logging;

import me.konglong.momei.util.Assert;

/**
 * This class is not part of the public API.
 *
 * @author chenlong
 */
public final class Loggers {

    private static final boolean USE_SLF4J = shouldUseSLF4J();

    private Loggers() {
    }

    /**
     * Gets a logger with the full name of the given class
     *
     * @param clazz
     * @return
     */
    public static Logger getLogger(final Class<?> clazz) {
        Assert.notNull(clazz, "Given class must not be null");

        String name = clazz.getName();
        if (USE_SLF4J) {
            return new SLF4JLogger(name);
        } else {
            return new JULLogger(name);
        }
    }

    private static boolean shouldUseSLF4J() {
        try {
            Class.forName("org.slf4j.LoggerFactory");
            // Don't use SLF4J unless a logging implementation has been configured for it
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
