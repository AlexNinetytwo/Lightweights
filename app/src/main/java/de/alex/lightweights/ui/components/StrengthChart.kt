package de.alex.lightweights.ui.components

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StrengthChart(
    chartData: List<Pair<LocalDate, Float>>,
    modifier: Modifier = Modifier
) {
    // Farben aus dem Compose MaterialTheme für Light/Dark-Mode-Unterstützung holen
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val lineColor = MaterialTheme.colorScheme.primary.toArgb()
    val circleColor = MaterialTheme.colorScheme.secondary.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                // Konfiguriere das grundlegende Chart-Layout einmalig
                configureChartStyle()
                // Konfiguriere die Achsen mit einem anfänglichen Formatter
                configureAxes(textColor, gridColor, chartData.map { it.first })
            }
        },
        update = { chart ->
            // Dieser Block wird bei jeder Neuberechnung (Recomposition) ausgeführt.

            // 1. Erstelle die Datenpunkte (Entries) aus den übergebenen chartData
            val entries = chartData.mapIndexed { index, pair ->
                Entry(index.toFloat(), pair.second) // X-Wert ist der Index, Y-Wert ist die Stärke
            }

            // 2. Erstelle ein DataSet und style es
            val dataSet = LineDataSet(entries, "Strength").apply {
                configureDataSetStyle(lineColor, circleColor)
            }

            // 3. Aktualisiere die Chart-Daten
            chart.data = LineData(dataSet)

            // 4. Stelle sicher, dass der Achsen-Formatter die aktuellsten Daten verwendet
            (chart.xAxis.valueFormatter as? DateAxisValueFormatter)?.dates = chartData.map { it.first }

            // 5. Zeichne das Diagramm neu
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
    initialDates: List<LocalDate>
) {
    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        this.textColor = textColor
        setDrawGridLines(false)
        setDrawAxisLine(true)
        granularity = 1f // Verhindert doppelte Labels beim Zoomen

        // Verwende eine eigene Formatter-Klasse, um die Daten später aktualisieren zu können
        valueFormatter = DateAxisValueFormatter(initialDates)
    }

    axisLeft.apply {
        this.textColor = textColor
        this.gridColor = gridColor
        setDrawAxisLine(false)
        // Setzt den Startpunkt der Y-Achse auf 0
        axisMinimum = 0f
    }

    // Die rechte Y-Achse wird nicht benötigt
    axisRight.isEnabled = false
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

/**
 * Ein benutzerdefinierter Formatter, der einen numerischen Index (0, 1, 2...)
 * in ein formatiertes Datum (z.B. "23.09.") für die X-Achse umwandelt.
 */
private class DateAxisValueFormatter(var dates: List<LocalDate>) : ValueFormatter() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.")

    // ÄNDERUNG: Überschreibe die neue Methode
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        // Gib das formatierte Datum zurück, wenn der Index gültig ist
        return if (index >= 0 && index < dates.size) {
            dates[index].format(formatter)
        } else {
            "" // Ansonsten leerer String
        }
    }
}
