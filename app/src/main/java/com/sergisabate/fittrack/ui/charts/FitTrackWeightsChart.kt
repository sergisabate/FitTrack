package com.sergisabate.fittrack.ui.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.sergisabate.fittrack.data.viewmodels.WeightRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

private val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ ->
    val test = LocalDate.ofEpochDay(x.toLong())
    "${test.dayOfMonth}/${test.monthValue}"
}
private val axisValueOverrider = AxisValueOverrider.adaptiveYValues(yFraction = 1.1f, round = true)

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

    val marker = rememberMarker()
    val color = MaterialTheme.colorScheme.onSecondary

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(color))
                    )
                ),
                axisValueOverrider = axisValueOverrider,
            ),
            startAxis = rememberStartAxis(
                line = rememberAxisLineComponent(color),
                tick = rememberAxisTickComponent(color),
                guideline = rememberAxisGuidelineComponent(color = color, strokeColor = color),
                titleComponent =
                rememberTextComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    margins = Dimensions.of(end = 0.dp),
                    padding = Dimensions.of(8.dp, 2.dp),
                    background = rememberShapeComponent(color, Shape.Pill),
                ),
                title = "kg",
            ),
            bottomAxis = rememberBottomAxis(
                line = rememberAxisLineComponent(color),
                tick = rememberAxisTickComponent(color),
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