package net.ssehub.kernel_haven.variability_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A serializer for {@link HierarchicalVariable}.
 * 
 * @author Adam
 */
public class HierarchicalVariableSerializer extends VariabilityVariableSerializer {
    
    protected static final int HIERARCHICAL_SIZE = DEFAULT_SIZE + 2;

    @Override
    protected @NonNull List<@NonNull String> serializeImpl(@NonNull VariabilityVariable variable) {
        HierarchicalVariable var = (HierarchicalVariable) variable;
        
        List<@NonNull String> result = super.serializeImpl(var);
        
        HierarchicalVariable parent = var.getParent();
        if (parent != null) {
            result.add(parent.getName());
        } else {
            result.add("null");
        }
        
        result.add(notNull(String.valueOf(var.getNestingDepth())));
        
        return result;
    }
    
    @Override
    protected @NonNull VariabilityVariable deserializeImpl(@NonNull String @NonNull [] csv) throws FormatException {
        VariabilityVariable variable = super.deserializeImpl(csv);
        
        try {
            HierarchicalVariable result = new HierarchicalVariable(variable);
            
            // set a pseudo parent for now; postProcess() will replace this with the proper parent references
            if (!csv[DEFAULT_SIZE].equals("null")) {
                HierarchicalVariable pseudoParent = new HierarchicalVariable(csv[DEFAULT_SIZE], "PSEUDO");
                result.setParent(pseudoParent);
            }
            
            try {
                result.setNestingDepth(Integer.valueOf(csv[DEFAULT_SIZE + 1]));
            } catch (NumberFormatException e) {
                throw new FormatException("Invalid nesting depth: " + csv[DEFAULT_SIZE - 1], e);
            }
            
            return result;
            
        } catch (NumberFormatException e) {
            throw new FormatException(e);
        }
    }
    
    @Override
    public void postProcess(@NonNull VariabilityModel varModel) throws FormatException {
        for (VariabilityVariable var : varModel.getVariables()) {
            HierarchicalVariable hierVar = (HierarchicalVariable) var;
            
            HierarchicalVariable pseudoParent = hierVar.getParent();
            if (pseudoParent != null) {
                // this number is already correct; restore it after setParent() is called, to ensure that it is
                // not accidently override with a wrong number
                int previousNestingDepth = hierVar.getNestingDepth();
                
                HierarchicalVariable realParent = (HierarchicalVariable)
                        varModel.getVariableMap().get(pseudoParent.getName());
                
                if (realParent == null) {
                    throw new FormatException("Couldn't find parent \"" + pseudoParent.getName() + "\" for variable "
                            + hierVar.getName());
                }
                
                hierVar.setParent(realParent);
                hierVar.setNestingDepth(previousNestingDepth);
            }
        }
        
        super.postProcess(varModel);
    }
    
    @Override
    protected void checkLength(@NonNull String @NonNull [] csv) throws FormatException {
        if (csv.length != HIERARCHICAL_SIZE) {
            throw new FormatException("Expected " +  HIERARCHICAL_SIZE + " fields");
        }
    }
    
}
