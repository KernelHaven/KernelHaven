package net.ssehub.kernel_haven.code_model;

/**
 * A syntax element that represents a literal value.
 * 
 * @author Adam
 */
public class LiteralSyntaxElement implements ISyntaxElementType {

    private String content;
    
    /**
     * Creates a new literal syntax element.
     * 
     * @param content The content of this literal.
     */
    public LiteralSyntaxElement(String content) {
        this.content = content;
    }
    
    /**
     * Retrieves the content of this literal.
     * 
     * @return The content of this literal.
     */
    public String getContent() {
        return content;
    }
    
    @Override
    public String toString() {
        return "Literal: " + content;
    }

}
