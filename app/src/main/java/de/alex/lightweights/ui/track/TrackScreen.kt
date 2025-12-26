import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    onExerciseClick: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val exercises = listOf(
        "Banckdrücken",
        "Rudern",
        "Seitheben",
        "Kniebeugen",
        "Beinbeuger",
        "Beinstrecker",
        "Schulterdrücken",
        "Schrägbankdrücken",
        "Bizepcurls",
        "Trizepcurls",
        "Dips",
        "Latzug",
        "Butterfly",
        "Planks",
        "Ausfallschritt",
        "Klimmzüge",
        "Schulterpresse",
        "Kabelzug",
        "Cruches"
    )

    val filteredExercises by remember(searchText) {
        derivedStateOf {
            if (searchText.isBlank()) {
                exercises
            } else {
                exercises.filter { it.contains(searchText, ignoreCase = true) }
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Track") }) }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredExercises) { exercise ->
                    ExerciseItem(
                        name = exercise,
                        onClick = {
                            onExerciseClick(exercise)
                            Log.d("TrackScreen", "Clicked: $exercise")
                        }
                    )
                }
            }
        }
    }
}
