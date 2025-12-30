package de.alex.lightweights.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.alex.lightweights.domain.TimeFilter
import de.alex.lightweights.domain.rememberFilteredChartDataForSummerizedExerciseStats
import de.alex.lightweights.ui.components.StrengthChart
import de.alex.lightweights.ui.track.ExerciseDetailViewModel
import de.alex.lightweights.ui.track.FilterButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen() {
    val viewModel: ExerciseDetailViewModel = viewModel()
    val entries by viewModel.entries.collectAsState(emptyList())
    var selectedFilter by remember { mutableStateOf(TimeFilter.ALL) }
    var maxReps by remember { mutableDoubleStateOf(12.0) }
    var cutoff by remember { mutableDoubleStateOf(0.18) }
    val filteredChartData by rememberFilteredChartDataForSummerizedExerciseStats(entries, selectedFilter, maxReps, cutoff)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress") }
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Text(
                        text = "Gesamtvolume",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FilterButtons(
                        selected = selectedFilter,
                        onFilterSelected = { newFilter -> selectedFilter = newFilter }
                    )

                    StrengthChart(
                        chartData = filteredChartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }
            }
        }
    }
}
