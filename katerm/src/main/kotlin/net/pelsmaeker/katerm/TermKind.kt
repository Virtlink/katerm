package net.pelsmaeker.katerm

/** Specifies the kind of [net.pelsmaeker.katerm.terms.Term]. */
enum class TermKind {

    /** A variable, list variable, or option variable. */
    VAR,

    /** An integer value. */
    VALUE_INT,

    /** A real number value. */
    VALUE_REAL,

    /** A string value. */
    VALUE_STRING,

    /** A constructor application. */
    APPL,

    /** A option with some value. */
    OPTION_SOME,

    /** An option with no value. */
    OPTION_NONE,

    /** An empty list. */
    LIST_NIL,

    /** A non-empty list. */
    LIST_CONS,

    /** A concatenation of two lists. */
    LIST_CONCAT,
}