/*
 * Copyright 2023 Stanislav Aleshin
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

package ru.aleshin.core.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.material.surfaceOne

/**
 * @author Stanislav Aleshin on 04.10.2023.
 */
@Composable
fun CustomSmallTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onTextChange: (String) -> Unit,
    label: @Composable () -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.onSurface),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceOne(),
        border = when (isFocused) {
            true -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
            false -> null
        },
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
        ) { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.invoke()
                Column(modifier = Modifier.weight(1f)) {
                    ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                            content = label,
                        )
                        if (text.isEmpty() && !isFocused && placeholder != null) {
                            Box {
                                ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                                    CompositionLocalProvider(
                                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                                        content = placeholder,
                                    )
                                }
                            }
                        } else {
                            innerTextField()
                        }
                    }
                }
                trailingIcon?.invoke()
            }
        }
    }
}

@Composable
fun CustomLargeTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onTextChange: (String) -> Unit,
    label: @Composable () -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.onSurface),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 64.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceOne(),
        border = when (isFocused) {
            true -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
            false -> null
        },
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
        ) { innerTextField ->
            Row(
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = if (trailingIcon != null) 8.dp else 16.dp,
                    end = if (trailingIcon != null) 8.dp else 16.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.invoke()
                Column(modifier = Modifier.weight(1f)) {
                    ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                            content = label,
                        )
                        if (text.isEmpty() && !isFocused && placeholder != null) {
                            Box {
                                ProvideTextStyle(value = MaterialTheme.typography.titleMedium) {
                                    CompositionLocalProvider(
                                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                                        content = placeholder,
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.padding(top = 2.dp),
                                content = { innerTextField() },
                            )
                        }
                    }
                }
                trailingIcon?.invoke()
            }
        }
    }
}

@Composable
fun CustomLargeTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    label: @Composable () -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.onSurface),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 64.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceOne(),
        border = when (isFocused) {
            true -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
            false -> null
        },
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
        ) { innerTextField ->
            Row(
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = if (leadingIcon == null) 8.dp else 16.dp,
                    end = if (trailingIcon == null) 8.dp else 16.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.invoke()
                Column(modifier = Modifier.weight(1f)) {
                    ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                            content = label,
                        )
                        if (text.text.isEmpty() && !isFocused && placeholder != null) {
                            Box {
                                ProvideTextStyle(value = MaterialTheme.typography.titleMedium) {
                                    CompositionLocalProvider(
                                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                                        content = placeholder,
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.padding(top = 2.dp),
                                content = { innerTextField() },
                            )
                        }
                    }
                }
                trailingIcon?.invoke()
            }
        }
    }
}
