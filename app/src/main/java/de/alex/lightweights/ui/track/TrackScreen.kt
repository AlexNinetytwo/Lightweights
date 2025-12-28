package de.alex.lightweights.ui.track
import ExerciseItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.alex.lightweights.domain.model.Exercise


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    onExerciseClick: (Exercise) -> Unit,
    onAddExerciseClick: () -> Unit,
    viewModel: TrackViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }

    val exercises by viewModel.exercises.collectAsState(emptyList())

    val filteredExercises by remember(searchText, exercises) {
        derivedStateOf {
            if (searchText.isBlank()) {
                exercises
            } else {
                exercises.filter {
                    it.name.contains(searchText, ignoreCase = true)
                }
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExerciseClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Übung hinzufügen"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Übung suchen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // Platz für FAB
            ) {
                items(filteredExercises) { exercise ->
                    ExerciseItem(
                        name = exercise.name,
                        onClick = { onExerciseClick(exercise) }
                    )
                }
            }
        }
    }
}
