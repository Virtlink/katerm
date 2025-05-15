package net.pelsmaeker.katerm

/** Specifies the kind of [Term]. */
enum class TermKind {
    /** A variable. */
    VAR,

    /** An integer. */
    INT,

    /** A real number. */
    REAL,

    /** A string. */
    STRING,

    /** A constructor application. */
    APPL,

    /** An option. */
    OPTION,

    /** A list. */
    LIST,
}