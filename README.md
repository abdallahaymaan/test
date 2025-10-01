## Prefix Tree (Trie) Demo - Java + Maven + JUnit

This project contains a simple, generic Prefix Tree (Trie) implementation in Java, along with JUnit tests that demonstrate and verify its behavior. The tests print structured, step-by-step logs so you can follow exactly what happens during each operation.

### Where things live
- Implementation: `src/main/java/com/pidima/test/PrefixTree.java`
- Tests: `src/test/java/com/pidima/test/PrefixTreeTests.java`

## What is a Prefix Tree (Trie)?
A Prefix Tree (Trie) is a tree data structure optimized for efficiently storing and querying strings by their prefixes. Each node represents a character; a path from the root to a node corresponds to a prefix. Tries are commonly used for prefix lookups, autocomplete, spell-checkers, and routing based on string keys.

### In this implementation
This trie maps String keys to generic values `V` and supports exact-key operations. Internally, it stores:
- `children: Map<Character, Node<V>>` for outgoing edges
- `isTerminal: boolean` to mark that a node corresponds to a complete key
- `value: V` the value associated with a terminal node

## Supported Operations

- `size()`
  - Returns the number of exact keys currently stored.

- `containsKey(String key)`
  - Returns `true` if the exact key exists, else `false`.

- `lookup(String key)`
  - Returns the value for the exact key, or `null` if not present.

- `insert(String key, V value)`
  - Upsert (insert or replace) the value for the exact key.
  - Returns the previous value if the key existed, otherwise `null`.
  - If the key was new, `size` increases by 1.

- `create(String key, V value)`
  - Create only-if-absent semantics.
  - Returns `true` if a new key was created, `false` if it already existed (no change to value if it existed).

- `update(String key, V newValue)`
  - Update only-if-present semantics.
  - Returns `true` if the key existed and was updated, otherwise `false`.

- `delete(String key)`
  - Removes the exact key if present and returns its value, otherwise returns `null`.
  - Prunes empty branches where possible.

### Example usage
```java
PrefixTree<Integer> trie = new PrefixTree<>();
trie.insert("app", 1);            // upsert, size -> 1
trie.create("car", 2);            // create-if-absent, size -> 2
Integer v = trie.lookup("app");   // returns 1
trie.update("app", 3);            // returns true, value now 3
Integer removed = trie.delete("car"); // returns 2, size -> 1
```

## Tests and Logging

The tests in `PrefixTreeTests` are written using JUnit 5 (provided by Spring Boot's `spring-boot-starter-test`). Each test prints structured logs to the console with the following tags:

- `[TEST]` the test title
- `[STEP]` an action taken during the test
- `[STATE]` state snapshots (e.g., size, lookups)
- `[EXPECT]` what the test expects to happen next
- `[RESULT]` the immediate outcome of a step

This makes it easy to follow the sequence of operations and see why assertions should pass.

### Example of console output (abridged)
```text
============================================================
[TEST] insert and lookup should store and retrieve values
============================================================
[STATE] initial size=0
[STEP] lookup key 'app' before insert
[RESULT] lookup('app') -> null
[EXPECT] null because key not present
[STEP] insert key 'app' with value 1
[RESULT] insert returned previous value -> null
[EXPECT] null because key was absent
[STATE] size=1
... (more steps) ...
```

## How to build and run tests

From the project root (where the `pom.xml` lives):

```bash
mvn clean test
```

Notes:
- The test output includes the structured logs described above.
- If you prefer less Maven noise and only want the test logs, you can add `-q` (quiet) as needed:

```bash
mvn -q -DskipTests=false clean test
```

## Requirements
- Java 17+
- Maven (as used by the included `mvnw`/`mvnw.cmd`, or a system-wide Maven installation)

## Extending the Trie
Potential extensions you could add:
- Prefix search that returns all keys/values under a prefix
- Iteration over stored keys/values
- Case-insensitive or locale-aware options
- Serialization/deserialization support

## License
This demo project is for educational purposes.


