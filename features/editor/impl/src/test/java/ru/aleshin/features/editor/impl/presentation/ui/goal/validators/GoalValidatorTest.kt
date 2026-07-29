/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.aleshin.features.editor.impl.presentation.ui.goal.validators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class GoalValidatorTest {

    private val validator = GoalValidator.Base()

    @Test
    fun validateRejectsBlankTitleMissingScopeAndZeroTarget() {
        val errors = validator.validate(
            createGoal().copy(
                scopeType = GoalScopeType.SUB_CATEGORY,
                targetValue = "0",
            )
        )

        assertEquals(
            setOf(
                GoalValidationError.TITLE,
                GoalValidationError.SCOPE,
                GoalValidationError.TARGET,
            ),
            errors,
        )
    }

    @Test
    fun validateAcceptsAllTasksScopeWithoutCategory() {
        val errors = validator.validate(
            createGoal().copy(
                title = "English",
                targetValue = "300",
            )
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun validateRejectsDeadlineBeforeCreationDate() {
        val errors = validator.validate(
            createGoal().copy(
                title = "English",
                targetValue = "300",
                createdAt = Date(2L * MILLIS_IN_DAY),
                deadline = Date(0L),
            )
        )

        assertEquals(setOf(GoalValidationError.DEADLINE), errors)
    }

    private fun createGoal() = GoalEditUi(
        title = "",
        scopeType = GoalScopeType.ALL,
        metric = GoalMetric.DURATION,
        direction = GoalDirection.AT_LEAST,
        targetValue = "",
        createdAt = Date(0L),
        deadline = Date(MILLIS_IN_DAY),
    )
}

private const val MILLIS_IN_DAY = 24L * 60L * 60L * 1000L
