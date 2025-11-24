package net.pelsmaeker.katerm.generator.diagnostics

/**
 * A simple implementation of [MessageCollector] that collects messages in a list
 * in the order in which they were received.
 */
class Messages: AbstractList<Message>(), MessageCollector {
    private val list = mutableListOf<Message>()

    override fun offer(message: Message): Boolean {
        this.list += message
        return true
    }

    override val size: Int
        get() = list.size

    override fun get(index: Int): Message {
        return list[index]
    }
}
