package net.pelsmaeker.katerm.generator.text

import net.pelsmaeker.katerm.generator.ResourceID

/**
 * The origin of a fragment of text in a resource.
 *
 * @property resourceId The resource the text originated from.
 * @property range The range in the resource the text originated from; or `null` if not known.
 */
data class TextOrigin(
    val resourceId: ResourceID,
    val range: TextRange? = null,
) : Comparable<TextOrigin> {

    constructor(resourceId: ResourceID, start: TextPosition, end: TextPosition) : this(resourceId,
        TextRange(start, end)
    )

    override fun compareTo(other: TextOrigin): Int {
        val resourceComparison = this.resourceId.compareTo(other.resourceId)
        if (resourceComparison != 0) return resourceComparison
        if (this.range == null && other.range == null) return 0
        if (this.range == null) return -1
        if (other.range == null) return 1
        return when {
            this.range.start != other.range.start -> this.range.start.compareTo(other.range.start)
            this.range.end != other.range.end -> this.range.end.compareTo(other.range.end)
            else -> 0
        }
    }

    override fun toString(): String = "$resourceId:$range"
}
