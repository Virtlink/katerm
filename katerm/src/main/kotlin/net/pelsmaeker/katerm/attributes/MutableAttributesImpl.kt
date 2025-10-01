package net.pelsmaeker.katerm.attributes

import java.util.WeakHashMap

/**
 * Implementation of [MutableAttributes] using a weak hash map.
 */
class MutableAttributesImpl : MutableAttributes {

    // We use a WeakHashMap which doesn't keep a pointer to the key object,
    // so that the attributes don't prevent the object from being garbage collected.
    private val map = WeakHashMap<Any, MutableMap<Attribute<*>, Any?>>()

    override fun <T> setAttribute(
        obj: Any,
        attribute: Attribute<T>,
        value: T?,
    ): T? {
        val attrMap = map.getOrPut(obj) { mutableMapOf() }
        @Suppress("UNCHECKED_CAST")
        return attrMap.put(attribute, value) as T?
    }

    override fun <T> getAttribute(obj: Any, attribute: Attribute<T>): T? {
        val attrMap = map[obj] ?: return null
        @Suppress("UNCHECKED_CAST")
        return attrMap[attribute] as T?
    }

    override fun <T> hasAttribute(
        obj: Any,
        attribute: Attribute<T>,
    ): Boolean {
        val attrMap = map[obj] ?: return false
        return attrMap.containsKey(attribute)
    }

}