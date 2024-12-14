package com.sergisabate.fittrack.ui.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberFadingEdges
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

private val x = (1..10).toList()
//private val axisValueOverrider = AxisValueOverrider.adaptiveYValues(yFraction = 1.2f, round = true)

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
                lineProvider = LineCartesianLayer.LineProvider.series(
                    vicoTheme.lineCartesianLayerColors.map {
                        LineCartesianLayer.rememberLine(LineCartesianLayer.LineFill.single(fill(color)))
                }),
                //axisValueOverrider = axisValueOverrider,
            ),
            startAxis = VerticalAxis.rememberStart(
                line = rememberAxisLineComponent(fill(color)),
                tick = rememberAxisTickComponent(fill(color)),
                guideline = rememberAxisGuidelineComponent(fill(color)),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                titleComponent =
                rememberTextComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    margins = Dimensions(endDp = 4f),
                    padding = Dimensions(8f, 2f),
                    background = rememberShapeComponent(fill(color), CorneredShape.Pill),
                ),
                title = label1,
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                line = rememberAxisLineComponent(fill(color)),
                tick = rememberAxisTickComponent(fill(color)),
                guideline = null,
                titleComponent =
                rememberTextComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    margins = Dimensions(topDp = 4f),
                    padding = Dimensions(8f, 2f),
                    background = rememberShapeComponent(fill(color), CorneredShape.Pill),
                ),
                title = label2,
            ),
            marker = marker,
            //horizontalLayout = HorizontalLayout.fullWidth(),
            fadingEdges = rememberFadingEdges(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false)
    )
}
