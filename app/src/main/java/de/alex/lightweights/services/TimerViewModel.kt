package de.alex.lightweights.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TimerViewModel(
    application: Application
): AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    private var timerJob: Job? = null
    private var workerId: UUID? = null
    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    fun startPause(durationInSeconds: Int) {
        // 2. Bestehenden Timer (UI-Job und Worker) immer zuerst stoppen
        resetTimer()

        _remainingSeconds.value = durationInSeconds
        _isRunning.value = true

        // Dieser Job ist nur für die sekundengenaue Aktualisierung der UI,
        // während die App im Vordergrund ist.
        timerJob = viewModelScope.launch {
            for (i in durationInSeconds downTo 1) {
                _remainingSeconds.value = i
                delay(1_000)
            }
            // Wenn der UI-Timer abläuft, setze den Status zurück.
            // Die Benachrichtigung kommt vom Worker.
            _isRunning.value = false
            _remainingSeconds.value = 0
        }

        // 3. Den TimerWorker für die Hintergrundausführung starten
        val data = Data.Builder()
            .putInt(TimerWorker.DURATION_KEY, durationInSeconds)
            .build()

        val timerRequest = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInputData(data)
            .build()

        // Speichere die ID, um den Worker bei Bedarf abbrechen zu können
        workerId = timerRequest.id

        // Starte den Worker. ExistingWorkPolicy.REPLACE stellt sicher,
        // dass immer nur EIN "pause_timer" läuft.
        workManager.enqueueUniqueWork("pause_timer",
            ExistingWorkPolicy.REPLACE, timerRequest)
    }

    fun stopTimer() {
        timerJob?.cancel()
        // 4. Auch den Hintergrund-Worker abbrechen
        workerId?.let {
            workManager.cancelWorkById(it)
            workerId = null
        }
        _isRunning.value = false
    }

    fun resetTimer() {
        stopTimer() // Stoppt UI-Job und Worker
        _remainingSeconds.value = 0
        // isRunning wird bereits in stopTimer() auf false gesetzt
    }
}