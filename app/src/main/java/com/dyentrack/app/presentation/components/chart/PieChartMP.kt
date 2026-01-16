package com.dyentrack.app.presentation.components.chart

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.dyentrack.app.presentation.theme.secondaryTextColor
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun PieChartMP(
    modifier: Modifier = Modifier,
    labels: List<String>,
    values: List<Float>,
    colors: List<Color>,
    showValues: Boolean = true,
    centerText: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                description.isEnabled = false

                legend.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 60f
                transparentCircleRadius = 65f
                setHoleColor(Color.Transparent.toArgb())
                setTransparentCircleColor(Color.Transparent.toArgb())

                setTouchEnabled(false)
                isHighlightPerTapEnabled = false

                setEntryLabelColor(secondaryTextColor.toArgb())
                setEntryLabelTextSize(12f)

                legend.textColor = secondaryTextColor.toArgb()
            }
        },
        update = { chart ->

            val entries = values.mapIndexed { index, value ->
                PieEntry(value, labels.getOrElse(index) { "" })
            }

            val dataSet = PieDataSet(entries, "").apply {
                setColors(colors.map { it.toArgb() })
                setDrawValues(showValues)
                valueTextColor = colorScheme.onSurface.toArgb()
                valueTextSize = 12f

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}%"
                    }
                }
            }

            chart.data = PieData(dataSet)

            centerText?.let {
                chart.centerText = it
                chart.setCenterTextSize(14f)
            }

            chart.invalidate()
        }
    )
}

