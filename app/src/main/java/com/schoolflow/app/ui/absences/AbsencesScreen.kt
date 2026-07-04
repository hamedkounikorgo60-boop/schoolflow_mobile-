package com.schoolflow.app.ui.absences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolflow.app.data.local.TokenManager
import com.schoolflow.app.data.model.AbsenceDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsencesScreen(
    tokenManager: TokenManager,
    onBack: () -> Unit
) {
    val viewModel: AbsencesViewModel = viewModel(
        factory = AbsencesViewModelFactory(tokenManager)
    )
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Absences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAbsences() }) {
                            Text("Réessayer")
                        }
                    }
                }
            }

            state.data != null -> {
                val data = state.data

                if (data.absences.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucune absence enregistrée. 🎉",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AbsenceStat(
                                    modifier = Modifier.weight(1f),
                                    label = "Absences",
                                    value = data.nb_absences.toString(),
                                    color = MaterialTheme.colorScheme.error
                                )
                                AbsenceStat(
                                    modifier = Modifier.weight(1f),
                                    label = "Retards",
                                    value = data.nb_retards.toString(),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                AbsenceStat(
                                    modifier = Modifier.weight(1f),
                                    label = "Non justifiées",
                                    value = data.nb_non_justifiees.toString(),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        items(data.absences) { absence ->
                            AbsenceRow(absence)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AbsenceStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AbsenceRow(absence: AbsenceDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (absence.justifiee) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (absence.justifiee)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (absence.type == "retard") "Retard" else "Absence",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(absence.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (absence.motif != null) {
                        Text(
                            "Motif: ${absence.motif}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = if (absence.justifiee) "Justifiée" else "Non justifiée",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (absence.justifiee)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}
