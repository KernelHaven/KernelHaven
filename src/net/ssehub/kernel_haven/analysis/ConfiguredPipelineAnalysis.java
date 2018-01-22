package net.ssehub.kernel_haven.analysis;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A {@link PipelineAnalysis} that reads the pipeline configuration from the user properties file and instantiates it
 * via reflection.
 * 
 * @author Adam
 */
public class ConfiguredPipelineAnalysis extends PipelineAnalysis {

    /**
     * Creates a new {@link ConfiguredPipelineAnalysis}.
     * 
     * @param config The global configuration.
     */
    public ConfiguredPipelineAnalysis(@NonNull Configuration config) {
        super(config);
    }

    @Override
    protected @NonNull AnalysisComponent<?> createPipeline() throws SetUpException {
        String configurationString = config.getValue(DefaultSettings.ANALYSIS_PIPELINE);
        return createComponent(configurationString);
    }
    
    /**
     * Creates an {@link AnalysisComponent} from the given configuration string. If the configuration string has the
     * following format:
     * <code>fully.qualified.Name(parameter.Component1(&lt;...&gt;), parameter.Component2(&lt;...&gt;))</code>
     * this method will will create the component "fully.qualified.Name" with the two given components as parameters.
     * 
     * @param configuration The configuration string.
     * @return The analysis component specified by the configuration string.
     * 
     * @throws SetUpException If creating the component fails or the string is malformed.
     */
    private @NonNull AnalysisComponent<?> createComponent(String configuration) throws SetUpException {
        configuration = configuration.trim();
        
        int openingBracket = configuration.indexOf('(');
        int closingBracket = configuration.lastIndexOf(')');
        
        if (openingBracket == -1 || closingBracket == -1) {
            throw new SetUpException("Couldn't find brackets in configuration string: " + configuration);
        }
        if (closingBracket != configuration.length() - 1) {
            throw new SetUpException("Closing bracket is not last character in configuration string: " + configuration);
        }
        
        String className = configuration.substring(0, openingBracket).trim();
        List<AnalysisComponent<?>> parameters = new ArrayList<>();
        
        String parameterString = configuration.substring(openingBracket + 1, closingBracket).trim();
        
        if (!parameterString.isEmpty()) {
            List<String> parameterParts = getParameterParts(parameterString);
            
            for (String param : parameterParts) {
                parameters.add(createComponent(param));
            }
        }
        
        AnalysisComponent<?> result;
        if (parameters.isEmpty() && className.equals("cmComponent")) {
            result = getCmComponent();
            
        } else if (parameters.isEmpty() && className.equals("bmComponent")) {
            result = getBmComponent();
            
        } else if (parameters.isEmpty() && className.equals("vmComponent")) {
            result = getVmComponent();
            
        } else {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends AnalysisComponent<?>> componentClass =
                        (Class<? extends AnalysisComponent<?>>) Class.forName(className);
                
                Object[] parameterValues = new Object[1 + parameters.size()];
                Class<?>[] parameterTypes = new Class[1 + parameters.size()];
                parameterTypes[0] = Configuration.class;
                parameterValues[0] = config;
                for (int i = 1; i < parameterTypes.length; i++) {
                    parameterTypes[i] = AnalysisComponent.class;
                    parameterValues[i] = parameters.get(i - 1);
                }
                
                result = notNull(componentClass.getConstructor(parameterTypes).newInstance(parameterValues));
                
            } catch (ReflectiveOperationException | ClassCastException | IllegalArgumentException e) {
                throw new SetUpException(e);
            }
        }
        
        return result;
    }
    
    /**
     * Splits the given string at each ',' that is not nested inside brackets.
     * 
     * @param parameterString The string that should be split.
     * @return The comma separated parts.
     */
    private List<String> getParameterParts(String parameterString) {
        List<String> result = new LinkedList<>();
        
        int depth = 0;
        int previous = -1;
        int current = -1;
        for (char c : parameterString.toCharArray()) {
            current++;
            switch (c) {
            case '(':
                depth++;
                break;
            case ')':
                depth--;
                break;
            case ',':
                if (depth == 0) {
                    result.add(parameterString.substring(previous + 1, current));
                    previous = current;
                }
                break;
            default:
                // ignore
            }
        }
        result.add(parameterString.substring(previous + 1));
        
        return result;
    }

}
