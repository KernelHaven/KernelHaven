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
package net.ssehub.kernel_haven.code_model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Iterator;

import org.junit.Test;

import net.ssehub.kernel_haven.util.logic.True;

/**
 * Tests the {@link CodeBlock} class.
 *
 * @author Adam
 */
public class CodeBlockTest {

    /**
     * Tests the different getters for nested elements.
     */
    @Test
    public void testStructure() {
        CodeBlock main = new CodeBlock(1, 100, new File("test.c"), True.INSTANCE, True.INSTANCE);
        CodeBlock nested1 = new CodeBlock(True.INSTANCE);
        CodeBlock nested2 = new CodeBlock(True.INSTANCE);
        
        main.addNestedElement(nested1);
        main.addNestedElement(nested2);
        
        assertThat(main.getNestedElementCount(), is(2));
        assertThat(main.getNestedElement(0), sameInstance(nested1));
        assertThat(main.getNestedElement(1), sameInstance(nested2));
        
        Iterator<CodeBlock> iter = main.iterator();
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), sameInstance(nested1));
        assertThat(iter.hasNext(), is(true));
        assertThat(iter.next(), sameInstance(nested2));
        assertThat(iter.hasNext(), is(false));
    }
    
}
