package com.sergisabate.fittrack.ui.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.sergisabate.fittrack.data.viewmodels.WeightRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.math.round

private val bottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
    val date = LocalDate.ofEpochDay(x.toLong())
    "${date.dayOfMonth}/${date.monthValue}"
}

@Composable
fun FitTrackWeightsChart(modifier: Modifier, weightRecords: List<WeightRecord>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                val x = weightRecords.map { it.date }
                val y = weightRecords.map { it.weight }
                lineSeries { series(x, y) }
            }
        }
    }

    val maxWeight = weightRecords.maxOfOrNull { it.weight } ?: 0f
    val minWeight = weightRecords.minOfOrNull { it.weight } ?: 0f

    val marker = rememberMarker()
    val color = MaterialTheme.colorScheme.onSecondary

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(color)),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            fill(
                                ShaderProvider.verticalGradient(
                                    arrayOf(color.copy(alpha = 0.4f), Color.Transparent)
                                )
                            )
                        ),
                        pointProvider = LineCartesianLayer.PointProvider.single(
                            LineCartesianLayer.point(
                                rememberShapeComponent(
                                    fill = fill(color),
                                    shape = CorneredShape.Pill
                                )
                            )
                        )
                    )
                ),
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minY = (round((minWeight - 50.0) / 10) * 10).coerceAtLeast(30.0),
                    maxY = (round((maxWeight + 20.0) / 10) * 10).coerceAtMost(300.0)
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                line = rememberAxisLineComponent(fill(color)),
                label = rememberAxisLabelComponent(color),
                tick = rememberAxisTickComponent(fill(color)),
                guideline = rememberAxisGuidelineComponent(fill = fill(color), strokeFill = fill(color)),
                titleComponent =
                rememberTextComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    padding = Insets(8f, 2f),
                    background = rememberShapeComponent(fill(color), CorneredShape.Pill),
                ),
                title = "kg",
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                line = rememberAxisLineComponent(fill(color)),
                label = rememberAxisLabelComponent(color),
                tick = rememberAxisTickComponent(fill(color)),
                guideline = null,
                valueFormatter = bottomAxisValueFormatter,
            ),
            marker = marker
        ),
        modelProducer = modelProducer,
        modifier = modifier.fillMaxWidth(),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}