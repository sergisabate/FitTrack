package com.sergisabate.fittrack.ui.charts

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
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberFadingEdges
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

private val x = (1..10).toList()
private val axisValueOverrider = AxisValueOverrider.adaptiveYValues(yFraction = 1.2f, round = true)

@Composable
fun FitTrackChartLabeled(modifier: Modifier, label1: String, label2: String) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                lineSeries { series(x, x.map { Random.nextFloat() * 15 }) }
            }
        }
    }

    val marker = rememberMarker()
    val color = MaterialTheme.colorScheme.onSecondary

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    rememberLine(
                        remember { LineCartesianLayer.LineFill.single(fill(color)) }
                    )
                ),
                axisValueOverrider = axisValueOverrider,
            ),
            startAxis =
            rememberStartAxis(
                line = rememberAxisLineComponent(color),
                tick = rememberAxisTickComponent(color),
                guideline = rememberAxisGuidelineComponent(color),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                titleComponent =
                rememberTextComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    margins = Dimensions.of(end = 4.dp),
                    padding = Dimensions.of(8.dp, 2.dp),
                    background = rememberShapeComponent(color, Shape.Pill),
                ),
                title = label1,
            ),
            bottomAxis =
            rememberBottomAxis(
                line = rememberAxisLineComponent(color),
                tick = rememberAxisTickComponent(color),
                guideline = null,
                titleComponent =
                rememberTextComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    margins = Dimensions.of(top = 4.dp),
                    padding = Dimensions.of(8.dp, 2.dp),
                    background =
                    rememberShapeComponent(color, Shape.Pill),
                ),
                title = label2,
            ),
            marker = marker,
            horizontalLayout = HorizontalLayout.fullWidth(),
            fadingEdges = rememberFadingEdges(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false)
    )
}
