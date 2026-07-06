package com.example.mvvmclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mvvmclean.presentation.screens.NoteDetailScreen
import com.example.mvvmclean.presentation.screens.NoteListScreen
import com.example.mvvmclean.presentation.state.NoteListUiState
import com.example.mvvmclean.presentation.state.UiEvent
import com.example.mvvmclean.presentation.viewmodel.NoteListViewModel
import com.example.mvvmclean.ui.theme.AndroidarchitecturetemplatesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NoteListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate().
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hold the splash until the first real state replaces Loading so the
        // list doesn't flash a spinner on cold start.
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value is NoteListUiState.Loading
        }

        enableEdgeToEdge()
        setContent {
            AndroidarchitecturetemplatesTheme {
                NotesApp(viewModel)
            }
        }
    }
}

@Composable
fun NotesApp(viewModel: NoteListViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    NavHost(navController = navController, startDestination = "notes") {
        composable("notes") {
            NoteListScreen(
                state = state,
                snackbarHostState = snackbarHostState,
                onAction = viewModel::onAction,
                onNoteClick = { id -> navController.navigate("notes/$id") },
                onCreateClick = { navController.navigate("notes/new") },
            )
        }
        composable("notes/new") {
            NoteDetailScreen(
                note = null,
                snackbarHostState = snackbarHostState,
                onAction = viewModel::onAction,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "notes/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")
            val note = (state as? NoteListUiState.Success)
                ?.notes
                ?.firstOrNull { it.id == noteId }
            NoteDetailScreen(
                note = note,
                snackbarHostState = snackbarHostState,
                onAction = viewModel::onAction,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
