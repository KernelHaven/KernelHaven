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
package net.ssehub.kernel_haven.util.logic;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A boolean variable.
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public final class Variable extends Formula {
    
    private static final long serialVersionUID = -5566369071417331297L;

    private @NonNull String name;
    
    /**
     * Creates a boolean variable.
     * 
     * @param name The name of this variable.
     */
    public Variable(@NonNull String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of this variable.
     * 
     * @return The name of this variable.
     */
    public @NonNull String getName() {
        return name;
    }
    
    @Override
    public @NonNull String toString() {
        return name;
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Variable) {
            Variable other = (Variable) obj;
            return name.equals(other.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public <T> T accept(@NonNull IFormulaVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }
    
    @Override
    public void accept(@NonNull IVoidFormulaVisitor visitor) {
        visitor.visitVariable(this);
    }
    
    @Override
    protected int getPrecedence() {
        return 3;
    }
    
    @Override
    public void toString(@NonNull StringBuilder result) {
        result.append(toString());
    }
}
