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

package me.konglong.momei.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for reading class path resources
 *
 * @author chenlong
 */
public class ClassPathResources {

    public static File resourceInClassPathRoot(String resourceName) {
        return new File(resourcePath(resourceName));
    }

    public static File resourceInSameDirWith(Class<?> clazz, String resourceName) {
        String path = clazz.getResource("").getPath();
        if (!path.endsWith("/")) {
            path += "/";
        }
        return new File(path + resourceName);
    }

    public static List<File> resourcesInSameDirWith(Class<?> clazz, String[] resourceNames) {
        if (ObjectUtils.isEmpty(resourceNames)) {
            return Collections.emptyList();
        }

        List<File> resources = new ArrayList<>(resourceNames.length);
        for (String resourceName : resourceNames) {
            resources.add(resourceInSameDirWith(clazz, resourceName));
        }

        return resources;
    }

    private static String resourcePath(String resourceName) {
        return ClassPathResources.class.getClassLoader().getResource(resourceName).getPath();
    }
}

