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

package me.konglong.momei.mongodb.script.convert;

/**
 * A converter converts a source object of type S to a target of type T.
 * Implementations of this interface are thread-safe and can be shared.
 *
 * <p>This interface referenced {@literal org.springframework.core.convert.converter.Converter}
 *
 * @author chenlong
 * @param <S> The source type
 * @param <T> The target type
 */
public interface Converter<S, T> {

    /**
     * Convert the source of type S to target type T.
     * @param source the source object to convert, which must be an instance of S (never {@code null})
     * @return the converted object, which must be an instance of T (potentially {@code null})
     * @throws IllegalArgumentException if the source could not be converted to the desired target type
     */
    T convert(S source);

}
