package com.dyentrack.app.presentation.components.chart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.dyentrack.app.R
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.createBitmap

@Composable
fun RadarChartMP(
    modifier: Modifier = Modifier,
    labels: List<String>,
    values1: List<Float>,
    values2: List<Float>,
    legend1: String = stringResource(R.string.legend_exercises),
    legend2: String = stringResource(R.string.legend_sets)
) {
    val colorScheme = MaterialTheme.colorScheme
    AndroidView(
        factory = { context: Context ->
            RadarChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    weight = 1f
                    gravity = Gravity.START or Gravity.TOP
                }
                description.isEnabled = false
                isRotationEnabled = false
                setTouchEnabled(false)
                isHighlightPerTapEnabled = false

                legend.isEnabled = true
                webLineWidth = 1f
                webColor = secondaryTextColor.toArgb()
                webLineWidthInner = 1f
                webColorInner = secondaryTextColor.copy(alpha = 0.4f).toArgb()
                webAlpha = 15
                setExtraOffsets(0f, 0f, 0f, 0f)

                legend.form = Legend.LegendForm.CIRCLE
                legend.yOffset = 8f
                legend.yEntrySpace = 10f
                legend.textColor = secondaryTextColor.toArgb()
                legend.textSize = 10f
                legend.formSize = 12f
                legend.formToTextSpace = 4f
                legend.setDrawInside(true)
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            }
        },
        modifier = modifier,
        update = { radarChart ->

            // Entries dataset 1
            val entries1 = values1.map { RadarEntry(it) }

            // Entries dataset 2 with white dot icon
            val dot = createDot(radarChart.context)
            val entries2 = values2.map {
                RadarEntry(it).apply {
                    icon = dot.toDrawable(radarChart.resources)
                }
            }

            val dataSet1 = RadarDataSet(entries1, legend1).apply {
                color = colorScheme.secondary.toArgb()
                fillColor = secondaryTextColor.toArgb()
                fillAlpha = 50
                lineWidth = 1f
                setDrawFilled(false)
                setDrawValues(false)
            }

            val dataSet2 = RadarDataSet(entries2, legend2).apply {
                color = colorScheme.primary.toArgb()
                fillColor = colorScheme.primary.toArgb()
                fillAlpha = 50
                lineWidth = 1f
                setDrawFilled(true)
                setDrawValues(true)
                valueTextColor = secondaryTextColor.toArgb()
            }

            // Formatter integer
            dataSet2.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            radarChart.data = RadarData(dataSet1, dataSet2)

            // Axis
            radarChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                textSize = 10f
                textColor = colorScheme.onSurface.toArgb()
            }

            radarChart.yAxis.apply {
                axisMinimum = 0f
                granularity = 10f
                textSize = 8f
                textColor = colorScheme.onSurface.copy(alpha = 0.7f).toArgb()
            }

            radarChart.invalidate()
        }
    )
}

fun createDot(context: Context): Bitmap {
    val size = (5 * context.resources.displayMetrics.density).toInt()
    val stroke = (1.5f * context.resources.displayMetrics.density)

    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = stroke
    }

    val radius = size / 2f - stroke
    canvas.drawCircle(size / 2f, size / 2f, radius, paint)

    return bitmap
}
