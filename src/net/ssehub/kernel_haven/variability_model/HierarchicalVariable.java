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
package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonList;
import net.ssehub.kernel_haven.util.io.json.JsonNull;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A {@link VariabilityVariable} that has additional information about the hierarchy of variables.
 * 
 * TODO: serialization
 * 
 * @author Adam
 */
public class HierarchicalVariable extends VariabilityVariable {

    private @Nullable HierarchicalVariable parent;
    
    private @NonNull Set<@NonNull HierarchicalVariable> children;
    
    private int nestingDepth;
    
    /**
     * Creates a {@link HierarchicalVariable}.
     * 
     * @param name The name of the variable.
     * @param type The type of the variable.
     */
    public HierarchicalVariable(@NonNull String name, @NonNull String type) {
        super(name, type);
        children = new HashSet<>();
    }

    /**
     * Creates a {@link HierarchicalVariable}.
     * 
     * @param name The name of the variable.
     * @param type The type of the variable.
     * @param dimacsNumer The number in the dimacs mapping of this variable.
     */
    public HierarchicalVariable(@NonNull String name, @NonNull String type, int dimacsNumer) {
        super(name, type, dimacsNumer);
        children = new HashSet<>();
    }
    
    /**
     * Creates a {@link HierarchicalVariable}.
     * 
     * @param variable A variable to copy the name, type and DIMACS number from.
     */
    public HierarchicalVariable(@NonNull VariabilityVariable variable) {
        super(variable.getName(), variable.getType(), variable.getDimacsNumber());
        children = new HashSet<>();

        List<@NonNull SourceLocation> locations = variable.getSourceLocations();
        if (locations != null) {
            for (SourceLocation sl : locations) {
                addLocation(sl);
            }
        }
    }
    
    /**
     * Returns the parent of this variable.
     * 
     * @return The parent variable. <code>null</code> if this variable has no parent.
     */
    public @Nullable HierarchicalVariable getParent() {
        return parent;
    }
    
    /**
     * <p>
     * Sets the parent for this variable, add this variable as a child to the parent variable and set the nesting depth
     * of this to parent+1. If there was a previous parent set for this, then this variable is removed from the
     * previous parents children list. If the new parent is null, then the new nesting depth is 0.
     * </p>
     * <p>
     * This method should only be called by the extractor that creates the variability model.
     * </p>
     * 
     * @param parent The new parent for this variable.
     */
    public void setParent(@Nullable HierarchicalVariable parent) {
        // remove from previous
        HierarchicalVariable previousParent = this.parent;
        if (previousParent != null) {
            previousParent.children.remove(this);
        }
        
        // add to new
        this.parent = parent;
        if (null != parent) {
            parent.children.add(this);
            this.nestingDepth = parent.nestingDepth + 1;
        } else {
            this.nestingDepth = 0;
        }
    }
    
    /**
     * Overrides the nesting depth. This should only be called by the serialization, to ensure consistency.
     * 
     * @param nestingDepth The new nesting depth.
     */
    void setNestingDepth(int nestingDepth) {
        this.nestingDepth = nestingDepth;
    }
    
    /**
     * Returns the children of this variable. May be empty, if this has no children.
     * 
     * @return The children of this variable.
     */
    public @NonNull Set<@NonNull HierarchicalVariable> getChildren() {
        return children;
    }
    
    /**
     * Returns the nesting depth of this variable. This is the number of parents above this variable. 0 means top level.
     * 
     * @return The nesting depth of this variable.
     */
    public int getNestingDepth() {
        return nestingDepth;
    }
    
    @Override
    protected @NonNull JsonObject toJson() {
        JsonObject result = super.toJson();
        
        if (parent != null) {
            result.putElement("parent", new JsonString(parent.getName()));
        } else {
            result.putElement("parent", JsonNull.INSTANCE);
        }
        
        JsonList childrenList = new JsonList();
        for (VariabilityVariable child : children) {
            childrenList.addElement(new JsonString(child.getName()));
        }
        result.putElement("children", childrenList);
        
        result.putElement("nestingDepth", new JsonNumber(nestingDepth));
        
        return result;
    }
    
    @Override
    protected void setJsonData(@NonNull JsonObject data, Map<@NonNull String, VariabilityVariable> vars)
            throws FormatException {
        super.setJsonData(data, vars);
        
        if (data.getElement("parent") != JsonNull.INSTANCE) {
            VariabilityVariable var = vars.get(data.getString("parent"));
            
            if (var == null) {
                throw new FormatException("Unknown variable " + data.getString("parent"));
            }
            if (!(var instanceof HierarchicalVariable)) {
                throw new FormatException(data.getString("parent") + " is not a hierarchical variable");
                
            }
            
            this.parent = (HierarchicalVariable) var;
        }
        
        for (JsonElement element : data.getList("children")) {
            if (!(element instanceof JsonString)) {
                throw new FormatException("Expected JsonString, but got " + element.getClass().getSimpleName());
            }
            
            String varName = ((JsonString) element).getValue();
            VariabilityVariable var = vars.get(varName);
            
            if (var == null) {
                throw new FormatException("Unknown variable " + varName);
            }
            if (!(var instanceof HierarchicalVariable)) {
                throw new FormatException(varName + " is not a hierarchical variable");
                
            }
            
            children.add((HierarchicalVariable) var);
        }
        
        this.nestingDepth = data.getInt("nestingDepth");
    }
    
    @Override
    @Deprecated
    protected @NonNull List<@NonNull String> getSerializationData() {
        List<@NonNull String> result = super.getSerializationData();
        
        // nesting depth
        result.add(0, notNull(String.valueOf(nestingDepth)));
        
        // children
        for (VariabilityVariable child : children) {
            result.add(0, child.getName());
        }
        result.add(0, notNull(String.valueOf(children.size())));
        
        // parent
        if (parent != null) {
            result.add(0, parent.getName());
        } else {
            result.add(0, "null");
        }
        
        return result;
    }
    
    @Override
    @Deprecated
    protected void setSerializationData(@NonNull List<@NonNull String> data,
            @NonNull Map<@NonNull String, VariabilityVariable> variables) throws FormatException {
        
        // parent
        if (data.isEmpty()) {
            throw new FormatException("Expecting at least one more element");
        }
        
        String parent = notNull(data.remove(0));
        if (!parent.equals("null")) {
            VariabilityVariable var = variables.get(parent);
            if (var == null) {
                throw new FormatException("Unknown parent variable " + parent);
            }
            if (!(var instanceof HierarchicalVariable)) {
                throw new FormatException("Parent (" + parent + ") is not a hierarchical variable");
            }
            this.parent = (HierarchicalVariable) var;
        }
        
        // children
        if (data.isEmpty()) {
            throw new FormatException("Expecting at least one more element");
        }
        
        int size;
        try {
            size = Integer.parseInt(data.remove(0));
        } catch (NumberFormatException e) {
            throw new FormatException(e);
        }
        if (data.size() < size) {
            throw new FormatException("Expecting at least " + size + " more elements");
        }
        
        for (int i = 0; i < size; i++) {
            String varStr = notNull(data.remove(0));
            VariabilityVariable var = variables.get(varStr);
            
            if (var == null) {
                throw new FormatException("Unknown child variable " + varStr);
            }
            if (!(var instanceof HierarchicalVariable)) {
                throw new FormatException("Child (" + varStr + ") is not a hierarchical variable");
            }
            
            children.add((HierarchicalVariable) var);
        }
        
        // nesting depth
        if (data.isEmpty()) {
            throw new FormatException("Expecting at least one more element");
        }
        
        try {
            this.nestingDepth = Integer.parseInt(data.remove(0));
        } catch (NumberFormatException e) {
            throw new FormatException(e);
        }
        
        super.setSerializationData(data, variables);
    }
    
}
