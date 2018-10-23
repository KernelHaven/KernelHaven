package net.ssehub.kernel_haven.util.io.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests the {@link JsonElement#toString()} method.
 *
 * @author Adam
 */
public class JsonToStringTest {

    /**
     * Tests the simple value types.
     */
    @Test
    public void testSimpleTypes() {
        assertThat(JsonBoolean.TRUE.toString(), is("true"));
        assertThat(JsonBoolean.FALSE.toString(), is("false"));
        
        assertThat(JsonNull.INSTANCE.toString(), is("null"));
        
        assertThat(new JsonNumber(42).toString(), is("42"));
        assertThat(new JsonNumber(4253432432432L).toString(), is("4253432432432"));
        assertThat(new JsonNumber(-42).toString(), is("-42"));
        assertThat(new JsonNumber(42.34).toString(), is("42.34"));
        assertThat(new JsonNumber(42.2345E-2).toString(), is("0.422345"));
        assertThat(new JsonNumber(-42.2345E-2).toString(), is("-0.422345"));
        
        // TODO: what to do about these?
        assertThat(new JsonNumber(Double.POSITIVE_INFINITY).toString(), is("Infinity"));
        assertThat(new JsonNumber(Double.NEGATIVE_INFINITY).toString(), is("-Infinity"));
        assertThat(new JsonNumber(Double.NaN).toString(), is("NaN"));
        
        assertThat(new JsonString("Hello World!").toString(), is("\"Hello World!\""));
        assertThat(new JsonString("A \" B").toString(), is("\"A \\\" B\""));
        assertThat(new JsonString("A \\ B").toString(), is("\"A \\\\ B\""));
        assertThat(new JsonString("A \b B").toString(), is("\"A \\b B\""));
        assertThat(new JsonString("A \n B").toString(), is("\"A \\n B\""));
        assertThat(new JsonString("A \r B").toString(), is("\"A \\r B\""));
        assertThat(new JsonString("A \t B").toString(), is("\"A \\t B\""));
        assertThat(new JsonString("A \f B").toString(), is("\"A \\f B\""));
    }
    
    /**
     * Tests the list type.
     */
    @Test
    public void testLists() {
        assertThat(new JsonList().toString(), is("[]"));
        
        JsonList l = new JsonList();
        l.addElement(new JsonNumber(42));
        assertThat(l.toString(), is("[ 42 ]"));
        
        l.addElement(new JsonString("Hello World"));
        assertThat(l.toString(), is("[ 42, \"Hello World\" ]"));
        
        l.addElement(JsonNull.INSTANCE);
        assertThat(l.toString(), is("[ 42, \"Hello World\", null ]"));
    }
    
    /**
     * Tests the object type.
     */
    @Test
    public void testObjects() {
        assertThat(new JsonObject().toString(), is("{}"));
        
        JsonObject o = new JsonObject();
        o.putElement("a", new JsonNumber(42));
        assertThat(o.toString(), is("{ \"a\": 42 }"));
        
        o.putElement("", new JsonString("Hello World"));
        assertThat(o.toString(), is("{ \"\": \"Hello World\", \"a\": 42 }"));
        
        o.putElement("b", JsonNull.INSTANCE);
        assertThat(o.toString(), is("{ \"\": \"Hello World\", \"a\": 42, \"b\": null }"));
    }
    
    /**
     * Tests the {@link JsonPrettyPrinter} with simple types.
     */
    @Test
    public void testPrettyPrinterSimpleTypes() {
        JsonPrettyPrinter p = new JsonPrettyPrinter();
        
        assertThat(JsonBoolean.TRUE.accept(p), is("true"));
        assertThat(JsonBoolean.FALSE.accept(p), is("false"));
        
        assertThat(JsonNull.INSTANCE.accept(p), is("null"));
        
        assertThat(new JsonNumber(42).accept(p), is("42"));
        assertThat(new JsonNumber(4253432432432L).accept(p), is("4253432432432"));
        assertThat(new JsonNumber(-42).accept(p), is("-42"));
        assertThat(new JsonNumber(42.34).accept(p), is("42.34"));
        assertThat(new JsonNumber(42.2345E-2).accept(p), is("0.422345"));
        assertThat(new JsonNumber(-42.2345E-2).accept(p), is("-0.422345"));
        
        // TODO: what to do about these?
        assertThat(new JsonNumber(Double.POSITIVE_INFINITY).accept(p), is("Infinity"));
        assertThat(new JsonNumber(Double.NEGATIVE_INFINITY).accept(p), is("-Infinity"));
        assertThat(new JsonNumber(Double.NaN).accept(p), is("NaN"));
        
        assertThat(new JsonString("Hello World!").accept(p), is("\"Hello World!\""));
        assertThat(new JsonString("A \" B").accept(p), is("\"A \\\" B\""));
        assertThat(new JsonString("A \\ B").accept(p), is("\"A \\\\ B\""));
        assertThat(new JsonString("A \b B").accept(p), is("\"A \\b B\""));
        assertThat(new JsonString("A \n B").accept(p), is("\"A \\n B\""));
        assertThat(new JsonString("A \r B").accept(p), is("\"A \\r B\""));
        assertThat(new JsonString("A \t B").accept(p), is("\"A \\t B\""));
        assertThat(new JsonString("A \f B").accept(p), is("\"A \\f B\""));
    }
    
    /**
     * Tests the {@link JsonPrettyPrinter} with the list type.
     */
    @Test
    public void testPrettyPrinterLists() {
        JsonPrettyPrinter p = new JsonPrettyPrinter();
        
        assertThat(new JsonList().accept(p), is("[]"));
        
        JsonList l = new JsonList();
        l.addElement(new JsonNumber(42));
        assertThat(l.accept(p), is("[\n\t42\n]"));
        
        l.addElement(new JsonString("Hello World"));
        assertThat(l.accept(p), is("[\n\t42,\n\t\"Hello World\"\n]"));
        
        l.addElement(JsonNull.INSTANCE);
        assertThat(l.accept(p), is("[\n\t42,\n\t\"Hello World\",\n\tnull\n]"));
    }
    
    /**
     * Tests the {@link JsonPrettyPrinter} with the object type.
     */
    @Test
    public void testPrettyPrinterObjects() {
        JsonPrettyPrinter p = new JsonPrettyPrinter();
        
        assertThat(new JsonObject().accept(p), is("{}"));
        
        JsonObject o = new JsonObject();
        o.putElement("a", new JsonNumber(42));
        assertThat(o.accept(p), is("{\n\t\"a\": 42\n}"));
        
        o.putElement("", new JsonString("Hello World"));
        assertThat(o.accept(p), is("{\n\t\"\": \"Hello World\",\n\t\"a\": 42\n}"));
        
        o.putElement("b", JsonNull.INSTANCE);
        assertThat(o.accept(p), is("{\n\t\"\": \"Hello World\",\n\t\"a\": 42,\n\t\"b\": null\n}"));
    }
    
   /**
    * Tests the {@link JsonPrettyPrinter} with mixed object and list types.
    */
    @Test
   public void testPrettyPrinterMixed() {
        JsonPrettyPrinter p = new JsonPrettyPrinter();
           
        JsonObject o1 = new JsonObject();
        o1.putElement("a", new JsonString("d"));
        o1.putElement("b", new JsonString("e"));
        o1.putElement("c", new JsonString("f"));
        
        JsonObject o2 = new JsonObject();
        o2.putElement("k1", new JsonString("str"));
        o2.putElement("k2", JsonBoolean.TRUE);
        o2.putElement("k3", new JsonNumber(42));
        
        JsonList l = new JsonList();
        l.addElement(new JsonNumber(-1));
        l.addElement(o1);
        l.addElement(new JsonString("intermediate"));
        l.addElement(o2);
        l.addElement(new JsonNumber(5));
        
        JsonObject top = new JsonObject();
        top.putElement("list", l);
        top.putElement("version", new JsonNumber(1));
        
        assertThat(top.accept(p), is(
                "{\n"
                + "\t\"list\": [\n"
                + "\t\t-1,\n"
                + "\t\t{\n"
                + "\t\t\t\"a\": \"d\",\n"
                + "\t\t\t\"b\": \"e\",\n"
                + "\t\t\t\"c\": \"f\"\n"
                + "\t\t},\n"
                + "\t\t\"intermediate\",\n"
                + "\t\t{\n"
                + "\t\t\t\"k1\": \"str\",\n"
                + "\t\t\t\"k2\": true,\n"
                + "\t\t\t\"k3\": 42\n"
                + "\t\t},\n"
                + "\t\t5\n"
                + "\t],\n"
                + "\t\"version\": 1\n"
                + "}"));
    }
    
}
