package net.pelsmaeker.katerm.attributes

/**
 * Associates attributes with objects.
 */
interface Attributes {

    /**
     * Gets the value of the specified attribute for the specified object.
     *
     * @param obj The object to get the attribute for.
     * @param attribute The attribute to get.
     * @return The value of the attribute, or null if the attribute is not set or set to `null`.
     */
    fun <T> getAttribute(obj: Any, attribute: Attribute<T>): T?

    /**
     * Gets the value of the specified attribute for the specified object,
     * throwing an exception if the attribute is not set.
     *
     * @param obj The object to get the attribute for.
     * @param attribute The attribute to get.
     * @return The value of the attribute, which may be `null` if the attribute is set to `null`.
     * @throws IllegalStateException if the attribute is not set.
     */
    fun <T> getAttributeOrThrow(obj: Any, attribute: Attribute<T>): T {
        check(hasAttribute(obj, attribute))
        // This cast is safe.
        // If T is non-nullable, we checked for null above.
        // If T is nullable, it should not check for null.
        @Suppress("UNCHECKED_CAST")
        return getAttribute(obj, attribute) as T
    }

    /**
     * Checks if the specified object has the specified attribute set.
     *
     * @param obj The object to check.
     * @param attribute The attribute to check.
     * @return `true` if the attribute is set (even if set to `null`); otherwise, `false`.
     */
    fun <T> hasAttribute(obj: Any, attribute: Attribute<T>): Boolean

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
