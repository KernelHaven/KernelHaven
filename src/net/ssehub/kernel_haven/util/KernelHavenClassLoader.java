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
package net.ssehub.kernel_haven.util;

import java.net.URL;
import java.net.URLClassLoader;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A {@link ClassLoader} to be used by KernelHaven. Starting with Java 9, it is required that this is set as the
 * <code>java.system.class.loader</code> in order for the plugin system to work.
 *
 * @author Adam
 */
public class KernelHavenClassLoader extends URLClassLoader {

    /**
     * Creates this class loader. This will be called by the JVM if <code>java.system.class.loader</code> is set to
     * this class.
     * 
     * @param parent The parent class loader to delegate to.
     */
    public KernelHavenClassLoader(@NonNull ClassLoader parent) {
        super(new URL[0], parent);
    }

    // make this method public
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
    
}
