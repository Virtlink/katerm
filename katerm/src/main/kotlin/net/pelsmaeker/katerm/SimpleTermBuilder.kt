package net.pelsmaeker.katerm

/**
 * Builds simple terms.
 *
 * Some types of terms don't accept separators because they cannot be pretty-printed.
 */
interface SimpleTermBuilder: TermBuilder {

    /////////
    // Int //
    /////////

    /**
     * Creates a new integer value term with the specified value and no attachments, and the default separators.
     *
     * @param value The value of the term.
     * @return The created term.
     */
    fun newInt(value: Int): IntTerm =
        newInt(value, TermAttachments.empty(), null)

    /**
     * Creates a new integer value term with the specified value and attachments, and the default separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newInt(value: Int, attachments: TermAttachments): IntTerm =
        newInt(value, attachments, null)

    /**
     * Creates a new integer value term with the specified value and separators, and no attachments.
     *
     * @param value The value of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newInt(value: Int, separators: List<String>?): IntTerm =
        newInt(value, TermAttachments.empty(), separators)

    /**
     * Creates a new integer value term with the specified value, attachments, and separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newInt(value: Int, attachments: TermAttachments, separators: List<String>?): IntTerm

    /**
     * Create a copy of the specified integer value term with the specified new value
     * and the same attachments and separators.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newValue The new value of the term.
     * @return The copy of the term, but with the new value.
     */
    fun copyInt(term: IntTerm, newValue: Int): IntTerm

    /////////
    // Real //
    /////////

    /**
     * Creates a new real value term with the specified value and no attachments, and the default separators.
     *
     * @param value The value of the term.
     * @return The created term.
     */
    fun newReal(value: Double): RealTerm =
        newReal(value, TermAttachments.empty(), null)

    /**
     * Creates a new real value term with the specified value and attachments, and the default separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newReal(value: Double, attachments: TermAttachments): RealTerm =
        newReal(value, attachments, null)

    /**
     * Creates a new real value term with the specified value and separators, and no attachments.
     *
     * @param value The value of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newReal(value: Double, separators: List<String>?): RealTerm =
        newReal(value, TermAttachments.empty(), separators)

    /**
     * Creates a new real value term with the specified value, attachments, and separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term,
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newReal(value: Double, attachments: TermAttachments, separators: List<String>?): RealTerm

    /**
     * Create a copy of the specified real value term with the specified new value
     * and the same attachments and separators.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newValue The new value of the term.
     * @return The copy of the term, but with the new value.
     */
    fun copyReal(term: RealTerm, newValue: Double): RealTerm

    ////////////
    // String //
    ////////////

    /**
     * Creates a new string term with the specified value and no attachments, and the default separators.
     *
     * @param value The value of the term.
     * @return The created term.
     */
    fun newString(value: String): StringTerm =
        newString(value, TermAttachments.empty(), null)

    /**
     * Creates a new string term with the specified value and attachments, and the default separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newString(value: String, attachments: TermAttachments): StringTerm =
        newString(value, attachments, null)

    /**
     * Creates a new string term with the specified value and separators, and no attachments.
     *
     * @param value The value of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newString(value: String, separators: List<String>?): StringTerm =
        newString(value, TermAttachments.empty(), separators)

    /**
     * Creates a new string term with the specified value, attachments, and separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newString(value: String, attachments: TermAttachments, separators: List<String>?): StringTerm

    /**
     * Create a copy of the specified string term with the specified new value
     * and the same attachments and separators.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newValue The new value of the term.
     * @return The copy of the term, but with the new value.
     */
    fun copyString(term: StringTerm, newValue: String): StringTerm

    ///////////
    // Value //
    ///////////

    /**
     * Creates a new value term with the specified value and no attachments, and the default separators.
     *
     * @param value The value of the term.
     * @return The created term.
     */
    fun <T> newValue(value: T): ValueTerm<T> =
        newValue(value, TermAttachments.empty(), null)

    /**
     * Creates a new value term with the specified value and attachments, and the default separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun <T> newValue(value: T, attachments: TermAttachments): ValueTerm<T> =
        newValue(value, attachments, null)

    /**
     * Creates a new value term with the specified value and separators, and no attachments.
     *
     * @param value The value of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun <T> newValue(value: T, separators: List<String>?): ValueTerm<T> =
        newValue(value, TermAttachments.empty(), separators)

    /**
     * Creates a new value term with the specified value, attachments, and separators.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun <T> newValue(value: T, attachments: TermAttachments, separators: List<String>?): ValueTerm<T>

    /**
     * Create a copy of the specified value term with the specified new value
     * and the same attachments and separators.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param V The type of the value.
     * @param term The term to copy.
     * @param newValue The new value of the term.
     * @return The copy of the term, but with the new value.
     */
    fun <V> copyValue(term: ValueTerm<V>, newValue: V): ValueTerm<V>
    
    //////////
    // Appl //
    //////////

    /**
     * Creates a new constructor application term with the specified constructor and arguments,
     * and no attachments and the default separators.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @return The created term.
     */
    fun newAppl(op: String, vararg args: Term): ApplTerm =
        newAppl(op, args.asList(), TermAttachments.empty())

    /**
     * Creates a new constructor application term with the specified constructor and arguments,
     * and no attachments and the default separators.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @return The created term.
     */
    fun newAppl(op: String, args: List<Term>): ApplTerm =
        newAppl(op, args, TermAttachments.empty())

    /**
     * Creates a new constructor application term with the specified constructor, arguments, and separators,
     * and no attachments.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newAppl(op: String, args: List<Term>, separators: List<String>?): ApplTerm =
        newAppl(op, args, TermAttachments.empty(), separators)

    /**
     * Creates a new constructor application term with the specified constructor, arguments, attachments,
     * and the default separators.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm =
        newAppl(op, args, attachments, null)

    /**
     * Creates a new constructor application term with the specified constructor, arguments, attachments, and separators.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @param attachments The attachments of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun newAppl(op: String, args: List<Term>, attachments: TermAttachments, separators: List<String>?): ApplTerm

    /**
     * Create a copy of the specified constructor application term with the specified new arguments
     * and the same constructor, attachments and separators.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newArgs The new arguments of the term.
     * @return The copy of the term, but with the new arguments.
     */
    fun copyAppl(term: ApplTerm, vararg newArgs: Term): ApplTerm = copyAppl(term, newArgs.asList())

    /**
     * Create a copy of the specified constructor application term with the specified new arguments
     * and the same constructor, attachments and separators.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newArgs The new arguments of the term.
     * @return The copy of the term, but with the new arguments.
     */
    fun copyAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm
    
    //////////
    // List //
    //////////

    /**
     * Creates a new list term with the specified elements, and no attachments and the default separators.
     *
     * @param E The type of the elements in the list.
     * @param elements The elements in the list.
     * @return The created term.
     */
    fun <E: Term> newList(vararg elements: E): ListTerm<E> =
        newList(elements.asList(), null)

    /**
     * Creates a new list term with the specified elements, and no attachments and the default separators.
     *
     * @param E The type of the elements in the list.
     * @param elements The elements in the list.
     * @return The created term.
     */
    fun <E: Term> newList(elements: List<E>): ListTerm<E> =
        newList(elements, TermAttachments.empty(), null)

    /**
     * Creates a new list term with the specified elements and separators, and no attachments.
     *
     * @param E The type of the elements in the list.
     * @param elements The elements in the list.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun <E: Term> newList(elements: List<E>, separators: List<String>?): ListTerm<E> =
        newList(elements, TermAttachments.empty(), separators)

    /**
     * Creates a new list term with the specified elements and attachments, and the default separators.
     *
     * @param E The type of the elements in the list.
     * @param elements The elements in the list.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun <E: Term> newList(elements: List<E>, attachments: TermAttachments): ListTerm<E> =
        newList(elements, attachments, null)

    /**
     * Creates a new list term with the specified elements, attachments, and separators.
     *
     * @param E The type of the elements in the list.
     * @param elements The elements in the list.
     * @param attachments The attachments of the term.
     * @param separators The separators; or `null` to use the default separators.
     * @return The created term.
     */
    fun <E: Term> newList(elements: List<E>, attachments: TermAttachments, separators: List<String>?): ListTerm<E>

    /**
     * Create a copy of the specified list term with the specified new elements
     * and the same attachments and separators.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param E The type of the elements in the list.
     * @param term The term to copy.
     * @param newElements The new elements of the term.
     * @return The copy of the term, but with the new elements.
     */
    fun <E: Term> copyList(term: ListTerm<E>, vararg newElements: E): ListTerm<E> = copyList(term, newElements.asList())

    /**
     * Create a copy of the specified list term with the specified new elements
     * and the same attachments and separators.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param E The type of the elements in the list.
     * @param term The term to copy.
     * @param newElements The new elements of the term.
     * @return The copy of the term, but with the new elements.
     */
    fun <E: Term> copyList(term: ListTerm<E>, newElements: List<E>): ListTerm<E>

    /////////
    // Var //
    /////////

    /**
     * Creates a new term variable with the specified name, and no attachments or separators.
     *
     * Term variables cannot have separators.
     *
     * Any resource names should be encoded as part of the variable name.
     *
     * @param name The name of the variable.
     * @return The created term.
     */
    fun newVar(name: String): TermVar = newVar(name, TermAttachments.empty())

    /**
     * Creates a new term variable with the specified name and attachments, and no separators.
     *
     * Term variables cannot have separators.
     *
     * Any resource names should be encoded as part of the variable name.
     *
     * @param name The name of the variable.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newVar(name: String, attachments: TermAttachments): TermVar

    /**
     * Create a copy of the specified term variable with the specified new name
     * and the same attachments and no separators.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newName The new name of the variable.
     * @return The copy of the term, but with the new name.
     */
    fun copyVar(term: TermVar, newName: String): TermVar

    /////////////
    // ListVar //
    /////////////

    fun newListVar(name: String): ListTermVar = newListVar(name, TermAttachments.empty())
    fun newListVar(name: String, attachments: TermAttachments): ListTermVar

}

