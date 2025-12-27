//package de.alex.lightweights.ui.track
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import de.alex.lightweights.data.TrainingEntryDao
//
//class ExerciseDetailViewModelFactory(
//    private val trainingEntryDao: TrainingEntryDao
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ExerciseDetailViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ExerciseDetailViewModel(trainingEntryDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
