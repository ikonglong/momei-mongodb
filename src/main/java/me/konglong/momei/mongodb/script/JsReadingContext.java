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

import me.konglong.momei.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author chenlong
 */
class JsReadingContext {

    private final Stack<JsPartial> partialStack = new Stack<>();

    void append(String line) {

        line = line.trim();

        // Directly discard the empty line or single-line comment
        if (StringUtils.isEmpty(line) || JsSingleLineComment.isStartingLine(line)) {
            return;
        }

        if (JsMultilineComment.isStartingLine(line)) {
            pushToStack(new JsMultilineComment(line));
            return;
        }

        if (JsFunction.isStartingLine(line)) {
            if (current() != null) {
                current().setToBeComplete();
            }
            pushToStack(new JsFunction(line));
            return;
        }

        // Discard multi-line comment if it is complete
        if (current() instanceof JsMultilineComment) {
            current().append(line);
            if (current().isComplete()) {
                partialStack.pop();
            }
            return;
        }

        current().append(line);
    }

    void onReadingFinished() {
        current().setToBeComplete();
    }

    List<NamedMongoScript> getNamedMongoScripts() {
        if (partialStack.isEmpty()) {
            return Collections.emptyList();
        }

        List<NamedMongoScript> scripts = new ArrayList<>(partialStack.size());
        for (JsPartial partial : partialStack) {
            if (partial instanceof JsFunction) {
                JsFunction func = (JsFunction) partial;
                scripts.add(func.asNamedMongoScript());
            }
        }
        return scripts;
    }

    private void pushToStack(JsPartial partial) {
        if (JsPartial.isSingleLineComment(partial)) {
            return;
        }
        if (JsPartial.isMultilineComment(partial) && partial.isComplete()) {
            return;
        }
        partialStack.push(partial);
    }

    private JsPartial current() {
        if (partialStack.isEmpty()) {
            return null;
        }
        return partialStack.peek();
    }
}
