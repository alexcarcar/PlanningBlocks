// MainActivity.kt (or a new Composable file like BoardScreen.kt)
package com.example.planningblocks // Use your package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planningblocks.model.BlockData
import com.example.planningblocks.ui.theme.PlanningBlocksTheme
import com.example.planningblocks.viewmodel.BoardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlanningBlocksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    PlanningBoardScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningBoardScreen(boardViewModel: BoardViewModel = viewModel()) {
    var showLabelDialog by remember { mutableStateOf(false) }
    var currentEditingBlockId by remember { mutableStateOf<String?>(null) }
    var tempLabel by remember { mutableStateOf("") }

    // Get density here, in the composable scope of PlanningBoardScreen
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    // Convert pixel offset to DpOffset
                    // Use the density obtained from the composable scope
                    val xInDp = (offset.x / density.density).dp
                    val yInDp = (offset.y / density.density).dp
                    boardViewModel.addBlock(DpOffset(xInDp, yInDp))
                }, onDoubleTap = { /* We'll handle this on the block itself */ })
            }) {
        boardViewModel.blocks.forEach { blockData ->
            PlanningBlock(
                blockData = blockData,
                onDrag = { dragAmountPx -> // dragAmount is in pixels (Offset)
                    val currentOffsetDp = blockData.offset // This is DpOffset

                    // Convert pixel drag amount to Dp using the density from PlanningBoardScreen
                    val dragXInDp = (dragAmountPx.x / density.density).dp
                    val dragYInDp = (dragAmountPx.y / density.density).dp

                    val newOffsetX = currentOffsetDp.x + dragXInDp
                    val newOffsetY = currentOffsetDp.y + dragYInDp

                    boardViewModel.updateBlockOffset(blockData.id, DpOffset(newOffsetX, newOffsetY))
                },
                onEditLabel = {
                    currentEditingBlockId = blockData.id
                    tempLabel = blockData.label
                    showLabelDialog = true
                })
        }
    }

    if (showLabelDialog && currentEditingBlockId != null) {
        EditLabelDialog(
            initialLabel = tempLabel,
            onDismiss = { showLabelDialog = false; currentEditingBlockId = null },
            onConfirm = { newLabel ->
                currentEditingBlockId?.let {
                    boardViewModel.updateBlockLabel(it, newLabel)
                }
                showLabelDialog = false
                currentEditingBlockId = null
            })
    }
}


@Composable
fun PlanningBlock(
    blockData: BlockData,
    onDrag: (Offset) -> Unit,
    onEditLabel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get the density here, in the composable scope
    val density = LocalDensity.current

    // Calculate the offset in pixels directly
    val offsetXInPx = with(density) { blockData.offset.x.roundToPx() }
    val offsetYInPx = with(density) { blockData.offset.y.roundToPx() }

    Box(modifier = modifier
        .offset {
            IntOffset(
                offsetXInPx, offsetYInPx
            )
        } // Use the pre-calculated Px values
        .shadow(4.dp, RoundedCornerShape(8.dp))
        .background(blockData.color, RoundedCornerShape(8.dp))
        .size(100.dp, 60.dp)
        .pointerInput(blockData.id) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                onDrag(dragAmount)
            }
        }
        .pointerInput(blockData.id) { // You can combine pointerInput modifiers
            detectTapGestures(
                onDoubleTap = {
                    onEditLabel()
                })
        }
        .padding(8.dp), contentAlignment = Alignment.Center) {
        Text(
            text = blockData.label,
            color = if (blockData.color.luminance() > 0.5) Color.Black else Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Extension to calculate luminance for text color contrast
fun Color.luminance(): Float {
    val red = red * 255
    val green = green * 255
    val blue = blue * 255
    return (0.2126f * red + 0.7152f * green + 0.0722f * blue) / 255
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLabelDialog(
    initialLabel: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialLabel) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Edit Label") }, text = {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Block Label") },
            singleLine = true
        )
    }, confirmButton = {
        Button(onClick = { onConfirm(text) }) {
            Text("Confirm")
        }
    }, dismissButton = {
        Button(onClick = onDismiss) {
            Text("Cancel")
        }
    })
}
   