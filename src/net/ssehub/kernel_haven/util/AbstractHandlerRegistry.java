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

import java.util.HashMap;
import java.util.Map;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A registry that stores classes as handlers for certain keys. Handlers can be registered and retrieved by the key
 * value.
 *
 * @param <Key> The type for key values.
 * @param <ClassType> The generic for classes stored for keys.
 *
 * @author Adam
 */
public class AbstractHandlerRegistry<Key, ClassType> {

    private Map<Key, Class<? extends ClassType>> handlers;

    /**
     * Creates a handler registry with an empty registry. 
     */
    public AbstractHandlerRegistry() {
        handlers = new HashMap<>();
    }
    
    /**
     * Registers a handler for a given key. Overrides the previous handler for this key.
     * 
     * @param key The key to register the handler for.
     * @param handler The handler class.
     */
    public void registerHandler(@NonNull Key key, @NonNull Class<? extends ClassType> handler) {
        handlers.put(key, handler);
    }
    
    /**
     * Retrieves the handler for the given key.
     * 
     * @param key The key to get the handler for.
     * 
     * @return The handler for the specified key. <code>null</code> if no handler has been registered yet.
     */
    protected @Nullable Class<? extends ClassType> getHandler(@NonNull Key key) {
        return handlers.get(key);
    }
    
}
