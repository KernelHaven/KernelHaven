/*
 * Copyright 2019 University of Hildesheim, Software Systems Engineering
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
package net.ssehub.kernel_haven.util.io.void_io;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.ssehub.kernel_haven.util.io.ITableCollection;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * An {@link ITableCollection} that does not write anything. Any input to writers is simply discarded; readers
 * return no content. This class is completely exception-free.
 *
 * @author Adam
 */
public class VoidTableCollection implements ITableCollection {

    private @NonNull File file;
    
    /**
     * Creates a {@link VoidTableCollection}.
     * 
     * @param file The file to create this collection for. Nothing is done with this; it won't be created.
     */
    public VoidTableCollection(@NonNull File file) {
        this.file = file;
    }
    
    @Override
    public void close() {
    }

    @Override
    public @NonNull VoidTableReader getReader(@NonNull String name) {
        return new VoidTableReader();
    }

    @Override
    public @NonNull Set<@NonNull String> getTableNames() {
        return new HashSet<>();
    }

    @Override
    public @NonNull VoidTableWriter getWriter(@NonNull String name) {
        return new VoidTableWriter();
    }

    @Override
    public @NonNull Set<@NonNull File> getFiles() {
        HashSet<@NonNull File> files = new HashSet<>();
        files.add(file);
        return files;
    }

}
