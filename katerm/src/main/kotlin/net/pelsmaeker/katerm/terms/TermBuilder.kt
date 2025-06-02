package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments

/**
 * Builds terms.
 */
interface TermBuilder {


    /////////
    // Int //
    /////////

    /**
     * Creates a new integer value term with the specified value and no attachments.
     *
     * @param value The value of the term.
     * @return The created term.
     */
    fun newInt(value: Int): IntTerm =
        newInt(value, TermAttachments.empty())

    /**
     * Creates a new integer value term with the specified value and attachments.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newInt(value: Int, attachments: TermAttachments): IntTerm

    /**
     * Create a copy of the specified integer value term with the specified new value and the same attachments.
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
     * Creates a new real value term with the specified value and no attachments.
     *
     * @param value The value of the term.
     * @return The created term.
     */
    fun newReal(value: Double): RealTerm =
        newReal(value, TermAttachments.empty())

    /**
     * Creates a new real value term with the specified value and attachments.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newReal(value: Double, attachments: TermAttachments): RealTerm

    /**
     * Create a copy of the specified real value term with the specified new value and the same attachments.
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
     * Creates a new string term with the specified value and no attachments.
     *
     * @param value The value of the term.
     * @return The created term.
     */
    fun newString(value: String): StringTerm =
        newString(value, TermAttachments.empty())

    /**
     * Creates a new string term with the specified value and attachments.
     *
     * @param value The value of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newString(value: String, attachments: TermAttachments): StringTerm

    /**
     * Create a copy of the specified string term with the specified new value and the same attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newValue The new value of the term.
     * @return The copy of the term, but with the new value.
     */
    fun copyString(term: StringTerm, newValue: String): StringTerm


    //////////
    // Appl //
    //////////

    /**
     * Creates a new constructor application term with the specified constructor and arguments, and no attachments.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @return The created term.
     */
    fun newAppl(op: String, vararg args: Term): ApplTerm =
        newAppl(op, args.asList(), TermAttachments.empty())

    /**
     * Creates a new constructor application term with the specified constructor and arguments, and no attachments.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @return The created term.
     */
    fun newAppl(op: String, args: List<Term>): ApplTerm =
        newAppl(op, args, TermAttachments.empty())

    /**
     * Creates a new constructor application term with the specified constructor, arguments, and attachments.
     *
     * @param op The name of the constructor.
     * @param args The arguments of the term.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm

    /**
     * Create a copy of the specified constructor application term with the specified new arguments
     * and the same constructor and attachments.
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
     * and the same constructor and attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newArgs The new arguments of the term.
     * @return The copy of the term, but with the new arguments.
     */
    fun copyAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm


    ////////////
    // Option //
    ////////////

    /**
     * Creates a new empty option term with no attachments.
     *
     * @return The created term.
     */
    fun newEmptyOption(): NoneOptionTerm =
        newEmptyOption(TermAttachments.empty())

    /**
     * Creates a new empty option term with the specified attachments.
     *
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newEmptyOption(attachments: TermAttachments): NoneOptionTerm

    /**
     * Creates a new option term with the specified element and no attachments.
     *
     * @param E The type of the element in the option.
     * @param element The element in the option; or `null` if the option is empty.
     * @return The created term.
     */
    fun <E: Term> newOption(element: E?): OptionTerm<E> =
        newOption(element, TermAttachments.empty())

    /**
     * Creates a new option term with the specified element and attachments.
     *
     * @param E The type of the element in the option.
     * @param element The element in the option; or `null` if the option is empty.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun <E: Term> newOption(element: E?, attachments: TermAttachments): OptionTerm<E>

    /**
     * Create a copy of the specified option term with the specified new element and the same attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param E The type of the element in the option.
     * @param term The term to copy.
     * @param newElement The element in the option; or `null` if the option is empty.
     * @return The copy of the term, but with the new elements.
     */
    fun <E: Term> copyOption(term: OptionTerm<E>, newElement: E?): OptionTerm<E>


    //////////
    // List //
    //////////

    /**
     * Creates a new empty list term with no attachments.
     *
     * @return The created term.
     */
    fun newEmptyList(): NilListTerm =
        newEmptyList(TermAttachments.empty())

    /**
     * Creates a new empty list term with the specified attachments.
     *
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newEmptyList(attachments: TermAttachments): NilListTerm



    /**
     * Creates a new list term with the specified elements and no attachments.
     *
     * If you want to specify the attachments, use [newList] with the head and tail.
     *
     * @param E The type of elements in the list.
     * @param elements The elements in the list.
     * @return The created term.
     */
    fun <E: Term> newListOf(vararg elements: E): ListTerm<E> =
        newListOf(elements.asList())

    /**
     * Creates a new list term with the specified elements and no attachments.
     *
     * If you want to specify the attachments, use [newList] with the head and tail.
     *
     * @param E The type of elements in the list.
     * @param elements The elements in the list.
     * @return The created term.
     */
    fun <E: Term> newListOf(elements: List<E>): ListTerm<E>

    /**
     * Creates a new list term with the specified head and tail, but no attachments.
     *
     * @param E The type of elements in the list.
     * @param head The head of the list.
     * @param tail The tail of the list.
     * @return The created term.
     */
    fun <E: Term> newList(head: E, tail: ListTerm<E>): ListTerm<E> =
        newList(head, tail, TermAttachments.empty())

    /**
     * Creates a new list term with the specified head, tail, and attachments.
     *
     * @param E The type of elements in the list.
     * @param head The head of the list.
     * @param tail The tail of the list.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun <E: Term> newList(head: E, tail: ListTerm<E>, attachments: TermAttachments): ListTerm<E>

    /**
     * Create a copy of the specified list term with the specified new elements and the same attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param E The type of elements in the list.
     * @param term The term to copy.
     * @param newHead The new head of the list.
     * @param newTail The new tail of the list.
     * @return The copy of the term, but with the new elements.
     */
    fun <E: Term> copyList(term: ListTerm<E>, newHead: E, newTail: ListTerm<E>): ListTerm<E>

    /**
     * Concatenates two list terms into a new list term.
     *
     * Concatenations cannot have term arguments.
     *
     * @param E The type of elements in the list.
     * @param leftList The first list term.
     * @param rightList The second list term.
     * @return The concatenated list term.
     */
    fun <E: Term> concatLists(leftList: ListTerm<E>, rightList: ListTerm<E>): ListTerm<E>


    /////////
    // Var //
    /////////

    /**
     * Creates a new term variable with the specified name and no attachments.
     *
     * Any resource names should be encoded as part of the variable name.
     *
     * @param name The name of the variable.
     * @return The created term.
     */
    fun newVar(name: String): TermVar = newVar(name, TermAttachments.empty())

    /**
     * Creates a new term variable with the specified name and attachments.
     *
     * Any resource names should be encoded as part of the variable name.
     *
     * @param name The name of the variable.
     * @param attachments The attachments of the term.
     * @return The created term.
     */
    fun newVar(name: String, attachments: TermAttachments): TermVar

    /**
     * Create a copy of the specified term variable with the specified new name and the same attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param term The term to copy.
     * @param newName The new name of the variable.
     * @return The copy of the term, but with the new name.
     */
    fun copyVar(term: TermVar, newName: String): TermVar

    /**
     * Creates a copy of the specified term with the specified new attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param T The type of term to copy.
     * @param term The term to copy.
     * @param newAttachments The new attachments of the term.
     * @return The copy of the term, but with the new attachments.
     */
    fun <T: Term> withAttachments(term: T, newAttachments: TermAttachments): T

}

