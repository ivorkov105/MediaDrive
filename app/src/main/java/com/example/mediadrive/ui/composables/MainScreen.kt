package com.example.mediadrive.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.mediadrive.ui.composables.audio.list.AudioListScreen
import com.example.mediadrive.ui.composables.audio.player.AudioPlayerScreen
import com.example.mediadrive.ui.composables.audio.player.AudioPlayerViewModel
import com.example.mediadrive.utils.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Photo,
        Screen.Video,
        Screen.AudioGraph
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painter = painterResource(screen.icon), contentDescription = null) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.AudioGraph.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Photo.route) { PhotoScreen() }
            composable(Screen.Video.route) { VideoScreen() }

            navigation(
                startDestination = Screen.AudioList.route,
                route = Screen.AudioGraph.route
            ) {
                composable(Screen.AudioList.route) {
                    AudioListScreen(
                        onAudioClick = { audioIndex ->
                            navController.navigate(Screen.AudioPlayer.createRoute(audioIndex))
                        }
                    )
                }

                composable(
                    route = Screen.AudioPlayer.route,
                    arguments = listOf(navArgument("audioIndex") { type = NavType.IntType })
                ) { backStackEntry ->
                    val audioIndex = backStackEntry.arguments?.getInt("audioIndex") ?: 0
                    val viewModel: AudioPlayerViewModel = hiltViewModel()

                    LaunchedEffect(audioIndex) {
                        viewModel.playAudio(audioIndex)
                    }

                    AudioPlayerScreen(viewModel = viewModel)
                }
            }
        }
    }
}