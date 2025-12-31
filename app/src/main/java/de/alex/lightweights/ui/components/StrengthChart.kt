package de.alex.lightweights.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.github.mikephil.charting.formatter.ValueFormatter
import de.alex.lightweights.data.DailyExerciseStats
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun StrengthChart(
    chartData: List<DailyExerciseStats>,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val weightLineColor = MaterialTheme.colorScheme.primary.toArgb()
    val strengthLineColor = MaterialTheme.colorScheme.tertiary.toArgb()
    val circleColor = MaterialTheme.colorScheme.secondary.toArgb()

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Volumen (kg)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Stärke",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            factory = { context ->
                CombinedChart(context).apply {
                    configureCombinedChartStyle()
                    configureAxes(textColor, weightLineColor, strengthLineColor, gridColor, null) // Startet ohne Datum
                }
            },
            update = { chart ->
                if (chartData.isEmpty()) {
                    chart.clear()
                    chart.invalidate()
                    return@AndroidView
                }

                val firstDate = chartData.first().date

                (chart.xAxis.valueFormatter as? TimeAxisValueFormatter)?.startDate = firstDate

                val volumeEntries = chartData.map { entry ->
                    val daysSinceFirst = ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
                    BarEntry(daysSinceFirst, entry.movedWeight)
                }

                val strengthEntries = chartData.map { entry ->
                    val daysSinceFirst = ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
                    Entry(daysSinceFirst, entry.bestStrength.toFloat())
                }

                val volumeDataSet = BarDataSet(volumeEntries, "Bewegtes Gewicht").apply {
                    configureDataSetStyle(weightLineColor)
                }

                val strengthDataSet = LineDataSet(strengthEntries, "Stärke").apply {
                    configureDataSetStyle(strengthLineColor, circleColor)
                }

                // Calculate the bar width based on the number of entries
                val entryCount = volumeEntries.size
                val barWidth = when {
                    entryCount <= 3  -> 0.25f
                    entryCount <= 7  -> 0.35f
                    entryCount <= 14 -> 0.45f
                    else             -> 0.6f
                }
                val barData = BarData(volumeDataSet).apply {
                    this.barWidth = barWidth
                }

                val combinedData = CombinedData().apply {
                    setData(barData)
                    setData(LineData(strengthDataSet))
                }

                chart.marker = ChartMarkerView(
                    context = chart.context,
                    startDate = firstDate
                )

                chart.data = combinedData
                chart.invalidate()
            }
        )
    }
}

private fun CombinedChart.configureCombinedChartStyle() {
    setBackgroundColor(Color.TRANSPARENT)
    description.isEnabled = false
    legend.isEnabled = false
    setNoDataText("Keine Daten für eine Grafik verfügbar.")
    isDragEnabled = true
    setScaleEnabled(true)
    setPinchZoom(true)
}

private fun CombinedChart.configureAxes(
    xAxisTextColor: Int,
    weightTextColor: Int,
    strengthTextColor: Int,
    gridColor: Int,
    initialStartDate: LocalDate?
) {
    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        this.textColor = xAxisTextColor
        setDrawGridLines(false)
        setDrawAxisLine(true)
        granularity = 1f
        valueFormatter = TimeAxisValueFormatter(initialStartDate)
    }

    axisLeft.apply {
        this.textColor = weightTextColor
        this.gridColor = gridColor
        setDrawAxisLine(false)
        axisMinimum = 0f
        setDrawLabels(true)
    }

    axisRight.apply {
        this.isEnabled = true
        this.textColor = strengthTextColor
        setDrawGridLines(false)
        setDrawAxisLine(false)
        axisMinimum = 0f
    }
}

private class TimeAxisValueFormatter(var startDate: LocalDate?) : ValueFormatter() {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.")
    override fun getFormattedValue(value: Float): String {
        // value ist hier die Anzahl der Tage (z.B. 0.0, 7.0, 21.0)
        // Wenn kein Startdatum da ist, zeige nichts an.
        return startDate?.plusDays(value.toLong())?.format(formatter) ?: ""
    }
}

private fun LineDataSet.configureDataSetStyle(lineColor: Int, circleColor: Int) {
    this.color = lineColor
    this.lineWidth = 2.5f
    this.axisDependency = YAxis.AxisDependency.RIGHT
    setDrawValues(false)
    setDrawCircles(false)
    this.circleRadius = 4f
    this.circleHoleRadius = 2f
    this.setCircleColor(circleColor)
}

private fun BarDataSet.configureDataSetStyle(color: Int) {
    this.color = color
    this.axisDependency = YAxis.AxisDependency.LEFT
    this.valueTextColor = color
    setDrawValues(false)
}
