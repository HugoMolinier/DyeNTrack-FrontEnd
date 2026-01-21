package com.dyentrack.app.presentation.components.chart

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.dyentrack.app.presentation.theme.BlueSub
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun CombinedChartMP(
    modifier: Modifier = Modifier,
    labels: List<String>,
    barValues: List<Float>,
    lineValues: List<Float>,
    showValueBar:Boolean=true,
    showValueLine:Boolean=true,
    axisMaximumLine : Float=1f,
    barLegendLabel: String = "legend Bar",
    barLegendColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
    lineLegendLabel: String = "legend Line",
    lineLegendColor: Color = BlueSub
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

            // Bar entries
            val barEntries = barValues.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value)
            }

            // Line entries
            val lineEntries = lineValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }

            val barDataSet = BarDataSet(barEntries, barLegendLabel).apply {
                color = barLegendColor.toArgb()
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawValues(showValueBar)
                valueTextColor = colorScheme.onSurface.toArgb()
            }

            val lineDataSet = LineDataSet(lineEntries, lineLegendLabel).apply {
                color = lineLegendColor.toArgb()
                lineWidth = 2f
                setDrawCircles(showValueLine)
                setDrawValues(showValueLine)
                valueTextColor = colorScheme.onSurface.toArgb()
                axisDependency = YAxis.AxisDependency.RIGHT

                if(axisMaximumLine==1f) {
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${(value * 100).toInt()}%"
                        }
                    }
                }
            }

            val combinedData = CombinedData().apply {
                setData(BarData(barDataSet))
                setData(LineData(lineDataSet))
            }

            chart.data = combinedData

            // Format X labels
            val labelsWithoutYear = labels.map { label ->
                label.split("-").drop(1).joinToString("/")
            }

            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(labelsWithoutYear)
                textColor = secondaryTextColor.toArgb()
                textSize = 10f
            }

            // Axe gauche
            chart.axisLeft.apply {
                axisMinimum = 0f
                textColor = secondaryTextColor.toArgb()
            }

            // Axe droite
            chart.axisRight.apply {
                axisMinimum = 0f
                axisMaximum = axisMaximumLine
                setDrawGridLines(false)
                textColor = secondaryTextColor.toArgb()
            }

            chart.invalidate()
        }
    )
}
