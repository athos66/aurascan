package com.aura.scanlab

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IngredientMatcherTest {

    @Test
    fun testExactMatch() {
        assertTrue(IngredientMatcher.fuzzyMatch("Titanium Dioxide", "Titanium Dioxide"))
    }

    @Test
    fun testCaseInsensitiveMatch() {
        assertTrue(IngredientMatcher.fuzzyMatch("titanium dioxide", "Titanium Dioxide"))
    }

    @Test
    fun testContainedMatch() {
        assertTrue(IngredientMatcher.fuzzyMatch("Contains Titanium Dioxide and others", "Titanium Dioxide"))
    }

    @Test
    fun testFuzzyMatch_OneCharDiff() {
        assertTrue(IngredientMatcher.fuzzyMatch("Titanium Dioxid", "Titanium Dioxide"))
    }

    @Test
    fun testFuzzyMatch_TwoCharsDiff() {
        assertTrue(IngredientMatcher.fuzzyMatch("Titanium Dioxi", "Titanium Dioxide"))
    }

    @Test
    fun testNoMatch() {
        assertFalse(IngredientMatcher.fuzzyMatch("Normal Sugar", "Titanium Dioxide"))
    }

    @Test
    fun testLevenshteinDistance() {
        assertEquals(0, IngredientMatcher.levenshteinDistance("test", "test"))
        assertEquals(1, IngredientMatcher.levenshteinDistance("test", "tost"))
        assertEquals(2, IngredientMatcher.levenshteinDistance("test", "toast"))
    }
}
