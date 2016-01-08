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

/**
 * @author chenlong
 */
abstract class JsPartial {

    protected final StringBuilder content = new StringBuilder();
    private boolean isComplete = false;

    static boolean isFunction(JsPartial partial) {
        return (partial instanceof JsFunction);
    }

    static boolean isSingleLineComment(JsPartial partial) {
        return (partial instanceof JsSingleLineComment);
    }

    static boolean isMultilineComment(JsPartial partial) {
        return (partial instanceof JsMultilineComment);
    }

    protected void append(String line) {
        if (isComplete()) {
            throw new UnsupportedOperationException(
                    String.format("%s is complete, so support this operation no longer",
                            this.getClass().getSimpleName()));
        }
        content.append(line);
    }

    protected boolean isComplete() {
        return this.isComplete;
    }

    void setToBeComplete() {
        this.isComplete = true;
    }
}
