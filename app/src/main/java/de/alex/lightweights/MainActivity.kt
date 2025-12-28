package de.alex.lightweights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.alex.lightweights.ui.components.BottomBar
import de.alex.lightweights.ui.progress.ProgressScreen
import de.alex.lightweights.ui.theme.LightweightsTheme
import de.alex.lightweights.ui.track.AddExerciseScreen
import de.alex.lightweights.ui.track.EditExerciseDialog
import de.alex.lightweights.ui.track.ExerciseDetailScreen
import de.alex.lightweights.ui.track.TrackScreen
import de.alex.lightweights.ui.track.TrackViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            LightweightsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            MainScreen(navController)
                        }

                        composable(
                            route = NavRoute.ExerciseDetail.route,
                            arguments = listOf(
                                navArgument("exerciseId") { type = NavType.StringType },
                                navArgument("exerciseName") { type = NavType.StringType }
                            )
                        ) { entry ->
                            ExerciseDetailScreen(
                                exerciseId = entry.arguments!!.getString("exerciseId")!!,
                                exerciseName = entry.arguments!!.getString("exerciseName")!!,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(NavRoute.AddExercise.route) {
                            AddExerciseScreen(
                                onDone = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(NavRoute.AddExercise.route) {
                            AddExerciseScreen(
                                onDone = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    val scope = rememberCoroutineScope()
    val trackViewModel: TrackViewModel = viewModel()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        bottomBar = {
            BottomBar(
                selectedIndex = pagerState.currentPage,
                onItemSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { padding ->   // ⬅️ DAS fehlte bei dir

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // ⬅️ extrem wichtig
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                when (page) {
                    0 -> TrackScreen(
                        onExerciseClick = { exercise ->
                            navController.navigate(
                                NavRoute.ExerciseDetail.createRoute(
                                    id = exercise.id,
                                    name = exercise.name
                                )
                            )
                        },
                        onAddExerciseClick = {
                            navController.navigate(NavRoute.AddExercise.route)
                        },
                        viewModel = trackViewModel
                    )

                    1 -> ProgressScreen()
                }
            }
        }
    }
}
