package com.schoolflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.schoolflow.app.data.local.TokenManager
import com.schoolflow.app.ui.absences.AbsencesScreen
import com.schoolflow.app.ui.children.ChildrenScreen
import com.schoolflow.app.ui.dashboard.DashboardScreen
import com.schoolflow.app.ui.login.LoginScreen
import com.schoolflow.app.ui.notes.NotesScreen
import com.schoolflow.app.ui.notifications.NotificationsScreen
import com.schoolflow.app.ui.paiements.PaiementsScreen
import com.schoolflow.app.ui.password.ChangePasswordScreen
import com.schoolflow.app.ui.theme.SchoolFlowTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tokenManager = TokenManager(applicationContext)

        setContent {
            SchoolFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SchoolFlowApp(tokenManager)
                }
            }
        }
    }
}

private object Routes {
    const val LOGIN = "login"
    const val CHILDREN = "children"
    const val DASHBOARD = "dashboard"
    const val NOTES = "notes"
    const val PAIEMENTS = "paiements"
    const val ABSENCES = "absences"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"


}

private data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomDestinations = listOf(
    BottomDestination(Routes.CHILDREN, "Mes enfants", Icons.Default.People),
    BottomDestination(Routes.DASHBOARD, "Accueil", Icons.Default.Home),
    BottomDestination(Routes.NOTES, "Notes", Icons.Default.School),
    BottomDestination(Routes.PAIEMENTS, "Paiements", Icons.Default.Payment),
    BottomDestination(Routes.ABSENCES, "Absences", Icons.Default.EventBusy),
)

@Composable
fun SchoolFlowApp(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = currentRoute in bottomDestinations.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        tokenManager = tokenManager,
                        onLoginSuccess = {
                            navController.navigate(Routes.CHILDREN) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Routes.CHILDREN) {
                    ChildrenScreen(
                        tokenManager = tokenManager,
                        onSelectEnfant = {
                            navController.navigate(Routes.DASHBOARD) {
                                launchSingleTop = true
                            }
                        },
                        onLogout = {
                            scope.launch {
                                tokenManager.clear()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
                composable(Routes.DASHBOARD) {
                    DashboardScreen(
                        tokenManager = tokenManager,
                        onLogout = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                launchSingleTop = true
                            }
                        },

                        onNavigateToNotes = { navController.navigate(Routes.NOTES) },
                        onNavigateToPaiements = { navController.navigate(Routes.PAIEMENTS) },
                        onNavigateToAbsences = { navController.navigate(Routes.ABSENCES) },
                        onNavigateToChangePassword = { navController.navigate(Routes.SETTINGS) },
                        onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
                    )
                }
                composable(Routes.NOTES) {
                    NotesScreen(
                        tokenManager = tokenManager,
                        onBack = { navController.navigate(Routes.DASHBOARD) }
                    )
                }
                composable(Routes.PAIEMENTS) {
                    PaiementsScreen(
                        tokenManager = tokenManager,
                        onBack = { navController.navigate(Routes.DASHBOARD) }
                    )
                }
                composable(Routes.ABSENCES) {
                    AbsencesScreen(
                        tokenManager = tokenManager,
                        onBack = { navController.navigate(Routes.DASHBOARD) }
                    )

                }
                composable(Routes.NOTIFICATIONS) {
                    NotificationsScreen(
                        tokenManager = tokenManager,
                        onBack = { navController.navigate(Routes.DASHBOARD) }
                    )
                }
                composable(Routes.SETTINGS) {
                    ChangePasswordScreen(
                        tokenManager = tokenManager,
                        onBack = { navController.navigate(Routes.DASHBOARD) }
                    )
                }
            }
        }
    }
}
