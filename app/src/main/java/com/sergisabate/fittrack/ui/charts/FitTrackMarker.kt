package com.sergisabate.fittrack.ui.charts

import android.annotation.SuppressLint
import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.copyColor
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
internal fun rememberMarker(
  labelPosition: DefaultCartesianMarker.LabelPosition = DefaultCartesianMarker.LabelPosition.Top,
  showIndicator: Boolean = true,
): CartesianMarker {
  val labelBackgroundShape = CorneredShape.Pill
  val labelBackground = rememberShapeComponent(
      fill = fill(Color.Black),
      shape = labelBackgroundShape,
      shadow = Shadow(radiusDp = 4f, yDp = 2f)
  )
  val label = rememberTextComponent(
      color = Color.White,
      textAlignment = Layout.Alignment.ALIGN_CENTER,
      padding = Insets(8f, 4f),
      background = labelBackground,
      minWidth = TextComponent.MinWidth.fixed(40.dp)
  )
  val indicatorFrontComponent = rememberShapeComponent(fill(MaterialTheme.colorScheme.onSecondary), CorneredShape.Pill)
  val guideline = rememberAxisGuidelineComponent(fill = fill(MaterialTheme.colorScheme.onSecondary))
  return remember(label, labelPosition, showIndicator, guideline) {
    @SuppressLint("RestrictedApi")
    object :
      DefaultCartesianMarker(
        label = label,
        labelPosition = labelPosition,
        indicator =
          if (showIndicator) {
            { color ->
              LayeredComponent(
                back = ShapeComponent(fill(Color(color.copyColor(alpha = 0.15f))), CorneredShape.Pill),
                front =
                  LayeredComponent(
                    back =
                      ShapeComponent(
                        fill = fill(Color(color)),
                        shape = CorneredShape.Pill,
                        shadow = Shadow(radiusDp = 12f, color = color),
                      ),
                    front = indicatorFrontComponent,
                    padding = Insets(5f),
                  ),
                padding = Insets(10f),
              )
            }
          } else {
            null
          },
        indicatorSizeDp = 36f,
        guideline = guideline,
      ) {
    }
  }
}
