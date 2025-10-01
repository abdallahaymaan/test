package com.pidima.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A generic Prefix Tree (Trie) that maps string keys to values.
 * Supports create (put if absent), insert/upsert, lookup, update, and delete operations.
 */
public class PrefixTree<V> {

    private static class Node<V> {
        Map<Character, Node<V>> children = new HashMap<>();
        boolean isTerminal;
        V value;
    }

    private final Node<V> root = new Node<>();
    private int size = 0;

    /**
     * Number of keys stored in the trie.
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the trie contains the exact key.
     */
    public boolean containsKey(String key) {
        return getNode(key) != null && getNode(key).isTerminal;
    }

    /**
     * Lookup the value for an exact key; returns null if absent.
     */
    public V lookup(String key) {
        Node<V> node = getNode(key);
        return node != null && node.isTerminal ? node.value : null;
    }

    /**
     * Insert a value for the key. If key exists, its value is replaced (upsert).
     * Returns the previous value associated with key, or null if none.
     */
    public V insert(String key, V value) {
        Objects.requireNonNull(key, "key");
        Node<V> node = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            node = node.children.computeIfAbsent(c, k -> new Node<>());
        }
        V previous = node.isTerminal ? node.value : null;
        if (!node.isTerminal) {
            size++;
        }
        node.isTerminal = true;
        node.value = value;
        return previous;
    }

    /**
     * Create a value only if the key is absent. Returns true if created, false if the key existed.
     */
    public boolean create(String key, V value) {
        Objects.requireNonNull(key, "key");
        Node<V> node = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            node = node.children.computeIfAbsent(c, k -> new Node<>());
        }
        if (node.isTerminal) {
            return false;
        }
        node.isTerminal = true;
        node.value = value;
        size++;
        return true;
    }

    /**
     * Update the value for an existing key. Returns true if updated, false if key not found.
     */
    public boolean update(String key, V newValue) {
        Node<V> node = getNode(key);
        if (node == null || !node.isTerminal) {
            return false;
        }
        node.value = newValue;
        return true;
    }

    /**
     * Delete the exact key. Returns the removed value, or null if key not found.
     */
    public V delete(String key) {
        Objects.requireNonNull(key, "key");
        return delete(root, key, 0);
    }

    private V delete(Node<V> node, String key, int index) {
        if (index == key.length()) {
            if (!node.isTerminal) {
                return null;
            }
            V old = node.value;
            node.isTerminal = false;
            node.value = null;
            size--;
            return old;
        }
        char c = key.charAt(index);
        Node<V> child = node.children.get(c);
        if (child == null) {
            return null;
        }
        V result = delete(child, key, index + 1);
        // Prune child if it became empty and non-terminal
        if (result != null && !child.isTerminal && child.children.isEmpty()) {
            node.children.remove(c);
        }
        return result;
    }

    private Node<V> getNode(String key) {
        Objects.requireNonNull(key, "key");
        Node<V> node = root;
        for (int i = 0; i < key.length(); i++) {
            node = node.children.get(key.charAt(i));
            if (node == null) {
                return null;
            }
        }
        return node;
    }
}


