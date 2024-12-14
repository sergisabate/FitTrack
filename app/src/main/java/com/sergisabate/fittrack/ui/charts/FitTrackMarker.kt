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
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
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
  val labelBackground =
    rememberShapeComponent(
      fill = fill(Color.Black),
      shape = labelBackgroundShape,
      shadow = Shadow(
        radiusDp = 4f,
        dyDp = 2f
      )
    )
  val label =
    rememberTextComponent(
      color = MaterialTheme.colorScheme.secondary,
      textAlignment = Layout.Alignment.ALIGN_CENTER,
      padding = Dimensions(8f, 4f),
      background = labelBackground,
      minWidth = TextComponent.MinWidth.fixed(40.dp),
    )
  val indicatorFrontComponent = rememberShapeComponent(fill(MaterialTheme.colorScheme.onSecondary), CorneredShape.Pill)
  val indicatorCenterComponent = rememberShapeComponent(shape = CorneredShape.Pill)
  val indicatorRearComponent = rememberShapeComponent(shape = CorneredShape.Pill)
  val indicator =
    rememberLayeredComponent(
      rear = indicatorRearComponent,
      front =
        rememberLayeredComponent(
          rear = indicatorCenterComponent,
          front = indicatorFrontComponent,
          padding = Dimensions(5f),
        ),
      padding = Dimensions(10f),
    )
  val guideline = rememberAxisGuidelineComponent(fill = fill(MaterialTheme.colorScheme.onSecondary))
  return remember(label, labelPosition, indicator, showIndicator, guideline) {
    @SuppressLint("RestrictedApi")
    object :
      DefaultCartesianMarker(
        label = label,
        labelPosition = labelPosition,
        indicator =
          if (showIndicator) {
            { color ->
              LayeredComponent(
                rear = ShapeComponent(fill(Color(color.copyColor(alpha = 0.15f))), CorneredShape.Pill),
                front =
                  LayeredComponent(
                    rear =
                      ShapeComponent(
                        fill = fill(Color(color)),
                        shape = CorneredShape.Pill,
                        shadow = Shadow(radiusDp = 12f, color = color),
                      ),
                    front = indicatorFrontComponent,
                    padding = Dimensions(5f),
                  ),
                padding = Dimensions(10f),
              )
            }
          } else {
            null
          },
        indicatorSizeDp = 36f,
        guideline = guideline,
      ) {
      /*override fun updateInsets(
        context: CartesianMeasureContext,
        horizontalDimensions: HorizontalDimensions,
        model: CartesianChartModel,
        insets: Insets
      ) {
        with(context) {
          val baseShadowInsetDp =
            CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
          var topInset = (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
          var bottomInset = (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
          when (labelPosition) {
            LabelPosition.Top,
            LabelPosition.AbovePoint -> topInset += label.getHeight(context) + tickSizeDp.pixels
            LabelPosition.Bottom -> bottomInset += label.getHeight(context) + tickSizeDp.pixels
            LabelPosition.AroundPoint -> {}
          }
          insets.ensureValuesAtLeast(top = topInset, bottom = bottomInset)
        }
      }*/
    }
  }
}
