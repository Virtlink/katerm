//package net.pelsmaeker.katerm.io
//
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.spec.style.funSpec
//import io.kotest.matchers.shouldBe
//import io.kotest.property.checkAll
//import net.pelsmaeker.katerm.TermAttachments
//
//
//
///** Tests an implementation of the [TermAttachments] interface. */
//class TermAttachmentsTests: FunSpec({
//
//    context("size property") {
//        test("should return the number of key/value pairs in the map") {
//            checkAll<Map<Any, List<Any>>> {
//                // Arrange
//                val attachments = TermAttachments.from(it)
//
//                // Assert
//                attachments.size shouldBe it.size
//            }
//        }
//    }
//
//    context("isEmpty() method") {
//        test("should return true if the map is empty") {
//            checkAll<Map<Any, List<Any>>> {
//                // Arrange
//                val attachments = TermAttachments.from(it)
//
//                // Assert
//                attachments.isEmpty() shouldBe it.isEmpty()
//            }
//        }
//
//        test("should return false if the map is not empty") {
//            checkAll<Map<Any, List<Any>>> {
//                // Arrange
//                val attachments = TermAttachments.from(it)
//
//                // Assert
//                attachments.isNotEmpty() shouldBe it.isNotEmpty()
//            }
//        }
//    }
//
//    context("containsKey() method") {
//        xtest("should return true if the map contains the key") {
//            TODO()
//        }
//
//        xtest("should return false if the map does not contain the key") {
//            TODO()
//        }
//    }
//
//    context("get() method") {
//        xtest("should return a set of values for the key") {
//            TODO()
//        }
//
//        xtest("should return an empty set if the map does not contain the key") {
//            TODO()
//        }
//    }
//
//    context("getOrDefault() method") {
//        xtest("should return a set of values for the key") {
//            TODO()
//        }
//
//        xtest("should return the default value if the map does not contain the key") {
//            TODO()
//        }
//    }
//
//    context("keys property") {
//        test("should return a set of keys") {
//            checkAll<Map<Any, List<Any>>> {
//                // Arrange
//                val attachments = TermAttachments.from(it)
//
//                // Assert
//                attachments.keys shouldBe it.keys
//            }
//        }
//    }
//
//    context("entries property") {
//        test("should return a set of key/value pairs in the map") {
//            checkAll<Map<Any, List<Any>>> {
//                // Arrange
//                val attachments = TermAttachments.from(it)
//
//                // Assert
//                attachments.entries shouldBe it.entries.groupBy { (k, _) -> k }.mapValues { (_, v) -> v.map { (_, v) -> v } }
//            }
//        }
//    }
//
//})