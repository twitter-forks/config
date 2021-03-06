/**
 *   Copyright (C) 2011 Typesafe Inc. <http://typesafe.com>
 */
package com.twitter_typesafe.config.impl

import org.junit.Assert._
import org.junit._

class UtilTest extends TestUtils {
    private lazy val supplementaryChars = {
        val sb = new java.lang.StringBuilder()
        val codepoints = Seq(
            0x2070E, 0x20731, 0x20779, 0x20C53, 0x20C78,
            0x20C96, 0x20CCF, 0x20CD5, 0x20D15, 0x20D7C)
        for (c <- codepoints) {
            sb.appendCodePoint(c)
        }
        assertTrue(sb.length() > codepoints.length)
        sb.toString()
    }

    @Test
    def unicodeTrimSupplementaryChars() {
        assertEquals("", ConfigImplUtil.unicodeTrim(""))
        assertEquals("a", ConfigImplUtil.unicodeTrim("a"))
        assertEquals("abc", ConfigImplUtil.unicodeTrim("abc"))
        assertEquals("", ConfigImplUtil.unicodeTrim("   \n   \n  \u00A0 "))
        assertEquals(supplementaryChars, ConfigImplUtil.unicodeTrim(supplementaryChars))

        val s = " \u00A0 \n  " + supplementaryChars + "  \n  \u00A0 "
        val asciiTrimmed = s.trim()
        val unitrimmed = ConfigImplUtil.unicodeTrim(s)

        assertFalse(asciiTrimmed.equals(unitrimmed))
        assertEquals(supplementaryChars, unitrimmed)
    }

    @Test
    def definitionOfWhitespace() {
        assertTrue(ConfigImplUtil.isWhitespace(' '))
        assertTrue(ConfigImplUtil.isWhitespace('\n'))
        // these three are nonbreaking spaces
        assertTrue(ConfigImplUtil.isWhitespace('\u00A0'))
        assertTrue(ConfigImplUtil.isWhitespace('\u2007'))
        assertTrue(ConfigImplUtil.isWhitespace('\u202F'))
        // vertical tab, a weird one
        assertTrue(ConfigImplUtil.isWhitespace('\u000B'))
        // file separator, another weird one
        assertTrue(ConfigImplUtil.isWhitespace('\u001C'))
    }

    @Test
    def equalsThatHandlesNull() {
        assertTrue(ConfigImplUtil.equalsHandlingNull(null, null))
        assertFalse(ConfigImplUtil.equalsHandlingNull(new Object(), null))
        assertFalse(ConfigImplUtil.equalsHandlingNull(null, new Object()))
        assertTrue(ConfigImplUtil.equalsHandlingNull("", ""))
    }

    private def roundtripJson(s: String) {
        val rendered = ConfigImplUtil.renderJsonString(s)
        val parsed = parseConfig("{ foo: " + rendered + "}").getString("foo")
        assertTrue("String round-tripped through maybe-unquoted escaping '" + s + "' " + s.length +
            " rendering '" + rendered + "' " + rendered.length +
            " parsed '" + parsed + "' " + parsed.length,
            s == parsed)
    }

    private def roundtripUnquoted(s: String) {
        val rendered = ConfigImplUtil.renderStringUnquotedIfPossible(s)
        val parsed = parseConfig("{ foo: " + rendered + "}").getString("foo")
        assertTrue("String round-tripped through maybe-unquoted escaping '" + s + "' " + s.length +
            " rendering '" + rendered + "' " + rendered.length +
            " parsed '" + parsed + "' " + parsed.length,
            s == parsed)
    }

    // These strings are used in many different ways, but for testing how things
    // render we don't want to have any substitutions because this render code
    // does not resolve the configs.
    val lotsOfStrings = (invalidJson ++ validConf).map(_.test).filter(_.indexOf("${") == -1)

    @Test
    def renderJsonString() {
        for (s <- lotsOfStrings) {
            roundtripJson(s)
        }
    }

    @Test
    def renderUnquotedIfPossible() {
        for (s <- lotsOfStrings) {
            roundtripUnquoted(s)
        }
    }
}
