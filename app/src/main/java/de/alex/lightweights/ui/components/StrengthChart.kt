package de.alex.lightweights.ui.components

import android.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
    // Farben aus dem Compose MaterialTheme für Light/Dark-Mode-Unterstützung holen
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val weightLineColor = MaterialTheme.colorScheme.primary.toArgb()
    val strengthLineColor = MaterialTheme.colorScheme.tertiary.toArgb()
    val circleColor = MaterialTheme.colorScheme.secondary.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                configureChartStyle()
                // Der Formatter wird jetzt von der Logik im 'update'-Block gesteuert.
                configureAxes(textColor, gridColor, null) // Startet ohne Datum
            }
        },
        update = { chart ->
            if (chartData.isEmpty()) {
                chart.clear() // Leert das Diagramm, wenn keine Daten da sind
                chart.invalidate()
                return@AndroidView
            }

            // NEUE LOGIK:
            // 1. Finde das Startdatum für unsere Zeitachse (das erste Datum in den Daten)
            val firstDate = chartData.first().date

            // 3. Passe den X-Achsen-Formatter an das Startdatum an
            (chart.xAxis.valueFormatter as? TimeAxisValueFormatter)?.startDate = firstDate

            val movedWeightEntries = chartData.map { entry ->
                val daysSinceFirst = ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
                Entry(daysSinceFirst, entry.movedWeight)
            }

            val strengthEntries = chartData.map { entry ->
                val daysSinceFirst = ChronoUnit.DAYS.between(firstDate, entry.date).toFloat()
                Entry(daysSinceFirst, entry.bestStrength.toFloat())
            }

            val weightDataSet = LineDataSet(movedWeightEntries, "Bewegtes Gewicht").apply {
                axisDependency = YAxis.AxisDependency.LEFT
                configureDataSetStyle(weightLineColor, circleColor)
            }

            val strengthDataSet = LineDataSet(strengthEntries, "Stärke").apply {
                axisDependency = YAxis.AxisDependency.RIGHT
                configureDataSetStyle(strengthLineColor, circleColor)
            }


            chart.data = LineData(weightDataSet, strengthDataSet)
            chart.invalidate()
        }
    )
}

/**
 * Konfiguriert das allgemeine Aussehen des Charts (Hintergrund, Legende etc.).
 */
private fun LineChart.configureChartStyle() {
    setBackgroundColor(Color.TRANSPARENT)
    description.isEnabled = false
    legend.isEnabled = false
    setNoDataText("Keine Daten für eine Grafik verfügbar.")
    isDragEnabled = true
    setScaleEnabled(true)
    setPinchZoom(true)
}

/**
 * Konfiguriert die X- und Y-Achsen des Charts.
 */
private fun LineChart.configureAxes(
    textColor: Int,
    gridColor: Int,
    initialStartDate: LocalDate? // Parameter geändert
) {
    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        this.textColor = textColor
        setDrawGridLines(false)
        setDrawAxisLine(true)
        granularity = 1f

        // NEUER Formatter, der mit Tagen rechnet
        valueFormatter = TimeAxisValueFormatter(initialStartDate)
    }

    axisLeft.apply {
        this.textColor = textColor
        this.gridColor = gridColor
        setDrawAxisLine(false)
        axisMinimum = 0f

        // HIER DIE ÄNDERUNG EINFÜGEN:
        // Diese Zeile sorgt dafür, dass die Werte (Labels) auf der Y-Achse gezeichnet werden.
        setDrawLabels(true)
    }

    // Die rechte Y-Achse wird nicht benötigt
    axisRight.isEnabled = false
}

private class TimeAxisValueFormatter(var startDate: LocalDate?) : ValueFormatter() {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.")
    override fun getFormattedValue(value: Float): String {
        // value ist hier die Anzahl der Tage (z.B. 0.0, 7.0, 21.0)
        // Wenn kein Startdatum da ist, zeige nichts an.
        return startDate?.plusDays(value.toLong())?.format(formatter) ?: ""
    }
}

/**
 * Konfiguriert das Aussehen der Linie und der Datenpunkte im Graphen.
 */
private fun LineDataSet.configureDataSetStyle(lineColor: Int, circleColor: Int) {
    this.color = lineColor
    this.lineWidth = 2.5f
    // Zeichne die Werte (z.B. "105.5") nicht direkt in den Graphen
    setDrawValues(false)

    // Style für die Kreise an den Datenpunkten
    setDrawCircles(true)
    this.circleRadius = 4f
    this.circleHoleRadius = 2f
    this.setCircleColor(circleColor)
}
