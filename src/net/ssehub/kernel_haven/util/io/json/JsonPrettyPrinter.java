package net.ssehub.kernel_haven.util.io.json;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.Map;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A visitor for printing out JSON with proper line breaks and indentation.
 * 
 * @author Adam
 */
public class JsonPrettyPrinter implements JsonVisitor<@NonNull String> {

    @Override
    public @NonNull String visitObject(@NonNull JsonObject object) {
        StringBuilder result = new StringBuilder();
        
        if (object.getSize() == 0) {
            result.append("{}");
            
        } else {
            result.append("{\n");
            for (Map.Entry<String, JsonElement> element : object) {
                
                result.append("\t").append(new JsonString(notNull(element.getKey())).accept(this)).append(": ");
                String[] lines = notNull(element.getValue()).accept(this).split("\n");
                
                boolean first = true;
                for (String line : lines) {
                    if (first) {
                        first = false;
                    } else {
                        result.append("\t");
                    }
                    result.append(line).append("\n");
                }
                result.delete(result.length() - 1, result.length()); // remove trailing "\n"
                
                result.append(",\n");
                
            }
            result.delete(result.length() - 2, result.length()); // remove trailing ",\n"
            result.append("\n}");
        }
        
        
        return notNull(result.toString());
    }

    @Override
    public @NonNull String visitList(@NonNull JsonList list) {
        StringBuilder result = new StringBuilder();
        
        if (list.getSize() == 0) {
            result.append("[]");
            
        } else {
            result.append("[\n");
            for (JsonElement element : list) {
                
                String[] lines = element.accept(this).split("\n");
                
                for (String line : lines) {
                    result.append("\t").append(line).append("\n");
                }
                result.delete(result.length() - 1, result.length()); // remove trailing "\n"
                
                result.append(",\n");
                
            }
            result.delete(result.length() - 2, result.length()); // remove trailing ",\n"
            result.append("\n]");
        }
        
        
        return notNull(result.toString());
    }

    @Override
    public @NonNull String visitBoolean(@NonNull JsonBoolean bool) {
        return notNull(String.valueOf(bool.getValue()));
    }

    @Override
    public @NonNull String visitNumber(@NonNull JsonNumber number) {
        return notNull(String.valueOf(number.getValue()));
    }

    @Override
    public @NonNull String visitString(@NonNull JsonString string) {
        return '"' + JsonString.jsonEscape(string.getValue()) + '"';
    }

    @Override
    public @NonNull String visitNull(@NonNull JsonNull nall) {
        return "null";
    }

}
