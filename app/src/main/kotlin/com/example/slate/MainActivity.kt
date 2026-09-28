package com.example.slate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.slate.R
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrawingApp()
        }
    }
}

@Preview
@Composable
fun DrawingApp() {

    val paths = remember {
        mutableStateListOf<Path>()
    }

    var currentPath by remember {
        mutableStateOf<Path?>(null)
    }

    var drawVersion by remember {
        mutableIntStateOf(0)
    }

    var strokein by remember {
        mutableStateOf("8")
    }

    // Toolbar position
    var toolbarX by remember {
        mutableFloatStateOf(0f)
    }

    var toolbarY by remember {
        mutableFloatStateOf(0f)
    }

    val strokeWidth = strokein.toFloatOrNull() ?: 8f

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // =========================
        // Drawing Canvas
        // =========================

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {

                    detectDragGestures(

                        onDragStart = { position ->

                            val path = Path()

                            path.moveTo(
                                position.x,
                                position.y
                            )

                            currentPath = path
                        },

                        onDrag = { change, _ ->

                            currentPath?.let { path ->

                                path.lineTo(
                                    change.position.x,
                                    change.position.y
                                )

                                drawVersion++

                                change.consume()
                            }
                        },

                        onDragEnd = {

                            currentPath?.let { path ->
                                paths.add(path)
                            }

                            currentPath = null
                            drawVersion++
                        },

                        onDragCancel = {

                            currentPath = null
                            drawVersion++
                        }
                    )
                }
        ) {

            // Force redraw
            drawVersion

            // Completed paths
            paths.forEach { path ->

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Current path
            currentPath?.let { path ->

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }


        // =========================
        // Draggable Toolbar
        // =========================

        Column(
            modifier = Modifier
                .offset {
                    IntOffset(
                        toolbarX.roundToInt(),
                        toolbarY.roundToInt()
                    )
                }
                .padding(8.dp)
                .background(
                    Color(0x47FF2D55)
                
                )
                .padding(8.dp)
                .pointerInput(Unit) {

                    detectDragGestures { change, dragAmount ->

                        change.consume()

                        toolbarX += dragAmount.x
                        toolbarY += dragAmount.y
                    }
                }
        ) {

            // Undo Button
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF2D55)
                ),
                onClick = {

                    if (paths.isNotEmpty()) {

                        paths.removeAt(
                            paths.lastIndex
                        )

                        drawVersion++
                    }
                }
            ) {

                Icon(
                    painter = painterResource(
                        R.drawable.undo
                    ),
                    contentDescription = "Undo"
                )
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // Delete Button
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF2D55)
                ),
                onClick = {

                    paths.clear()

                    currentPath = null

                    drawVersion++
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // Stroke Width
            OutlinedTextField(
                value = strokein,

                onValueChange = {
                    strokein = it
                },

                label = {
                    Text("width")
                },

                modifier = Modifier
                    .width(90.dp)
            )
        }
    }
}