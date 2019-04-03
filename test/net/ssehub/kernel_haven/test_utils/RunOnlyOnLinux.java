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
package net.ssehub.kernel_haven.test_utils;

import org.junit.runners.model.InitializationError;

import net.ssehub.kernel_haven.util.Util.OSType;

/**
 * May be used to specify that a certain test class runs only on <b>Linux</b> systems.
 * Usage: <tt>@RunWith(value = RunOnlyOnLinux.class)</tt>
 * @author El-Sharkawy
 *
 */
public class RunOnlyOnLinux extends AbstractOsSpecificTestRunner {
    
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code clazz}.
     * @param clazz The test class.
     * @throws InitializationError if the test class is malformed.
     */
    public RunOnlyOnLinux(Class<?> clazz) throws InitializationError {
        super(clazz);
    }


    @Override
    protected boolean isSupportedOS(OSType os) {
        return os == OSType.LINUX64 || os == OSType.LINUX32;
    }
}
