package com.example.ui.graph

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ExpressionParser
import com.example.ui.MainViewModel
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphingScreen(viewModel: MainViewModel) {
    var graphDimensionMode by remember { mutableStateOf("2D") } // "2D" or "3D"

    // 2D Parameters
    var equationInput by remember { mutableStateOf("sin(x)") }
    var scaleY by remember { mutableFloatStateOf(50.0f) } // pixels per unit
    var centerX by remember { mutableFloatStateOf(0.0f) } // center coordinate X
    var centerY by remember { mutableFloatStateOf(0.0f) } // center coordinate Y

    // Interactive slider values
    var variableA by remember { mutableFloatStateOf(1.0f) }
    var variableB by remember { mutableFloatStateOf(0.0f) }
    var variableC by remember { mutableFloatStateOf(0.0f) }

    // Calculus modes
    var showTangent by remember { mutableStateOf(false) }
    var tangentPointX by remember { mutableFloatStateOf(1.0f) }
    var showIntegral by remember { mutableStateOf(false) }
    var integralLowerLimit by remember { mutableFloatStateOf(-2.0f) }
    var integralUpperLimit by remember { mutableFloatStateOf(2.0f) }

    // 3D Parameters
    var equation3DInput by remember { mutableStateOf("sin(x) * cos(y)") }
    var rotX by remember { mutableFloatStateOf(60.0f) } // X-axis rotation angle in degrees
    var rotZ by remember { mutableFloatStateOf(45.0f) } // Z-axis rotation angle in degrees
    var scale3D by remember { mutableFloatStateOf(60.0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Mode Selector: 2D or 3D
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Graphing Suite",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            SingleChoiceSegmentedButtonRow {
                val dimensions = listOf("2D", "3D")
                dimensions.forEachIndexed { index, dim ->
                    SegmentedButton(
                        selected = graphDimensionMode == dim,
                        onClick = { graphDimensionMode = dim },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = dimensions.size)
                    ) {
                        Text(dim)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (graphDimensionMode == "2D") {
            // 2D GRAPHING CONTROLS
            OutlinedTextField(
                value = equationInput,
                onValueChange = { equationInput = it },
                label = { Text("Enter Cartesian Function y = f(x, a, b, c)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("graph_equation_input"),
                trailingIcon = {
                    IconButton(onClick = {
                        equationInput = "sin(x)"
                        centerX = 0f
                        centerY = 0f
                        scaleY = 50f
                        variableA = 1f
                        variableB = 0f
                        variableC = 0f
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Graph Canvas with drag navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            centerX -= dragAmount.x / scaleY
                            centerY += dragAmount.y / scaleY
                        }
                    }
                    .testTag("graph_canvas_2d")
            ) {
                val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                val curveColor = MaterialTheme.colorScheme.primary
                val tangentColor = MaterialTheme.colorScheme.tertiary
                val integralColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val halfW = w / 2f
                    val halfH = h / 2f

                    // 1. Draw Grid Lines
                    val startX = centerX - halfW / scaleY
                    val endX = centerX + halfW / scaleY
                    val startY = centerY - halfH / scaleY
                    val endY = centerY + halfH / scaleY

                    // Draw vertical grids & X ticks
                    val xInterval = if (scaleY < 30) 2.0 else 1.0
                    var currentGridX = (ceil(startX / xInterval) * xInterval).toFloat()
                    while (currentGridX <= endX) {
                        val u = halfW + (currentGridX - centerX) * scaleY
                        drawLine(
                            color = gridColor,
                            start = Offset(u, 0f),
                            end = Offset(u, h),
                            strokeWidth = 1f
                        )
                        currentGridX += xInterval.toFloat()
                    }

                    // Draw horizontal grids & Y ticks
                    val yInterval = if (scaleY < 30) 2.0 else 1.0
                    var currentGridY = (ceil(startY / yInterval) * yInterval).toFloat()
                    while (currentGridY <= endY) {
                        val v = halfH - (currentGridY - centerY) * scaleY
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, v),
                            end = Offset(w, v),
                            strokeWidth = 1f
                        )
                        currentGridY += yInterval.toFloat()
                    }

                    // 2. Draw Main Axes
                    val axisU = halfW - centerX * scaleY
                    val axisV = halfH + centerY * scaleY
                    // Y axis
                    drawLine(
                        color = axisColor,
                        start = Offset(axisU, 0f),
                        end = Offset(axisU, h),
                        strokeWidth = 2.dp.toPx()
                    )
                    // X axis
                    drawLine(
                        color = axisColor,
                        start = Offset(0f, axisV),
                        end = Offset(w, axisV),
                        strokeWidth = 2.dp.toPx()
                    )

                    // 3. Plot Function Curve
                    val path = Path()
                    var first = true
                    for (u in 0..w.toInt() step 2) {
                        val x = centerX + (u - halfW) / scaleY
                        val vars = mapOf(
                            "x" to x.toDouble(),
                            "a" to variableA.toDouble(),
                            "b" to variableB.toDouble(),
                            "c" to variableC.toDouble()
                        )
                        val y = ExpressionParser.evaluate(equationInput, vars)
                        if (!y.isNaN() && !y.isInfinite()) {
                            val v = halfH - (y - centerY) * scaleY
                            if (first) {
                                path.moveTo(u.toFloat(), v.toFloat())
                                first = false
                            } else {
                                path.lineTo(u.toFloat(), v.toFloat())
                            }
                        }
                    }
                    drawPath(
                        path = path,
                        color = curveColor,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // 4. Shade Integral Area if requested
                    if (showIntegral) {
                        val integralPath = Path()
                        val lowerU = halfW + (integralLowerLimit - centerX) * scaleY
                        val upperU = halfW + (integralUpperLimit - centerX) * scaleY

                        // Move to base axis lower limit
                        integralPath.moveTo(lowerU, axisV)

                        for (u in lowerU.toInt()..upperU.toInt() step 2) {
                            val x = centerX + (u - halfW) / scaleY
                            val vars = mapOf(
                                "x" to x.toDouble(),
                                "a" to variableA.toDouble(),
                                "b" to variableB.toDouble(),
                                "c" to variableC.toDouble()
                            )
                            val y = ExpressionParser.evaluate(equationInput, vars)
                            if (!y.isNaN()) {
                                val v = halfH - (y - centerY) * scaleY
                                integralPath.lineTo(u.toFloat(), v.toFloat())
                            }
                        }
                        integralPath.lineTo(upperU, axisV)
                        integralPath.close()
                        drawPath(path = integralPath, color = integralColor)
                    }

                    // 5. Draw Tangent Line if requested
                    if (showTangent) {
                        val x0 = tangentPointX.toDouble()
                        val delta = 1e-5
                        val vars0 = mapOf(
                            "x" to x0,
                            "a" to variableA.toDouble(),
                            "b" to variableB.toDouble(),
                            "c" to variableC.toDouble()
                        )
                        val varsDelta = mapOf(
                            "x" to x0 + delta,
                            "a" to variableA.toDouble(),
                            "b" to variableB.toDouble(),
                            "c" to variableC.toDouble()
                        )
                        val y0 = ExpressionParser.evaluate(equationInput, vars0)
                        val yDelta = ExpressionParser.evaluate(equationInput, varsDelta)

                        if (!y0.isNaN() && !yDelta.isNaN()) {
                            val slope = (yDelta - y0) / delta
                            // Tangent equation: y - y0 = slope * (x - x0) -> y = slope*(x - x0) + y0
                            val tangentPath = Path()
                            val tStartX = centerX - halfW / scaleY
                            val tEndX = centerX + halfW / scaleY

                            val tStartY = slope * (tStartX - x0) + y0
                            val tEndY = slope * (tEndX - x0) + y0

                            val tStartU = halfW + (tStartX - centerX) * scaleY
                            val tStartV = halfH - (tStartY - centerY) * scaleY
                            val tEndU = halfW + (tEndX - centerX) * scaleY
                            val tEndV = halfH - (tEndY - centerY) * scaleY

                            drawLine(
                                color = tangentColor,
                                start = Offset(tStartU, tStartV.toFloat()),
                                end = Offset(tEndU, tEndV.toFloat()),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Highlight tangent point
                            val ptU = halfW + (x0 - centerX) * scaleY
                            val ptV = halfH - (y0 - centerY) * scaleY
                            drawCircle(
                                color = tangentColor,
                                radius = 6.dp.toPx(),
                                center = Offset(ptU.toFloat(), ptV.toFloat())
                            )
                        }
                    }
                }

                // Overlay Controls: Zoom In / Out
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { scaleY *= 1.3f },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                    }
                    FloatingActionButton(
                        onClick = { scaleY /= 1.3f },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Variable Sliders
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Interactive Variable Sliders", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("a: ${String.format("%.2f", variableA)}", modifier = Modifier.width(60.dp), fontSize = 12.sp)
                        Slider(
                            value = variableA,
                            onValueChange = { variableA = it },
                            valueRange = -5f..5f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("b: ${String.format("%.2f", variableB)}", modifier = Modifier.width(60.dp), fontSize = 12.sp)
                        Slider(
                            value = variableB,
                            onValueChange = { variableB = it },
                            valueRange = -5f..5f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calculus Helpers Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showTangent,
                    onClick = { showTangent = !showTangent },
                    label = { Text("Tangent") }
                )
                if (showTangent) {
                    Slider(
                        value = tangentPointX,
                        onValueChange = { tangentPointX = it },
                        valueRange = -4f..4f,
                        modifier = Modifier.weight(1f)
                    )
                }

                FilterChip(
                    selected = showIntegral,
                    onClick = { showIntegral = !showIntegral },
                    label = { Text("Integral Shade") }
                )
            }
        } else {
            // 3D GRAPHING MODE
            OutlinedTextField(
                value = equation3DInput,
                onValueChange = { equation3DInput = it },
                label = { Text("Enter 3D Surface z = f(x, y)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("graph_3d_input"),
                trailingIcon = {
                    IconButton(onClick = {
                        equation3DInput = "sin(x) * cos(y)"
                        rotX = 60f
                        rotZ = 45f
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            rotZ += dragAmount.x * 0.5f
                            rotX -= dragAmount.y * 0.5f
                        }
                    }
                    .testTag("graph_canvas_3d")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val halfW = w / 2f
                    val halfH = h / 2f

                    // Projection Math: Orthographic projection with X and Z rotations
                    val radX = Math.toRadians(rotX.toDouble())
                    val radZ = Math.toRadians(rotZ.toDouble())

                    // Define range for 3D domain
                    val gridPoints = 20
                    val range = 4.0
                    val step = (range * 2) / gridPoints

                    // Compute grid vertex points projected to 2D
                    val projectedVertices = Array(gridPoints + 1) { Array(gridPoints + 1) { Offset(0f, 0f) } }

                    for (i in 0..gridPoints) {
                        val x = -range + i * step
                        for (j in 0..gridPoints) {
                            val y = -range + j * step
                            val vars = mapOf("x" to x, "y" to y)
                            val zVal = ExpressionParser.evaluate(equation3DInput, vars)
                            val z = if (zVal.isNaN() || zVal.isInfinite()) 0.0 else zVal

                            // Standard 3D rotations:
                            // Rotate Z (around Z-axis)
                            val rx = x * cos(radZ) - y * sin(radZ)
                            val ry = x * sin(radZ) + y * cos(radZ)
                            val rz = z

                            // Rotate X (around X-axis)
                            val fx = rx
                            val fy = ry * cos(radX) - rz * sin(radX)
                            val fz = ry * sin(radX) + rz * cos(radX)

                            // Translate projection to Screen coordinates
                            val screenX = halfW + (fx * scale3D).toFloat()
                            val screenY = halfH - (fy * scale3D).toFloat() // Y is inverted in screen space

                            projectedVertices[i][j] = Offset(screenX, screenY)
                        }
                    }

                    // Draw Wireframe Lines
                    val wireColor = Color(0xFF62DC9F).copy(alpha = 0.6f)
                    for (i in 0..gridPoints) {
                        for (j in 0..gridPoints) {
                            if (i < gridPoints) {
                                drawLine(
                                    color = wireColor,
                                    start = projectedVertices[i][j],
                                    end = projectedVertices[i + 1][j],
                                    strokeWidth = 1f
                                )
                            }
                            if (j < gridPoints) {
                                drawLine(
                                    color = wireColor,
                                    start = projectedVertices[i][j],
                                    end = projectedVertices[i][j + 1],
                                    strokeWidth = 1f
                                )
                            }
                        }
                    }
                }

                // Overlay hints
                Text(
                    "Drag to rotate surface",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3D parameters control card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("3D Projection Properties", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Zoom: ${scale3D.toInt()}", modifier = Modifier.width(80.dp), fontSize = 12.sp)
                        Slider(
                            value = scale3D,
                            onValueChange = { scale3D = it },
                            valueRange = 20f..150f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
