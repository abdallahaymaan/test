package com.pidima.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class PrefixTreeTests {

    private void title(TestInfo info) {
        System.out.println("\n============================================================");
        System.out.println("[TEST] " + info.getDisplayName());
        System.out.println("============================================================");
    }

    private void step(String message) {
        System.out.println("[STEP] " + message);
    }

    private void state(String message) {
        System.out.println("[STATE] " + message);
    }

    private void expect(String message) {
        System.out.println("[EXPECT] " + message);
    }

    private void result(String message) {
        System.out.println("[RESULT] " + message);
    }

    @Test
    @DisplayName("insert and lookup should store and retrieve values")
    void insertAndLookup(TestInfo info) {
        title(info);
        PrefixTree<Integer> trie = new PrefixTree<>();

        state("initial size=" + trie.size());
        step("lookup key 'app' before insert");
        Integer initial = trie.lookup("app");
        result("lookup('app') -> " + initial);
        expect("null because key not present");
        assertNull(initial);

        step("insert key 'app' with value 1");
        Integer prev = trie.insert("app", 1);
        result("insert returned previous value -> " + prev);
        expect("null because key was absent");
        assertNull(prev);
        state("size=" + trie.size());
        assertEquals(1, trie.size());

        step("lookup key 'app' after insert");
        Integer val = trie.lookup("app");
        result("lookup('app') -> " + val);
        expect("1");
        assertEquals(1, val);

        step("upsert key 'app' with new value 2");
        Integer previous = trie.insert("app", 2);
        result("insert returned previous value -> " + previous);
        expect("1 because existing value is replaced");
        assertEquals(1, previous);
        state("size stays constant=" + trie.size());
        assertEquals(1, trie.size());

        step("lookup key 'app' after upsert");
        Integer val2 = trie.lookup("app");
        result("lookup('app') -> " + val2);
        expect("2");
        assertEquals(2, val2);
    }

    @Test
    @DisplayName("create should only add when key absent")
    void createOnlyIfAbsent(TestInfo info) {
        title(info);
        PrefixTree<String> trie = new PrefixTree<>();

        step("create key 'car' with value 'sedan'");
        boolean created1 = trie.create("car", "sedan");
        result("create returned -> " + created1);
        expect("true because key was absent");
        assertTrue(created1);
        state("size=" + trie.size());

        step("create same key 'car' with value 'hatch'");
        boolean created2 = trie.create("car", "hatch");
        result("create returned -> " + created2);
        expect("false because key already exists");
        assertFalse(created2);

        step("lookup key 'car'");
        String val = trie.lookup("car");
        result("lookup('car') -> " + val);
        expect("'sedan' original value unchanged");
        assertEquals("sedan", val);
        state("size=" + trie.size());
        assertEquals(1, trie.size());
    }

    @Test
    @DisplayName("update should change existing key and fail for missing key")
    void updateExistingOnly(TestInfo info) {
        title(info);
        PrefixTree<String> trie = new PrefixTree<>();

        step("attempt update on missing key 'x'");
        boolean updatedMissing = trie.update("x", "1");
        result("update returned -> " + updatedMissing);
        expect("false because key not present");
        assertFalse(updatedMissing);

        step("insert key 'x' with value 'old'");
        trie.insert("x", "old");
        state("size=" + trie.size());

        step("update existing key 'x' to 'new'");
        boolean updated = trie.update("x", "new");
        result("update returned -> " + updated);
        expect("true");
        assertTrue(updated);

        step("lookup key 'x' after update");
        String val = trie.lookup("x");
        result("lookup('x') -> " + val);
        expect("'new'");
        assertEquals("new", val);
        state("size remains=" + trie.size());
        assertEquals(1, trie.size());
    }

    @Test
    @DisplayName("delete should remove key and prune nodes")
    void deleteKey(TestInfo info) {
        title(info);
        PrefixTree<Integer> trie = new PrefixTree<>();

        step("insert keys 'to':1, 'tea':2, 'ten':3");
        trie.insert("to", 1);
        trie.insert("tea", 2);
        trie.insert("ten", 3);
        state("size=" + trie.size());
        assertEquals(3, trie.size());

        step("attempt delete of non-terminal 'te'");
        Integer removedNone = trie.delete("te");
        result("delete('te') -> " + removedNone);
        expect("null because 'te' was not a key");
        assertNull(removedNone);
        state("size still=" + trie.size());
        assertEquals(3, trie.size());

        step("delete key 'tea'");
        Integer removed = trie.delete("tea");
        result("delete('tea') -> " + removed);
        expect("2 as the removed value");
        assertEquals(2, removed);
        state("size now=" + trie.size());
        assertEquals(2, trie.size());

        step("verify remaining keys");
        Integer to = trie.lookup("to");
        Integer ten = trie.lookup("ten");
        Integer tea = trie.lookup("tea");
        result("lookup('to') -> " + to + ", lookup('ten') -> " + ten + ", lookup('tea') -> " + tea);
        expect("1, 3, and null respectively");
        assertEquals(1, to);
        assertEquals(3, ten);
        assertNull(tea);
    }

    @Nested
    class MixedOperations {
        @Test
        @DisplayName("mixed scenario with create, update, delete, and lookups")
        void scenario(TestInfo info) {
            title(info);
            PrefixTree<String> trie = new PrefixTree<>();

            step("create 'abc'->'A' and 'abd'->'B'");
            assertTrue(trie.create("abc", "A"));
            assertTrue(trie.create("abd", "B"));
            state("size=" + trie.size());
            assertEquals(2, trie.size());

            step("lookup existing keys and a non-key prefix");
            String abc = trie.lookup("abc");
            String abd = trie.lookup("abd");
            String ab = trie.lookup("ab");
            result("abc=" + abc + ", abd=" + abd + ", ab=" + ab);
            expect("'A', 'B', null");
            assertEquals("A", abc);
            assertEquals("B", abd);
            assertNull(ab);

            step("update 'abc' to 'A2'");
            assertTrue(trie.update("abc", "A2"));
            state("lookup('abc') -> " + trie.lookup("abc"));

            step("delete missing 'abe' and existing 'abd'");
            String d1 = trie.delete("abe");
            String d2 = trie.delete("abd");
            result("delete('abe') -> " + d1 + ", delete('abd') -> " + d2);
            expect("null and 'B'");
            assertNull(d1);
            assertEquals("B", d2);
            state("size=" + trie.size());
            assertEquals(1, trie.size());

            step("final lookups");
            assertNull(trie.lookup("abd"));
            assertEquals("A2", trie.lookup("abc"));
            result("lookup('abd') -> null, lookup('abc') -> 'A2'");
        }
    }
}


