package de.alex.lightweights.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun StrengthChart(
    values: List<Float>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                description.isEnabled = false
                legend.isEnabled = false

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.LTGRAY
                    setDrawGridLines(false)
                }

                axisLeft.apply {
                    textColor = android.graphics.Color.LTGRAY
                    gridColor = android.graphics.Color.DKGRAY
                }

                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = values.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }

            val dataSet = LineDataSet(entries, "Strength").apply {
                color = android.graphics.Color.CYAN
                lineWidth = 3f
                setDrawCircles(true)
                circleRadius = 4f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setCircleColor(android.graphics.Color.CYAN)
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}
