package com.schoolflow.app.ui.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolflow.app.data.local.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    tokenManager: TokenManager,
    onBack: () -> Unit = {}
) {
    val viewModel: NotificationsViewModel = viewModel(
        factory = NotificationsViewModelFactory(tokenManager)
    )
    val state = viewModel.uiState

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.errorMessage != null -> Text(
                    state.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center)
                )
                /*state.notifications.isEmpty() -> Text(
                    "Aucune notification",
                    modifier = Modifier.align(Alignment.Center)
                )  */
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(state.notifications) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable {
                                    if (!notif.lue) viewModel.markRead(notif.id)
                                }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    notif.titre,
                                    fontWeight = if (notif.lue) FontWeight.Normal else FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(notif.message)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    notif.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
