package net.pelsmaeker.katerm.terms

import net.pelsmaeker.katerm.attachments.TermAttachments

/**
 * Base class for term builders.
 */
abstract class TermBuilderBase: TermBuilder {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Term> withAttachments(term: T, newAttachments: TermAttachments): T {
        if (term.termAttachments == newAttachments) return term
        return when (term) {
            is ApplTerm -> newAppl(term.termOp, term.termArgs, newAttachments) as T
            is IntTerm -> newInt(term.value, newAttachments) as T
            is RealTerm -> newReal(term.value, newAttachments) as T
            is StringTerm -> newString(term.value, newAttachments) as T
            is SomeOptionTerm<*> -> newOption(term.element, newAttachments) as T
            is NoneOptionTerm -> newEmptyOption(newAttachments) as T
            is ConsListTerm<*> -> newList(term.head, term.tail, newAttachments) as T
            is NilListTerm -> newEmptyList(newAttachments) as T
            is ConcatListTerm<*> -> {
                require(newAttachments.isEmpty()) { "Concatenation terms cannot have attachments." }
                term
            }
            is TermVar -> newVar(term.name, newAttachments) as T
            else -> throw IllegalArgumentException("Unknown term type: $term")
        }
    }


    /////////
    // Int //
    /////////

    final override fun newInt(value: Int, attachments: TermAttachments): IntTerm {
        return IntTerm(value, attachments)
    }

    final override fun copyInt(term: IntTerm, newValue: Int): IntTerm {
        if (term.value == newValue) return term
        return newInt(newValue, term.termAttachments)
    }


    /////////
    // Real //
    /////////

    final override fun newReal(value: Double, attachments: TermAttachments): RealTerm {
        return RealTerm(value, attachments)
    }

    final override fun copyReal(term: RealTerm, newValue: Double): RealTerm {
        if (term.value == newValue) return term
        return newReal(newValue, term.termAttachments)
    }


    ////////////
    // String //
    ////////////

    final override fun newString(value: String, attachments: TermAttachments): StringTerm {
        return StringTerm(value, attachments)
    }

    final override fun copyString(term: StringTerm, newValue: String): StringTerm {
        if (term.value == newValue) return term
        return newString(newValue, term.termAttachments)
    }


    //////////
    // Appl //
    //////////

    abstract override fun newAppl(op: String, args: List<Term>, attachments: TermAttachments): ApplTerm

    override fun copyAppl(term: ApplTerm, newArgs: List<Term>): ApplTerm {
        // Can be overridden to provide a more efficient implementation.
        if (term.termArgs == newArgs) return term
        return newAppl(term.termOp, newArgs, term.termAttachments)
    }


    ////////////
    // Option //
    ////////////

    final override fun newEmptyOption(attachments: TermAttachments): NoneOptionTerm {
        return NoneOptionTerm(attachments)
    }

    final override fun <E: Term> newOption(element: E?, attachments: TermAttachments): OptionTerm<E> {
        return when (element) {
            null -> newEmptyOption(attachments)
            else -> SomeOptionTerm(element, attachments)
        }
    }

    final override fun <E: Term> copyOption(term: OptionTerm<E>, newElement: E?): OptionTerm<E> {
        if (term is SomeOptionTerm<E> && term.element == newElement) return term
        return newOption(newElement, term.termAttachments)
    }


    //////////
    // List //
    //////////

    final override fun newEmptyList(attachments: TermAttachments): NilListTerm {
        return NilListTerm(attachments)
    }

    final override fun <T : Term> newListOf(elements: List<T>): ListTerm<T> {
        return if (elements.isNotEmpty()) {
            newList(elements.first(), newListOf(elements.drop(1)))
        } else {
            newEmptyList()
        }
    }

    final override fun <E : Term> newList(head: E, tail: ListTerm<E>, attachments: TermAttachments): ListTerm<E> {
        return ConsListTerm(head, tail, attachments)
    }

    final override fun <E : Term> copyList(term: ListTerm<E>, newHead: E, newTail: ListTerm<E>): ListTerm<E> {
        if (term is ConsListTerm<*> && term.head == newHead && term.tail == newTail) return term
        return newList(newHead, newTail, term.termAttachments)
    }

    final override fun <E: Term> concatLists(leftList: ListTerm<E>, rightList: ListTerm<E>): ListTerm<E> {
        return if (rightList is NilListTerm) {
            // as ++ [] = as
            leftList
        } else {
            when (leftList) {
                // (a :: as) ++ bs = a :: (as ++ bs)
                is ConsListTerm<E> -> newList(leftList.head, concatLists(leftList.tail, rightList), leftList.termAttachments)

                // [] ++ bs = bs
                is NilListTerm -> rightList

                // ?xs ++ bs = ?xs ++ bs
                is TermVar -> ConcatListTerm(leftList, rightList)

                // (xs ++ as) ++ bs == xs ++ (as ++ bs)
                is ConcatListTerm<E> -> ConcatListTerm<E>(leftList.left, concatLists(leftList.right, rightList))
            }
        }
    }


    /////////
    // Var //
    /////////

    final override fun newVar(name: String, attachments: TermAttachments): TermVar {
        return TermVar(name, attachments)
    }

    final override fun copyVar(term: TermVar, newName: String): TermVar {
        return newVar(newName, term.termAttachments)
    }

}