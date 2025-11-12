package com.dyentrack.app.presentation.components

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry

@Composable
fun RadarChartMP(
    modifier: Modifier = Modifier,
    labels: List<String>,
    values1: List<Float>,
    values2: List<Float>
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


                legend.isEnabled = true
                webLineWidth = 1f
                webColor = secondaryTextColor.toArgb()
                webLineWidthInner = 1f
                webColorInner = secondaryTextColor.copy(alpha = 0.4f).toArgb()
                webAlpha = 25
                setExtraOffsets(0f, 0f, 0f, 0f)

                legend.form = Legend.LegendForm.CIRCLE
                legend.yOffset =8f
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
            // Préparer les données
            val entries1 = values1.map { RadarEntry(it) }
            val entries2 = values2.map { RadarEntry(it) }

            val dataSet1 = RadarDataSet(entries1, "Nombre Exercices").apply {
                color = colorScheme.secondary.toArgb()
                fillColor = secondaryTextColor.toArgb()
                fillAlpha = 50
                lineWidth = 2f
                setDrawFilled(false)
                setDrawValues(false)
            }

            val dataSet2 = RadarDataSet(entries2, "Nombre Séries").apply {
                color = colorScheme.primary.toArgb()
                fillColor = colorScheme.primary.toArgb()

                fillAlpha = 50
                lineWidth = 2f
                setDrawFilled(true)
                setDrawValues(true)
                valueTextColor = secondaryTextColor.toArgb()

            }
            dataSet2.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            val radarData = RadarData(dataSet1, dataSet2)
            radarChart.apply {
                isRotationEnabled = false
                data = radarData
                invalidate()
            }
            // Labels
            radarChart.xAxis.apply {
                valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
                textSize = 10f
                textColor = colorScheme.onSurface.toArgb()
            }

            radarChart.yAxis.apply {
                axisMinimum = 0f
                granularity = 5f // pour un pallier tous les 5
                textSize = 8f
                textColor = colorScheme.onSurface.copy(alpha = 0.7f).toArgb()
            }

            radarChart.invalidate()
        }
    )
}
