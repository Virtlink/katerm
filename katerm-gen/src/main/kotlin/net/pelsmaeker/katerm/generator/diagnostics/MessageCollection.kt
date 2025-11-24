package net.pelsmaeker.katerm.generator.diagnostics

typealias MessageCollection = Collection<Message>

/**
 * Gets all messages in the collection with a severity of [MessageSeverity.FATAL] or higher.
 *
 * @receiver The message collection.
 * @return A collection of messages with a severity of [MessageSeverity.FATAL] or higher.
 */
val MessageCollection.fatal: Collection<Message> get() = filter { it.severity >= MessageSeverity.FATAL }

/**
 * Gets all messages in the collection with a severity of [MessageSeverity.ERROR] or higher.
 *
 * @receiver The message collection.
 * @return A collection of messages with a severity of [MessageSeverity.ERROR] or higher.
 */
val MessageCollection.errors: Collection<Message> get() = filter { it.severity >= MessageSeverity.ERROR }

/**
 * Determines whether the message collection has messages with a severity of [MessageSeverity.FATAL] or higher.
 *
 * @receiver The message collection.
 * @return `true` if the collection has any fatal messages, `false` otherwise.
 */
fun MessageCollection.hasFatal(): Boolean = any { it.severity >= MessageSeverity.FATAL }

/**
 * Determines whether the message collection has messages with a severity of [MessageSeverity.ERROR] or higher.
 *
 * @receiver The message collection.
 * @return `true` if the collection has any error or fatal messages, `false` otherwise.
 */
fun MessageCollection.hasErrors(): Boolean = any { it.severity >= MessageSeverity.ERROR }
