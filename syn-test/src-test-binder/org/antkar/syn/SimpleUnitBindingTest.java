/*
 * Copyright 2013 Anton Karmanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.antkar.syn;

import java.io.IOException;

import junit.framework.TestCase;

import org.antkar.syn.SynBinder;
import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.schema.simpleunit.SimpleEntity;
import org.antkar.syn.schema.simpleunit.SimpleMember;
import org.antkar.syn.schema.simpleunit.SimpleUnit;
import org.junit.Test;

/**
 * Unit tests for {@link SynBinder} used to bind {@link SimpleUnit} class.
 */
public class SimpleUnitBindingTest extends TestCase {

    @Test
    public void testCorrectText() throws SynException, IOException {
        SimpleUnit unit = parse("entity A { b : B; } entity B { c : C; } entity C { a : A; }");
        assertNotNull(unit);
        SimpleEntity[] entities = unit.getEntities();
        assertEquals(3, entities.length);
        
        assertEquals("A", entities[0].getName());
        SimpleMember[] members0 = entities[0].getMembers();
        assertEquals(1, members0.length);
        assertEquals("b", members0[0].getName());
        assertEquals("B", members0[0].getSfType());
        assertSame(entities[1], members0[0].getType());
        
        assertEquals("B", entities[1].getName());
        SimpleMember[] members1 = entities[1].getMembers();
        assertEquals(1, members1.length);
        assertEquals("c", members1[0].getName());
        assertEquals("C", members1[0].getSfType());
        assertSame(entities[2], members1[0].getType());

        assertEquals("C", entities[2].getName());
        SimpleMember[] members2 = entities[2].getMembers();
        assertEquals(1, members2.length);
        assertEquals("a", members2[0].getName());
        assertEquals("A", members2[0].getSfType());
        assertSame(entities[0], members2[0].getType());
    }
    
    @Test
    public void testNoEntities() throws SynException, IOException {
        SimpleUnit unit = parse("");
        assertNotNull(unit);
        SimpleEntity[] entities = unit.getEntities();
        assertEquals(0, entities.length);
    }

    @Test
    public void testNoMembers() throws SynException, IOException {
        SimpleUnit unit = parse("entity A { }");
        assertNotNull(unit);
        SimpleEntity[] entities = unit.getEntities();
        assertEquals(1, entities.length);
        assertEquals("A", entities[0].getName());
        SimpleMember[] members = entities[0].getMembers();
        assertEquals(0, members.length);
    }
    
    @Test
    public void testTwoEntitiesWithSameName() throws SynException, IOException {
        try {
            parse("entity A { } entity B { } entity A { }");
            fail();
        } catch (IllegalStateException e) {
            assertEquals("There is more than one entity with name A", e.getMessage());
        }
    }
    
    @Test
    public void testMembersWithSameNameInDifferentEntities() throws SynException, IOException {
        SimpleUnit unit = parse("entity A { x : X; } entity B { x : X; } entity X { }");
        assertNotNull(unit);
    }

    @Test
    public void testMembersWithSameNameInSameEntity() throws SynException, IOException {
        try {
            parse("entity A { x : X; x : X; } entity X { }");
            fail();
        } catch (IllegalStateException e) {
            assertEquals("There is more than one member A.x", e.getMessage());
        }
    }

    @Test
    public void testMembersWithSameTypeInSameEntity() throws SynException, IOException {
        SimpleUnit unit = parse("entity A { x1 : X; x2 : X; } entity X { }");
        assertNotNull(unit);
    }

    @Test
    public void testWrongTypeName() throws SynException, IOException {
        try {
            parse("entity A { x : X; }");
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Name 'X' is used as the type of field A.x, but it is not defined", e.getMessage());
        }
    }
    
    @Test
    public void testTextPosBinding() throws SynException, IOException {
        SimpleUnit unit = parse("entity Ent123 { attrib123 : Ent123; } ");
        assertNotNull(unit);
        SimpleEntity[] entities = unit.getEntities();
        assertEquals(1, entities.length);
        SimpleEntity entity = entities[0];
        SimpleMember[] members = entity.getMembers();
        assertEquals(1, members.length);
        SimpleMember member = members[0];
        
        TextPos pos = member.getPos();
        assertNotNull(pos);
        assertEquals(1, pos.getLine());
        assertEquals(27, pos.getColumn());
        assertEquals(1, pos.getLength());
        
        TextPos namePos = member.getNamePos();
        assertNotNull(namePos);
        assertEquals(1, namePos.getLine());
        assertEquals(17, namePos.getColumn());
        assertEquals(9, namePos.getLength());
        
        TextPos typePos = member.getTypePos();
        assertNotNull(typePos);
        assertEquals(1, typePos.getLine());
        assertEquals(29, typePos.getColumn());
        assertEquals(6, typePos.getLength());
    }

    private static SimpleUnit parse(String text) throws SynException, IOException {
        SimpleUnit unit = SynBinderTest.parse(SimpleUnit.class, "simpleunit_grammar.txt", text);
        return unit;
    }
}
