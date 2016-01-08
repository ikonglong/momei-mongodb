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
class JsFunction extends JsPartial {

    private static final String STARTING_CHARS_FOR_PREPARE = ScriptConstants.PREPARE_FUNC_NAME_PREFIX;
    private static final String STARTING_CHARS_FOR_CLEAN = ScriptConstants.CLEANUP_FUNC_NAME_PREFIX;
    private static final String NAME_BODY_JUNCTURE_REGEX = "=[ ]*function[ ]*\\([ ]*\\)";
    private static final String NAME_BODY_JUNCTURE = "=function()";

    JsFunction(String startingLine) {
        Assert.isTrue(isStartingLine(startingLine),
                "Line '%s' is not a starting line of function!", startingLine);
        this.append(startingLine);
    }

    static boolean isStartingLine(String line) {
        return (line.startsWith(STARTING_CHARS_FOR_PREPARE)
                || line.startsWith(STARTING_CHARS_FOR_CLEAN));
    }

    @Override
    protected void append(String line) {
        super.append(trimTrailingInlineComment(line));
    }

    NamedMongoScript asNamedMongoScript() {
        String theCode = this.content.toString()
                .replaceFirst(NAME_BODY_JUNCTURE_REGEX, NAME_BODY_JUNCTURE);
        if (theCode.endsWith(";")) {
            theCode = theCode.substring(0, theCode.length() - 1);
        }

        int nameEndIndex = theCode.indexOf(NAME_BODY_JUNCTURE);
        if (nameEndIndex == -1) {
            throw new IllegalStateException(String.format("Malformed js function: %s", this.content));
        }

        String funcName = theCode.substring(0, nameEndIndex).trim();
        // Skip the char '='
        String funcBody = theCode.substring(nameEndIndex + 1).trim();
        return new NamedMongoScript(funcName, funcBody);
    }

    private String trimTrailingInlineComment(String codeLine) {
        int commentStartingIndex = codeLine.lastIndexOf("//");
        if (commentStartingIndex == -1) {
            return codeLine;
        }

        return codeLine.substring(0, commentStartingIndex);
    }
}