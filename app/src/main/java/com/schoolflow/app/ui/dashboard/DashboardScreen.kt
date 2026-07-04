package com.schoolflow.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.schoolflow.app.data.local.TokenManager
import com.schoolflow.app.data.model.NoteResumeDto
import com.schoolflow.app.ui.components.ErrorState
import com.schoolflow.app.ui.components.LoadingState
import com.schoolflow.app.ui.components.MetricCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    tokenManager: TokenManager,
    onLogout: () -> Unit,
    onNavigateToNotes: () -> Unit = {},
    onNavigateToPaiements: () -> Unit = {},
    onNavigateToAbsences: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},

) {
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(tokenManager)
    )
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tableau de bord") },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = onNavigateToChangePassword) {
                        Icon(Icons.Default.Settings, contentDescription = "Paramètres")
                    }
                    IconButton(onClick = { viewModel.loadDashboard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.logout()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Déconnexion")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingState(
                modifier = Modifier.padding(padding),
                message = "Chargement du tableau de bord..."
            )

            state.errorMessage != null -> ErrorState(
                message = state.errorMessage,
                modifier = Modifier.padding(padding),
                onRetry = { viewModel.loadDashboard() }
            )

            state.data != null -> {
                val data = state.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        StudentHeader(
                            name = "${data.eleve.nom} ${data.eleve.prenoms}",
                            subtitle = "${data.eleve.classe} · ${data.eleve.matricule}",
                            photo = data.eleve.photo
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Moyenne générale",
                                value = data.moyenne_generale?.let { "%.2f/20".format(it) } ?: "N/A",
                                icon = Icons.Default.MenuBook
                            )
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Rang",
                                value = "${data.rang}/${data.total_eleves}",
                                icon = Icons.Default.EmojiEvents
                            )
                        }
                    }

                    item {
                        SectionCard(title = "Informations de l'élève") {
                            InfoRow("Naissance", data.eleve.date_naissance)
                            InfoRow("Lieu", data.eleve.lieu_naissance)
                            InfoRow("Genre", data.eleve.genre)
                            InfoRow("Téléphone", data.eleve.telephone)
                            InfoRow("Adresse", data.eleve.adresse)
                            InfoRow("Email", data.eleve.email)
                        }
                    }

                    if (data.ecole != null) {
                        item {
                            SectionCard(title = "Établissement") {
                                InfoRow("Nom", data.ecole.nom)
                                InfoRow("Année scolaire", data.ecole.annee_scolaire)
                                InfoRow("Téléphone", data.ecole.telephone)
                                InfoRow("Email", data.ecole.email)
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Accès rapides",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Notes et moyennes",
                            subtitle = "Consulter les notes du trimestre",
                            icon = Icons.Default.MenuBook,
                            onClick = onNavigateToNotes
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Paiements",
                            subtitle = "Voir l'historique et déclarer un paiement",
                            icon = Icons.Default.Payments,
                            onClick = onNavigateToPaiements
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Absences et retards",
                            subtitle = "Suivre la présence de l'élève",
                            icon = Icons.Default.EventBusy,
                            onClick = onNavigateToAbsences
                        )
                    }

                    item {
                        Text(
                            text = "Dernières notes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    if (data.dernieres_notes.isEmpty()) {
                        item {
                            SectionCard(title = "Aucune note récente") {
                                Text(
                                    text = "Les dernières notes apparaîtront ici dès leur publication.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(data.dernieres_notes) { note ->
                            RecentNoteCard(note)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentHeader(name: String, subtitle: String, photo: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!photo.isNullOrBlank()) {
                AsyncImage(
                    model = photo,
                    contentDescription = "Photo de l'élève",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun RecentNoteCard(note: NoteResumeDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(note.matiere, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Coefficient ${note.coefficient}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${note.note}/20",
                fontWeight = FontWeight.Bold,
                color = when {
                    note.note >= 14 -> MaterialTheme.colorScheme.primary
                    note.note >= 10 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
