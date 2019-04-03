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

import org.junit.AssumptionViolatedException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.Util.OSType;

/**
 * May be used to specify that a certain test class runs only on specific operating systems.
 * Usage: <tt>@RunWith(value = AbstractOsSpecificTestRunner.class)</tt>
 * @author El-Sharkawy
 *
 */
abstract class AbstractOsSpecificTestRunner extends BlockJUnit4ClassRunner {
    
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code clazz}.
     * @param clazz The test class.
     * @throws InitializationError if the test class is malformed.
     */
    public AbstractOsSpecificTestRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
        OSType os = Util.determineOS();
        
        if (!isSupportedOS(os)) {
            notifier.fireTestAssumptionFailed(new Failure(super.getDescription(),
                    new AssumptionViolatedException(this.getTestClass().getName()
                            + " skipped because of wrong OS used: " + os)));
        } else {
            super.run(notifier);            
            
        }
    }
    
    /**
     * Specification whether the given operating system is supported by the executed test.
     * 
     * @param os The operating system as determined by {@link Util#determineOS()}.
     * @return <tt>true</tt> if the test can be executed on the OS, <tt>false</tt> if the test shall be skipped.
     */
    protected abstract boolean isSupportedOS(OSType os);
}
