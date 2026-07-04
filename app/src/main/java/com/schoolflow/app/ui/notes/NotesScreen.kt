package com.schoolflow.app.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolflow.app.data.local.TokenManager
import com.schoolflow.app.data.model.NoteDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    tokenManager: TokenManager,
    onBack: () -> Unit
) {
    val viewModel: NotesViewModel = viewModel(
        factory = NotesViewModelFactory(tokenManager)
    )
    val state = viewModel.uiState

    val trimestres = listOf(
        "trimestre1" to "Trimestre 1",
        "trimestre2" to "Trimestre 2",
        "trimestre3" to "Trimestre 3"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Sélecteur de trimestre
            ScrollableTabRow(
                selectedTabIndex = trimestres.indexOfFirst { it.first == state.trimestre },
                edgePadding = 16.dp
            ) {
                trimestres.forEach { (key, label) ->
                    Tab(
                        selected = state.trimestre == key,
                        onClick = { viewModel.loadNotes(key) },
                        text = { Text(label) }
                    )
                }
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadNotes() }) {
                                Text("Réessayer")
                            }
                        }
                    }
                }

                state.data != null -> {
                    val notes = state.data.notes

                    if (notes.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Aucune note pour ce trimestre.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        val totalCoefs = notes.sumOf { it.coefficient }
                        val totalPoints = notes.sumOf { it.note * it.coefficient }
                        val moyenne = if (totalCoefs > 0) totalPoints / totalCoefs else 0.0

                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Moyenne générale", fontWeight = FontWeight.Medium)
                                        Text(
                                            "%.2f/20".format(moyenne),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }

                            items(notes) { note ->
                                NoteRow(note)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteRow(note: NoteDto) {
    val color = when {
        note.note >= 14 -> MaterialTheme.colorScheme.primary
        note.note >= 10 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(note.matiere, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Text(
                    "Coefficient ${note.coefficient}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${note.note}/20",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = color
            )
        }
    }
}
