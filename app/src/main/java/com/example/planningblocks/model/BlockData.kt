// BlockData.kt
package com.example.planningblocks.model // Use your package name

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import java.util.UUID

data class BlockData(
    val id: String = UUID.randomUUID().toString(), // Unique ID for each block
    var offset: DpOffset = DpOffset.Zero,
    var label: String = "",
    var color: Color = Color.LightGray // Default color
)