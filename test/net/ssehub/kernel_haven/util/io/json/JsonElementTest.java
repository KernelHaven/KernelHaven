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
package net.ssehub.kernel_haven.util.io.json;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;

/**
 * Tests the structure classes inheriting from {@link JsonElement}.
 *
 * @author Adam
 */
public class JsonElementTest {

    /**
     * Tests the getString() method {@link JsonObject}.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testObjectGetString() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonString("b"));
        
        assertThat(obj.getString("a"), is("b"));
    }
    
    /**
     * Tests the getString() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetStringNoKey() throws FormatException {
        JsonObject obj = new JsonObject();
        
        obj.getString("a");
    }
    
    /**
     * Tests the getString() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetStringWrongType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(2));
        
        obj.getString("a");
    }
    
    /**
     * Tests the getBoolean() method {@link JsonObject}.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testObjectGetBoolean() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", JsonBoolean.TRUE);
        
        assertThat(obj.getBoolean("a"), is(true));
    }
    
    /**
     * Tests the getBoolean() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetBooleanNoKey() throws FormatException {
        JsonObject obj = new JsonObject();
        
        obj.getBoolean("a");
    }
    
    /**
     * Tests the getBoolean() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetBooleanWrongType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(2));
        
        obj.getBoolean("a");
    }
    
    /**
     * Tests the getInt() method {@link JsonObject}.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testObjectGetInt() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(4));
        
        assertThat(obj.getInt("a"), is(4));
    }
    
    /**
     * Tests the getInt() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetIntNoKey() throws FormatException {
        JsonObject obj = new JsonObject();
        
        obj.getInt("a");
    }
    
    /**
     * Tests the getInt() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetIntWrongType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonString("abc"));
        
        obj.getInt("a");
    }
    
    /**
     * Tests the getInt() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetIntWrongNumberType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(2.5));
        
        obj.getInt("a");
    }
    
    /**
     * Tests the getLong() method {@link JsonObject}.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testObjectGetLong() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(4));
        
        assertThat(obj.getLong("a"), is(4L));
        
        obj.putElement("b", new JsonNumber(12321321L));
        
        assertThat(obj.getLong("b"), is(12321321L));
    }
    
    /**
     * Tests the getLong() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetLongNoKey() throws FormatException {
        JsonObject obj = new JsonObject();
        
        obj.getLong("a");
    }
    
    /**
     * Tests the getLong() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetLongWrongType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonString("abc"));
        
        obj.getLong("a");
    }
    
    /**
     * Tests the getLong() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetLongWrongNumberType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(2.5));
        
        obj.getLong("a");
    }
    
    /**
     * Tests the getDouble() method {@link JsonObject}.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testObjectGetDouble() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(4.2));
        
        assertThat(obj.getDouble("a"), is(4.2));
    }
    
    /**
     * Tests the getDouble() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetDoubleNoKey() throws FormatException {
        JsonObject obj = new JsonObject();
        
        obj.getDouble("a");
    }
    
    /**
     * Tests the getDouble() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetDoubleWrongType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonString("abc"));
        
        obj.getDouble("a");
    }
    
    /**
     * Tests the getDouble() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetDoubleWrongNumberType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(2));
        
        obj.getDouble("a");
    }
    
    /**
     * Tests the getList() method {@link JsonObject}.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testObjectGetList() throws FormatException {
        JsonObject obj = new JsonObject();
        JsonList l = new JsonList();
        l.addElement(new JsonNumber(1));
        l.addElement(new JsonNumber(2));
        obj.putElement("a", l);
        
        assertThat(obj.getList("a"), is(l));
    }
    
    /**
     * Tests the getList() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetListNoKey() throws FormatException {
        JsonObject obj = new JsonObject();
        
        obj.getList("a");
    }
    
    /**
     * Tests the getList() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetListWrongType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(2));
        
        obj.getList("a");
    }
    
    /**
     * Tests the getObject() method {@link JsonObject}.
     * 
     * @throws FormatException unwanted.
     */
    @Test
    public void testObjectGetObject() throws FormatException {
        JsonObject obj = new JsonObject();
        JsonObject o1 = new JsonObject();
        o1.putElement("abc", new JsonNumber(1));
        o1.putElement("def", new JsonNumber(2));
        obj.putElement("a", o1);
        
        assertThat(obj.getObject("a"), is(o1));
    }
    
    /**
     * Tests the getObject() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetObjectNoKey() throws FormatException {
        JsonObject obj = new JsonObject();
        
        obj.getObject("a");
    }
    
    /**
     * Tests the getObject() method {@link JsonObject}.
     * 
     * @throws FormatException wanted.
     */
    @Test(expected = FormatException.class)
    public void testObjectGetObjectWrongType() throws FormatException {
        JsonObject obj = new JsonObject();
        obj.putElement("a", new JsonNumber(2));
        
        obj.getObject("a");
    }
    
    /**
     * Tests the basic set operations of {@link JsonObject}.
     */
    @Test
    public void testObjectSetOpreations() {
        JsonObject obj = new JsonObject();
        
        assertThat(obj.getSize(), is(0));
        Iterator<Entry<String, JsonElement>> it = obj.iterator();
        assertThat(it.hasNext(), is(false));
        
        obj.putElement("a", new JsonNumber(2));
        
        assertThat(obj.getSize(), is(1));
        assertThat(obj.getElement("a"), is(new JsonNumber(2)));
        it = obj.iterator();
        assertThat(it.next().getKey(), is("a"));
        assertThat(it.hasNext(), is(false));
        
        obj.removeElement("a");
        
        assertThat(obj.getSize(), is(0));
        
        obj.putElement("a", new JsonNumber(2));
        obj.putElement("a", new JsonNumber(3));
        obj.putElement("b", new JsonNumber(2));
        
        assertThat(obj.getSize(), is(2));
        assertThat(obj.getElement("a"), is(new JsonNumber(3)));
        assertThat(obj.getElement("b"), is(new JsonNumber(2)));
        it = obj.iterator();
        assertThat(it.next().getKey(), is("a"));
        assertThat(it.next().getKey(), is("b"));
        assertThat(it.hasNext(), is(false));
    }
    
    /**
     * Tests the basic list operations of {@link JsonList}.
     */
    // CHECKSTYLE:OFF // method length
    @Test
    // CHECKSTYLE:ON
    public void testListListOperations() {
        JsonList l = new JsonList();
        
        assertThat(l.getSize(), is(0));
        Iterator<JsonElement> it = l.iterator();
        assertThat(it.hasNext(), is(false));
        
        l.addElement(new JsonNumber(2));
        
        assertThat(l.getSize(), is(1));
        assertThat(l.getElement(0), is(new JsonNumber(2)));
        it = l.iterator();
        assertThat(it.next(), is(new JsonNumber(2)));
        assertThat(it.hasNext(), is(false));
        
        l.addElement(new JsonString("abc"));
        l.addElement(new JsonObject());
        
        assertThat(l.getSize(), is(3));
        assertThat(l.getElement(0), is(new JsonNumber(2)));
        assertThat(l.getElement(1), is(new JsonString("abc")));
        assertThat(l.getElement(2), is(new JsonObject()));
        it = l.iterator();
        assertThat(it.next(), is(new JsonNumber(2)));
        assertThat(it.next(), is(new JsonString("abc")));
        assertThat(it.next(), is(new JsonObject()));
        assertThat(it.hasNext(), is(false));
        
        l.setElement(1, new JsonNumber(0));
        
        assertThat(l.getSize(), is(3));
        assertThat(l.getElement(0), is(new JsonNumber(2)));
        assertThat(l.getElement(1), is(new JsonNumber(0)));
        assertThat(l.getElement(2), is(new JsonObject()));
        it = l.iterator();
        assertThat(it.next(), is(new JsonNumber(2)));
        assertThat(it.next(), is(new JsonNumber(0)));
        assertThat(it.next(), is(new JsonObject()));
        assertThat(it.hasNext(), is(false));
        
        l.removeElement(0);
        
        assertThat(l.getSize(), is(2));
        assertThat(l.getElement(0), is(new JsonNumber(0)));
        assertThat(l.getElement(1), is(new JsonObject()));
        it = l.iterator();
        assertThat(it.next(), is(new JsonNumber(0)));
        assertThat(it.next(), is(new JsonObject()));
        assertThat(it.hasNext(), is(false));
        
        try {
            l.getElement(2);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            l.getElement(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
        
        try {
            l.removeElement(2);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            l.removeElement(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
        
        try {
            l.setElement(2, new JsonObject());
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            l.setElement(-1, new JsonObject());
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }
    }
    
    /**
     * Tests the trivial methods of {@link JsonNull}.
     */
    @Test
    public void testJsonNull() {
        JsonNull nul = JsonNull.INSTANCE;
        
        assertThat(nul.getValue(), sameInstance(JsonNull.INSTANCE));
    }
    
    /**
     * Tests the trivial methods of {@link JsonBoolean}.
     */
    @Test
    public void testJsonBoolean() {
        JsonBoolean bool = JsonBoolean.get(true);
        assertThat(bool.getValue(), is(true));
        
        bool = JsonBoolean.get(false);
        assertThat(bool.getValue(), is(false));
    }
    
    /**
     * Tests the equals() and hashCode() methods of {@link JsonBoolean}.
     */
    @Test
    @SuppressWarnings("null")
    public void testEqualsBoolean() {
        assertThat(JsonBoolean.TRUE, is(JsonBoolean.TRUE));
        assertThat(JsonBoolean.FALSE, is(JsonBoolean.FALSE));
        assertThat(JsonBoolean.FALSE, not(is(JsonBoolean.TRUE)));
        assertThat(JsonBoolean.TRUE, not(is(JsonBoolean.FALSE)));
        
        assertThat(JsonBoolean.TRUE, not(is(new JsonString("true"))));
        assertThat(JsonBoolean.TRUE, not(is(new JsonNumber(1))));
        assertThat(JsonBoolean.FALSE, not(is(new JsonString("false"))));
        assertThat(JsonBoolean.FALSE, not(is(new JsonNumber(0))));
        
        assertThat(JsonBoolean.TRUE.hashCode(), is(JsonBoolean.TRUE.hashCode()));
        assertThat(JsonBoolean.FALSE.hashCode(), is(JsonBoolean.FALSE.hashCode()));
        assertThat(JsonBoolean.FALSE.hashCode(), not(is(JsonBoolean.TRUE.hashCode())));
        assertThat(JsonBoolean.TRUE.hashCode(), not(is(JsonBoolean.FALSE.hashCode())));
    }
    
    /**
     * Tests the equals() and hashCode() methods of {@link JsonNull}.
     */
    @Test
    @SuppressWarnings("null")
    public void testEqualsNull() {
        assertThat(JsonNull.INSTANCE, is(JsonNull.INSTANCE));
        
        assertThat(JsonNull.INSTANCE, not(is(JsonBoolean.TRUE)));
        assertThat(JsonNull.INSTANCE, not(is(JsonBoolean.FALSE)));
        assertThat(JsonNull.INSTANCE, not(is(new JsonString("null"))));
        assertThat(JsonNull.INSTANCE, not(is(new JsonNumber(0))));
        
        assertThat(JsonNull.INSTANCE.hashCode(), is(JsonNull.INSTANCE.hashCode()));
        assertThat(JsonNull.INSTANCE.hashCode(), not(is(JsonBoolean.TRUE.hashCode())));
        assertThat(JsonNull.INSTANCE.hashCode(), not(is(JsonBoolean.FALSE.hashCode())));
    }
    
    /**
     * Tests the equals() and hashCode() methods of {@link JsonNumber}.
     */
    @Test
    @SuppressWarnings("null")
    public void testEqualsNumber() {
        assertThat(new JsonNumber(1), is(new JsonNumber(1)));
        assertThat(new JsonNumber(1L), is(new JsonNumber(1L)));
        assertThat(new JsonNumber(-2.5), is(new JsonNumber(-2.5)));

        assertThat(new JsonNumber(1), not(is(new JsonString("1"))));
        assertThat(new JsonNumber(1), not(is(JsonBoolean.TRUE)));
        
        assertThat(new JsonNumber(1).hashCode(), is(new JsonNumber(1).hashCode()));
        assertThat(new JsonNumber(25L).hashCode(), is(new JsonNumber(25L).hashCode()));
        assertThat(new JsonNumber(-123.5).hashCode(), is(new JsonNumber(-123.5).hashCode()));
        
        assertThat(new JsonNumber(1).hashCode(), not(is(new JsonNumber(2).hashCode())));
    }
    
    /**
     * Tests the equals() and hashCode() methods of {@link JsonString}.
     */
    @Test
    public void testEqualsString() {
        assertThat(new JsonString("abc"), is(new JsonString("abc")));
        assertThat(new JsonString(""), is(new JsonString("")));
        
        assertThat(new JsonString(""), not(is(new JsonString("a"))));
        assertThat(new JsonString("b"), not(is(new JsonString("a"))));
        assertThat(new JsonString("5"), not(is(new JsonNumber(5))));
        
        assertThat(new JsonString("abc").hashCode(), is(new JsonString("abc").hashCode()));
        assertThat(new JsonString("").hashCode(), is(new JsonString("").hashCode()));
        assertThat(new JsonString("").hashCode(), not(is(new JsonString("a").hashCode())));
        assertThat(new JsonString("b").hashCode(), not(is(new JsonString("a").hashCode())));
        assertThat(new JsonString("5").hashCode(), not(is(new JsonNumber(5).hashCode())));
    }
    
    /**
     * Tests the equals() and hashCode() methods of {@link JsonList}.
     */
    @Test
    public void testEqualsList() {
        JsonList l1 = new JsonList();
        JsonList l2 = new JsonList();
        assertThat(l1, is(l2));
        assertThat(l1.hashCode(), is(l2.hashCode()));
        
        l1.addElement(new JsonNumber(1));
        assertThat(l1, not(is(l2)));
        assertThat(l1.hashCode(), not(is(l2.hashCode())));
        
        l2.addElement(new JsonNumber(1));
        assertThat(l1, is(l2));
        assertThat(l1.hashCode(), is(l2.hashCode()));
        
        l1.addElement(new JsonString("a"));
        l2.addElement(new JsonString("b"));
        assertThat(l1, not(is(l2)));
        assertThat(l1.hashCode(), not(is(l2.hashCode())));
        
        l1 = new JsonList();
        l2 = new JsonList();
        l1.addElement(new JsonNumber(1));
        l1.addElement(new JsonNumber(2));
        l2.addElement(new JsonNumber(2));
        l2.addElement(new JsonNumber(1));
        assertThat(l1, not(is(l2)));
        assertThat(l1.hashCode(), not(is(l2.hashCode())));
        
        assertThat(new JsonList(), not(is(new JsonString("[]"))));
    }
    
    /**
     * Tests the equals() and hashCode() methods of {@link JsonObject}.
     */
    @Test
    public void testEqualsObject() {
        JsonObject o1 = new JsonObject();
        JsonObject o2 = new JsonObject();
        assertThat(o1, is(o2));
        assertThat(o1.hashCode(), is(o2.hashCode()));
        
        o1.putElement("a", new JsonNumber(1));
        assertThat(o1, not(is(o2)));
        assertThat(o1.hashCode(), not(is(o2.hashCode())));
        
        o2.putElement("a", new JsonNumber(1));
        assertThat(o1, is(o2));
        assertThat(o1.hashCode(), is(o2.hashCode()));
        
        o1.putElement("b", new JsonString("b"));
        o2.putElement("c", new JsonString("b"));
        assertThat(o1, not(is(o2)));
        assertThat(o1.hashCode(), not(is(o2.hashCode())));
        
        o2.removeElement("c");
        o2.putElement("b", new JsonString("b"));
        assertThat(o1, is(o2));
        assertThat(o1.hashCode(), is(o2.hashCode()));
        
        assertThat(new JsonObject(), not(is(new JsonString("{}"))));
    }
    
}
