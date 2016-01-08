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

import me.konglong.momei.util.Assert;

/**
 * @author chenlong
 */
class JsSingleLineComment extends JsPartial {

    private static final String STARTING_CHARS = "//";

    JsSingleLineComment(String startingLine) {
        Assert.isTrue(isStartingLine(startingLine),
                "Line '%s' is not a starting line of single-line comment!", startingLine);
        this.append(startingLine);
        this.setToBeComplete();
    }

    static boolean isStartingLine(String line) {
        return line.startsWith(STARTING_CHARS);
    }
}