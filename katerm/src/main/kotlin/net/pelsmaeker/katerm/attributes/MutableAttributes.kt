package net.pelsmaeker.katerm.attributes

/**
 * Mutable variant of [Attributes].
 */
interface MutableAttributes : Attributes {

    /**
     * Sets the value of the specified attribute for the specified object.
     *
     * @param obj The object to set the attribute for.
     * @param attribute The attribute to set.
     * @param value The value to set the attribute to, or `null` to clear the attribute.
     * @return The previous value of the attribute, or `null` if the attribute was not set or set to `null`.
     */
    fun <T> setAttribute(obj: Any, attribute: Attribute<T>, value: T?): T?

    /**
     * Clears the specified attribute for the specified object.
     *
     * @param obj The object to clear the attribute for.
     * @param attribute The attribute to clear.
     * @return The previous value of the attribute, or `null` if the attribute was not set or set to `null`.
     */
    fun <T> clearAttribute(obj: Any, attribute: Attribute<T>): T? = setAttribute(obj, attribute, null)
}
