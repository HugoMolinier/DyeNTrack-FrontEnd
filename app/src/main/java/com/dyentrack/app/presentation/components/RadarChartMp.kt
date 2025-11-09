package com.dyentrack.app.presentation.components

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.charts.RadarChart
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
                description.isEnabled = false
                legend.isEnabled = true
                webLineWidth = 1f
                webColor = "#CCCCCC".toColorInt()
                webLineWidthInner = 1f
                webColorInner = "#AAAAAA".toColorInt()
                webAlpha = 100
            }
        },
        modifier = modifier,
        update = { radarChart ->
            // Préparer les données
            val entries1 = values1.map { RadarEntry(it) }
            val entries2 = values2.map { RadarEntry(it) }

            val dataSet1 = RadarDataSet(entries1, "Exercices").apply {
                color = colorScheme.secondary.toArgb()
                fillColor = colorScheme.secondary.toArgb()
                fillAlpha = 100
                lineWidth = 2f
                setDrawFilled(true)
            }

            val dataSet2 = RadarDataSet(entries2, "Séries").apply {
                color = colorScheme.primary.toArgb()
                fillColor = colorScheme.primary.toArgb()
                fillAlpha = 50
                lineWidth = 2f
                setDrawFilled(true)
            }

            val radarData = RadarData(dataSet1, dataSet2)
            radarData.setDrawValues(true)
            radarChart.apply {
                isRotationEnabled = false
                data = radarData
                invalidate()
            }
            // Labels
            radarChart.xAxis.apply {
                valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
                textSize = 12f
                textColor = colorScheme.onSurface.toArgb()
            }

            radarChart.yAxis.apply {
                axisMinimum = 0f
                granularity = 5f // pour un pallier tous les 5
                textSize = 12f
                textColor = colorScheme.onSurface.toArgb()
            }
            radarChart.legend.apply {
                textColor = colorScheme.onSurface.toArgb()  // couleur du texte de la légende
                textSize = 14f                               // taille du texte
                form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE // forme
                formSize = 12f
                formToTextSpace = 8f
                yOffset = 0f
                yEntrySpace = 4f
            }
            radarChart.invalidate()
        }
    )
}
