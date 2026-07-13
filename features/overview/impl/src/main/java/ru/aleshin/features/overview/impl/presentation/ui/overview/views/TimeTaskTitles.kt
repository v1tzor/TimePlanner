/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.impl.presentation.ui.overview.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** @author Stanislav Aleshin on 11.07.2026. */
@Composable
internal fun TimeTaskTitles(modifier: Modifier = Modifier, title: String, titleColor: Color, subTitle: String?) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Text(text = title, color = titleColor, style = MaterialTheme.typography.titleMedium)
        if (subTitle != null) Text(modifier = Modifier.padding(top = 2.dp), text = subTitle, color = titleColor, style = MaterialTheme.typography.bodyMedium)
    }
}
