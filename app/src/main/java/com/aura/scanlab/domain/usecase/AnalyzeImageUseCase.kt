package com.aura.scanlab.domain.usecase

import com.aura.scanlab.domain.model.HazardLevel
import com.aura.scanlab.domain.model.Ingredient
import com.aura.scanlab.IngredientMatcher

class AnalyzeImageUseCase {
    /**
     * Matches detected lines against the database of harmful ingredients.
     * Returns a list of matched Ingredient objects.
     */
    fun matchIngredients(detectedLines: List<String>, allIngredients: List<Ingredient>): List<Ingredient> {
        val matches = mutableListOf<Ingredient>()
        
        outer@for (line in detectedLines) {
            for (ingredient in allIngredients) {
                if (IngredientMatcher.fuzzyMatch(line, ingredient.name)) {
                    if (!matches.any { it.name == ingredient.name }) {
                        matches.add(ingredient)
                    }
                }
            }
        }
        return matches
    }
}
