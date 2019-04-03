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
package net.ssehub.kernel_haven.analysis;

import java.lang.reflect.Field;

import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * May be used to rename a thread via a {@link NamedRunnable}.
 * @author El-Sharkawy
 *
 */
class ThreadRenamer {

    private int threadNumber = 1;
    private String prefix; 
    
    /**
     * Sole constructor.
     * @param prefix The prefix to rename threads.
     */
    ThreadRenamer(@Nullable String prefix) {
        this.prefix = prefix;
        if (null == this.prefix) {
            this.prefix = "PollThread";
        }
    }
    
    /**
     * Gives the currently executed thread a meaningful name.
     */
    void rename() {
        Thread th = Thread.currentThread();
        Field thField;
        try {
            thField = th.getClass().getDeclaredField("target");
            thField.setAccessible(true);
            Object fieldValue = thField.get(th);
            String name = prefix + " #" + threadNumber++;
            if (fieldValue instanceof NamedRunnable) {
                name += " - " + ((NamedRunnable) fieldValue).getName();
            } else {
                // Maybe a private Worker
                Field f = fieldValue.getClass().getDeclaredField("firstTask");
                f.setAccessible(true);
                Object workerdValue = f.get(fieldValue);
                if (workerdValue instanceof NamedRunnable) {
                    name += " - " + ((NamedRunnable) fieldValue).getName();
                }
            }
            Thread.currentThread().setName(name);
        } catch (ReflectiveOperationException | SecurityException e) {
            e.printStackTrace();
        }
    }
}
