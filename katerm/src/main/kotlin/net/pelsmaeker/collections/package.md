# Package net.pelsmaeker.collections
This package contains interfaces and implementations for a few less common collections.  We can distinguish different
collections interfaces by:

- Read/write: read only or read/write
- Mutability: (immutable or persistent) or (mutable or transient)
- Ordering: ordered or unordered
- Duplicates: allow duplicates or not
- Nulls: allow nulls or not
- Key/value: key/value or not

And different implementations by:

- Thread safety: thread safe or not
- Bulk operations: support (efficient) bulk operations or not
- Random-access: support (efficient) random access or not
- Performance
- Memory usage

The interfaces are distinguished by that you can _do_, whereas the different implementations are distinguished by
how (performant) they behave.  For example, whether in-place multability operations are available depends on the
interface, and similarly, whether you can add `null` keys/values to the collection should depend on the interface
(type).  Most attributes that refer to performance (speed, memory usage, Big-O notation) depend on the implementation.
Arguably, all other attributes should be part of the interface, including things such as null-safety and thread-safety.
For example, a function might want to request/assert that a particular implementation passed to it is thread-safe.

We could model these properties as separate (marker) interfaces, something like: `Ordered`, `ThreadSafe`.
Alternatively, these properties could be actual properties on the instance (e.g., `isOrdered`, `isThreadSafe`) and
that would also allow the program to assert on it, or use a method such as `asThreadSafe()` to ensure it is thread-safe
or in a thread-safe wrapper. 

We also need to take into account that if someone is given a mutable collection, you won't know if it's a set
or a list, and therefore whether it retains or discards duplicates that are added.

This already shows the complexities. We can have a:

| T                  | Unordered   | Ordered      |
|--------------------|-------------|--------------|
| Duplicates         | MultiSet    | List         |
| No duplicates      | Set         | SortedSet(?) |

For a map:

| K -> V             | Unordered   | Ordered      |
|--------------------|-------------|--------------|
| Duplicate pairs    | MultiMap(?) | ListMap(?)   |
| No duplicate pairs | MultiMap(?) | SortedMap(?) |
| No duplicate keys  | Map         | SortedMap(?) |

