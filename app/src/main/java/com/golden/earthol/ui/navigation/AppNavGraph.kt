package com.golden.earthol.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.golden.earthol.data.GameRepository
import com.golden.earthol.ui.screen.archive.ArchiveScreen
import com.golden.earthol.ui.screen.archive.ArchiveViewModel
import com.golden.earthol.ui.screen.character.CharacterScreen
import com.golden.earthol.ui.screen.character.CharacterViewModel
import com.golden.earthol.ui.screen.guides.GuidesScreen
import com.golden.earthol.ui.screen.guides.GuidesViewModel
import com.golden.earthol.ui.screen.home.HomeScreen
import com.golden.earthol.ui.screen.home.HomeViewModel
import com.golden.earthol.ui.screen.relationships.RelationshipsScreen
import com.golden.earthol.ui.screen.relationships.RelationshipsViewModel
import com.golden.earthol.ui.screen.tasks.TasksScreen
import com.golden.earthol.ui.screen.tasks.TasksViewModel
import com.golden.earthol.ui.screen.world.WorldScreen
import com.golden.earthol.ui.screen.world.WorldViewModel

@Composable
fun AppNavGraph(repository: GameRepository) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavHost(
                navController,
                startDestination = "home",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                composable("home") { HomeScreen(viewModel(factory = HomeViewModel.Factory(repository))) }
                composable("tasks") { TasksScreen(viewModel(factory = TasksViewModel.Factory(repository))) }
                composable("character") { CharacterScreen(viewModel(factory = CharacterViewModel.Factory(repository))) }
                composable("world") { WorldScreen(viewModel(factory = WorldViewModel.Factory(repository))) }
                composable("guides") { GuidesScreen(viewModel(factory = GuidesViewModel.Factory(repository))) }
                composable("relationships") { RelationshipsScreen(viewModel(factory = RelationshipsViewModel.Factory(repository))) }
                composable("archive") { ArchiveScreen(viewModel(factory = ArchiveViewModel.Factory(repository))) }
            }
            BottomNavBar(backStack?.destination) { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}
