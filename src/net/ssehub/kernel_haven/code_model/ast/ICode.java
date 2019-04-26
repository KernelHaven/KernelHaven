/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.code_model.ast;

/**
 * Represents unparsed string of code inside the AST. See class comment of {@link ISyntaxElement}.
 * <p>
 * This can contain variability (e.g. ifdef blocks), so instances of this interface can be any of:
 * <ul>
 *      <li>{@link Code} (representing the unparsed code string)</li>
 *      <li>{@link CodeList} (a list containing {@link Code}s and/or {@link CppBlock}s)</li>
 *      <li>{@link CppBlock} (only containing {@link Code}s and/or {@link CppBlock}s)</li>
 *      <li>{@link Comment} (a comment inside the unparsed code)</li>
 * </ul>
 * 
 * @author Adam
 */
public interface ICode extends ISyntaxElement {

}
