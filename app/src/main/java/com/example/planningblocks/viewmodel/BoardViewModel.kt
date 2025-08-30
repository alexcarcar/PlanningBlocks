// BoardViewModel.kt
package com.example.planningblocks.viewmodel // Use your package name

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.ViewModel
import com.example.planningblocks.model.BlockData
import kotlin.random.Random

class BoardViewModel : ViewModel() {
    // Using mutableStateListOf to make Compose observe changes
    val blocks = mutableStateListOf<BlockData>()

    private val availableColors = listOf(
        Color(0xFFF44336), // Red
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF2196F3), // Blue
        Color(0xFF00BCD4), // Cyan
        Color(0xFF009688), // Teal
        Color(0xFF4CAF50), // Green
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFF9800), // Orange
    )

    fun addBlock(offset: DpOffset) {
        val randomColor = availableColors.random()
        blocks.add(BlockData(offset = offset, color = randomColor))
    }

    fun updateBlockOffset(blockId: String, newOffset: DpOffset) {
        blocks.find { it.id == blockId }?.let { block ->
            val index = blocks.indexOf(block)
            if (index != -1) {
                blocks[index] = block.copy(offset = newOffset)
            }
        }
    }

    fun updateBlockLabel(blockId: String, newLabel: String) {
        blocks.find { it.id == blockId }?.let { block ->
            val index = blocks.indexOf(block)
            if (index != -1) {
                blocks[index] = block.copy(label = newLabel)
            }
        }
    }

    fun getBlockById(blockId: String): BlockData? {
        return blocks.find { it.id == blockId }
    }
}
   