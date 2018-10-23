package net.ssehub.kernel_haven.util.io.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;

/**
 * Tests the {@link JsonParser}.
 * 
 * @author Adam
 */
public class JsonParserTest {

    /**
     * Tests the example from the JSON wikipedia page.
     * 
     * @throws IOException unwanted.
     * @throws FormatException unwanted.
     */
    @Test
    public void testWikipediaExample() throws IOException, FormatException {
        String json = "{\n"
                + "  \"firstName\": \"John\",\n"
                + "  \"lastName\": \"Smith\",\n"
                + "  \"isAlive\": true,\n"
                + "  \"age\": 27,\n"
                + "  \"address\": {\n"
                + "    \"streetAddress\": \"21 2nd Street\",\n"
                + "    \"city\": \"New York\",\n"
                + "    \"state\": \"NY\",\n"
                + "    \"postalCode\": \"10021-3100\"\n"
                + "  },\n"
                + "  \"phoneNumbers\": [\n"
                + "    {\n"
                + "      \"type\": \"home\",\n"
                + "      \"number\": \"212 555-1234\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"type\": \"office\",\n"
                + "      \"number\": \"646 555-4567\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"type\": \"mobile\",\n"
                + "      \"number\": \"123 456-7890\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"children\": [],\n"
                + "  \"spouse\": null\n"
                + "}";
                
        JsonObject expected = new JsonObject();
        expected.putElement("firstName", new JsonString("John"));
        expected.putElement("lastName", new JsonString("Smith"));
        expected.putElement("isAlive", JsonBoolean.TRUE);
        expected.putElement("age", new JsonNumber(27));
        expected.putElement("children", new JsonList());
        expected.putElement("spouse", JsonNull.INSTANCE);
            
        JsonList phoneNumbers = new JsonList();
        JsonObject p1 = new JsonObject();
        p1.putElement("type", new JsonString("home"));
        p1.putElement("number", new JsonString("212 555-1234"));
        JsonObject p2 = new JsonObject();
        p2.putElement("type", new JsonString("office"));
        p2.putElement("number", new JsonString("646 555-4567"));
        JsonObject p3 = new JsonObject();
        p3.putElement("type", new JsonString("mobile"));
        p3.putElement("number", new JsonString("123 456-7890"));
        
        phoneNumbers.addElement(p1);
        phoneNumbers.addElement(p2);
        phoneNumbers.addElement(p3);
        expected.putElement("phoneNumbers", phoneNumbers);
        
        JsonObject address = new JsonObject();
        address.putElement("streetAddress", new JsonString("21 2nd Street"));
        address.putElement("city", new JsonString("New York"));
        address.putElement("state", new JsonString("NY"));
        address.putElement("postalCode", new JsonString("10021-3100"));
        expected.putElement("address", address);
        
        try (JsonParser parser = new JsonParser(new StringReader(json))) {
            JsonElement result = parser.parse();
            assertThat(result, is(expected));
        }
    }
    
}
