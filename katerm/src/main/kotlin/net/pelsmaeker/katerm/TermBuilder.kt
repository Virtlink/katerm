package net.pelsmaeker.katerm

/**
 * Builds terms.
 */
interface TermBuilder {

    /**
     * Creates a copy of the specified term with the specified new attachments.
     *
     * @param term the term to copy
     * @param newAttachments the new attachments of the term
     * @return the copy of the term, but with the new attachments
     */
    fun withAttachments(term: Term, newAttachments: TermAttachments): Term

    /////////
    // Int //
    /////////

    /**
     * Creates a new integer value term with the specified value and no attachments.
     *
     * @param value the value of the term
     * @return the created term
     */
    fun createInt(value: Int): IntTerm = createInt(value, TermAttachments.empty())

    /**
     * Creates a new integer value term with the specified value and attachments.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @return the created term
     */
    fun createInt(value: Int, attachments: TermAttachments): IntTerm

    /**
     * Create a copy of the specified integer value term with the specified new value
     * and the same attachments.
     *
     * @param newValue the new value of the term
     * @return the copy of the term, but with the new value
     */
    fun replaceInt(term: IntTerm, newValue: Int): IntTerm

    /////////
    // Real //
    /////////

    /**
     * Creates a new real value term with the specified value and no attachments.
     *
     * @param value the value of the term
     * @return the created term
     */
    fun createReal(value: Double): RealTerm = createReal(value, TermAttachments.empty())

    /**
     * Creates a new real value term with the specified value and attachments.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @return the created term
     */
    fun createReal(value: Double, attachments: TermAttachments): RealTerm

    /**
     * Create a copy of the specified real value term with the specified new value
     * and the same attachments.
     *
     * @param newValue the new value of the term
     * @return the copy of the term, but with the new value
     */
    fun replaceReal(term: RealTerm, newValue: Double): RealTerm

    ////////////
    // String //
    ////////////

    /**
     * Creates a new string term with the specified value and no attachments.
     *
     * @param value the value of the term
     * @return the created term
     */
    fun createString(value: String): StringTerm = createString(value, TermAttachments.empty())

    /**
     * Creates a new string term with the specified value and attachments.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @return the created term
     */
    fun createString(value: String, attachments: TermAttachments): StringTerm

    /**
     * Create a copy of the specified string term with the specified new value
     * and the same attachments.
     *
     * @param newValue the new value of the term
     * @return the copy of the term, but with the new value
     */
    fun replaceString(term: StringTerm, newValue: String): StringTerm



    /**
     * Creates a new blob term with the specified value and no attachments.
     *
     * @param value the value of the term
     * @return the created term
     */
    fun createBlob(value: Any): BlobTerm = createBlob(value, TermAttachments.empty())

    /**
     * Creates a new blob term with the specified value and attachments.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @return the created term
     */
    fun <T> createValue(value: T, attachments: TermAttachments): ValueTerm<T> =
        createValue(value, attachments, null)

    /**
     * Creates a new value term with the specified value and separators, and no attachments.
     *
     * @param value the value of the term
     * @param separators the separators; or `null` to use the default separators
     * @return the created term
     */
    fun <T> createValue(value: T, separators: List<String>?): ValueTerm<T> =
        createValue(value, TermAttachments.empty(), separators)

    /**
     * Creates a new value term with the specified value, attachments, and separators.
     *
     * @param value the value of the term
     * @param attachments the attachments of the term
     * @param separators the separators; or `null` to use the default separators
     * @return the created term
     */
    fun <T> createValue(value: T, attachments: TermAttachments, separators: List<String>?): ValueTerm<T>

    /**
     * Create a copy of the specified value term with the specified new value
     * and the same attachments and separators.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param term the term to copy
     * @param newValue the new value of the term
     * @return the copy of the term, but with the new value
     */
    fun <T> replaceValue(term: ValueTerm<T>, newValue: T): ValueTerm<T>

//    //////////
//    // Blob //
//    //////////
//
//    /**
//     * Creates a new blob term with the specified value and no attachments or separators.
//     *
//     * Blobs cannot have separators.
//     *
//     * @param value the value of the term
//     * @return the created term
//     */
//    fun createBlob(value: Any): BlobTerm =
//        createBlob(value, TermAttachments.empty())
//
//    /**
//     * Creates a new blob term with the specified value and attachments, and no separators.
//     *
//     * Blobs cannot have separators.
//     *
//     * @param value the value of the term
//     * @param attachments the attachments of the term
//     * @return the created term
//     */
//    fun createBlob(value: Any, attachments: TermAttachments): BlobTerm
//
//    /**
//     * Create a copy of the specified blob term with the specified new value
//     * and the same attachments, and no separators.
//     *
//     * Blobs cannot have separators.
//     *
//     * Calling this method can be efficient than deconstructing and rebuilding a term.
//     *
//     * @param term the term to copy
//     * @param newValue the new value of the term
//     * @return the copy of the term, but with the new value
//     */
//    fun replaceBlob(term: BlobTerm, newValue: Any): BlobTerm

    //////////
    // Appl //
    //////////

    fun createAppl(op: String, vararg args: Term): ApplTerm = createAppl(op, args.asList())
    fun createAppl(type: ApplTermType, vararg args: Term): ApplTerm = createAppl(type, args.asList())

    fun createAppl(op: String, args: List<Term>): ApplTerm = createAppl(ApplTermType(op, args.map { it.termType }), args, TermAttachments.empty())
    fun createAppl(type: ApplTermType, args: List<Term>): ApplTerm = createAppl(type, args, TermAttachments.empty())

    fun createAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm = createAppl(ApplTermType(op, args.map { it.termType }), args, attachments)
    fun createAppl(type: ApplTermType, args: List<Term>, attachments: TermAttachments): ApplTerm

    fun replaceAppl(term: ApplTerm, vararg newArgs: Term): ApplTerm = replaceAppl(term, newArgs.asList())
    fun replaceAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm

    //////////
    // List //
    //////////

    fun createList(vararg elements: Term): ListTerm = createList(elements.asList())
    fun createList(type: ListTermType, vararg elements: Term): ListTerm = createList(elements.asList())

    fun createList(elements: List<Term>): ListTerm = createList(ListTermType(TermType.getSupertypeOf(elements.map { it.termType} )), elements, TermAttachments.empty())
    fun createList(type: ListTermType, elements: List<Term>): ListTerm = createList(type, elements, TermAttachments.empty())

    fun createList(elements: List<Term>, attachments: TermAttachments): ListTerm = createList(ListTermType(TermType.getSupertypeOf(elements.map { it.termType} )), elements, attachments)
    fun createList(type: ListTermType, elements: List<Term>, attachments: TermAttachments): ListTerm

    fun replaceList(term: ListTerm, vararg newElements: Term): ListTerm = replaceList(term, newElements.asList())
    fun replaceList(term: ListTerm, newElements: List<Term>): ListTerm

    /////////
    // Var //
    /////////

    fun createVar(type: TermType, name: String, resource: String? = null): TermVar = createVar(type, name, resource, TermAttachments.empty())
    fun createVar(type: TermType, name: String, resource: String? = null, attachments: TermAttachments): TermVar

    /////////////
    // ListVar //
    /////////////

    fun createListVar(type: ListTermType, name: String, resource: String? = null): ListTermVar = createListVar(type, name, resource, TermAttachments.empty())
    fun createListVar(type: ListTermType, name: String, resource: String? = null, attachments: TermAttachments): ListTermVar

}

