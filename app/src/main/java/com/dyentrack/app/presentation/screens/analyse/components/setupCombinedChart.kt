package com.dyentrack.app.presentation.screens.analyse.components

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun CombinedChartMP(
    modifier: Modifier = Modifier,
    labels: List<String>,
    barValues: List<Float>,  // somme pondérée
    lineValues: List<Float>
) {
    val colorScheme = MaterialTheme.colorScheme

    AndroidView(
        factory = { context ->
            CombinedChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setTouchEnabled(false)
                isHighlightPerTapEnabled = false
                legend.textColor = secondaryTextColor.toArgb()
                legend.isEnabled = true
                setExtraOffsets(0f, 0f, 0f, 0f)
            }
        },
        modifier = modifier,
        update = { chart ->

            // Bar Entries (somme pondérée) → axe gauche
            val barEntries = barValues.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value)
            }

            // Line Entries (ratio fail/set) → axe droite invisible
            val lineEntries = lineValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }

            val barDataSet = BarDataSet(barEntries, "Somme pondérée").apply {
                color = colorScheme.primary.copy(alpha = 0.7f).toArgb()
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawValues(true)
                valueTextColor = colorScheme.onSurface.toArgb()
            }

            val lineDataSet = LineDataSet(lineEntries, "Ratio fail/set").apply {
                color = colorScheme.secondary.copy(alpha = 0.4f).toArgb()
                lineWidth = 2f
                setDrawCircles(true)
                setDrawValues(true)
                valueTextColor = colorScheme.onSurface.toArgb()
                axisDependency = YAxis.AxisDependency.RIGHT
                valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${(value * 100).toInt()}%"  // valeur sur le point
                    }
                }
            }

            val barData = BarData(barDataSet)
            val lineData = LineData(lineDataSet)

            val combinedData = CombinedData()
            combinedData.setData(barData)
            combinedData.setData(lineData)

            chart.data = combinedData
            val labelsWithoutYear = labels.map { label ->
                label.split("-").drop(1).joinToString("/") // garde seulement jour et mois
            }
            // X Axis
            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labelsWithoutYear)
                textColor = secondaryTextColor.toArgb()
                textSize = 10f
            }

            // Axe gauche → barre somme pondérée
            chart.axisLeft.apply {
                axisMinimum = 0f
                textColor = secondaryTextColor.toArgb()
            }

            // Axe droite → ratio invisible
            chart.axisRight.apply {
                axisMinimum = 0f
                setDrawGridLines(false)
                textColor = secondaryTextColor.toArgb()
                axisMaximum = 1f
            }

            chart.invalidate()
        }
    )
}
