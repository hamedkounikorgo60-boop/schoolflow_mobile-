package com.schoolflow.app.ui.paiements  
  
import androidx.compose.foundation.layout.*  
import androidx.compose.foundation.lazy.LazyColumn  
import androidx.compose.foundation.lazy.items  
import androidx.compose.foundation.selection.selectable  
import androidx.compose.material.icons.Icons  
import androidx.compose.material.icons.filled.Add  
import androidx.compose.material.icons.filled.ArrowBack  
import androidx.compose.material.icons.filled.CheckCircle  
import androidx.compose.material3.*  
import androidx.compose.runtime.*  
import androidx.compose.ui.Alignment  
import androidx.compose.ui.Modifier  
import androidx.compose.ui.text.font.FontWeight  
import androidx.compose.ui.unit.dp  
import androidx.compose.ui.unit.sp  
import androidx.lifecycle.viewmodel.compose.viewModel  
import com.schoolflow.app.data.local.TokenManager  
import com.schoolflow.app.data.model.PaiementDto  
import java.text.NumberFormat  
import java.util.Locale  
  
@OptIn(ExperimentalMaterial3Api::class)  
@Composable  
fun PaiementsScreen(  
    tokenManager: TokenManager,  
    onBack: () -> Unit  
) {  
    val viewModel: PaiementsViewModel = viewModel(  
        factory = PaiementsViewModelFactory(tokenManager)  
    )  
    val state = viewModel.uiState  
    var showDialog by remember { mutableStateOf(false) }  
  
    LaunchedEffect(state.submitSuccess) {  
        if (state.submitSuccess) {  
            showDialog = false  
            viewModel.clearSubmitState()  
        }  
    }  
  
    Scaffold(  
        topBar = {  
            TopAppBar(  
                title = { Text("Paiements") },  
                navigationIcon = {  
                    IconButton(onClick = onBack) {  
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")  
                    }  
                }  
            )  
        },  
        floatingActionButton = {  
            ExtendedFloatingActionButton(  
                onClick = { showDialog = true },  
                icon = { Icon(Icons.Default.Add, contentDescription = null) },  
                text = { Text("Payer") }  
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
                        Button(onClick = { viewModel.loadPaiements() }) {  
                            Text("Réessayer")  
                        }  
                    }  
                }  
            }  
  
            state.data != null -> {  
                val data = state.data  
  
                if (data.paiements.isEmpty()) {  
                    Box(  
                        modifier = Modifier.fillMaxSize().padding(padding),  
                        contentAlignment = Alignment.Center  
                    ) {  
                        Text(  
                            "Aucun paiement enregistré.",  
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
                            Card(  
                                modifier = Modifier.fillMaxWidth(),  
                                colors = CardDefaults.cardColors(  
                                    containerColor = MaterialTheme.colorScheme.primaryContainer  
                                )  
                            ) {  
                                Column(modifier = Modifier.padding(16.dp)) {  
                                    Text(  
                                        "Total payé",  
                                        fontSize = 13.sp,  
                                        color = MaterialTheme.colorScheme.onSurfaceVariant  
                                    )  
                                    Text(  
                                        formatMontant(data.total_paye),  
                                        fontWeight = FontWeight.Bold,  
                                        fontSize = 24.sp  
                                    )  
                                }  
                            }  
                        }  
  
                        items(data.paiements) { paiement ->  
                            PaiementRow(paiement)  
                        }  
                    }  
                }  
            }  
        }  
    }  
  
    if (showDialog) {  
        PaiementDialog(  
            isSubmitting = state.isSubmitting,  
            errorMessage = state.submitError,  
            onDismiss = {  
                showDialog = false  
                viewModel.clearSubmitState()  
            },  
            onConfirm = { montant, type, mode, numero ->  
                viewModel.createPaiement(montant, type, mode, numero)  
            }  
        )  
    }  
}  
  
@OptIn(ExperimentalMaterial3Api::class)  
@Composable  
fun PaiementDialog(  
    isSubmitting: Boolean,  
    errorMessage: String?,  
    onDismiss: () -> Unit,  
    onConfirm: (Double, String, String, String) -> Unit  
) {  
    var montant by remember { mutableStateOf("") }  
    var type by remember { mutableStateOf("scolarite") }  
    var mode by remember { mutableStateOf("mobile_money") }  
    var numero by remember { mutableStateOf("") }  
    var typeExpanded by remember { mutableStateOf(false) }  
  
    val types = listOf(  
        "scolarite" to "Scolarité",  
        "inscription" to "Inscription",  
        "cantine" to "Cantine",  
        "transport" to "Transport",  
        "fournitures" to "Fournitures",  
        "autre" to "Autre"  
    )  
  
    val modes = listOf(  
        "mobile_money" to "Mobile Money",  
        "virement" to "Virement bancaire"  
    )  
  
    val numeroRequis = mode == "mobile_money" || mode == "virement"  
  
    AlertDialog(  
        onDismissRequest = onDismiss,  
        title = { Text("Nouveau paiement") },  
        text = {  
            Column {  
                OutlinedTextField(  
                    value = montant,  
                    onValueChange = { montant = it.filter { c -> c.isDigit() } },  
                    label = { Text("Montant (F)") },  
                    singleLine = true,  
                    modifier = Modifier.fillMaxWidth()  
                )  
                Spacer(modifier = Modifier.height(12.dp))  
                ExposedDropdownMenuBox(  
                    expanded = typeExpanded,  
                    onExpandedChange = { typeExpanded = !typeExpanded }  
                ) {  
                    OutlinedTextField(  
                        value = types.firstOrNull { it.first == type }?.second ?: type,  
                        onValueChange = {},  
                        readOnly = true,  
                        label = { Text("Type de paiement") },  
                        trailingIcon = {  
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)  
                        },  
                        modifier = Modifier.fillMaxWidth().menuAnchor()  
                    )  
                    ExposedDropdownMenu(  
                        expanded = typeExpanded,  
                        onDismissRequest = { typeExpanded = false }  
                    ) {  
                        types.forEach { (value, label) ->  
                            DropdownMenuItem(  
                                text = { Text(label) },  
                                onClick = {  
                                    type = value  
                                    typeExpanded = false  
                                }  
                            )  
                        }  
                    }  
                }  
                Spacer(modifier = Modifier.height(12.dp))  
                Text("Mode de paiement", fontWeight = FontWeight.Medium, fontSize = 14.sp)  
                modes.forEach { (value, label) ->  
                    Row(  
                        modifier = Modifier  
                            .fillMaxWidth()  
                            .selectable(  
                                selected = (mode == value),  
                                onClick = { mode = value }  
                            )  
                            .padding(vertical = 4.dp),  
                        verticalAlignment = Alignment.CenterVertically  
                    ) {  
                        RadioButton(  
                            selected = (mode == value),  
                            onClick = { mode = value }  
                        )  
                        Spacer(modifier = Modifier.width(8.dp))  
                        Text(label)  
                    }  
                }  
                if (numeroRequis) {  
                    Spacer(modifier = Modifier.height(12.dp))  
                    OutlinedTextField(  
                        value = numero,  
                        onValueChange = { numero = it.filter { c -> c.isDigit() } },  
                        label = {  
                            Text(  
                                if (mode == "mobile_money") "Numéro Mobile Money"  
                                else "Numéro de compte / virement"  
                            )  
                        },  
                        singleLine = true,  
                        modifier = Modifier.fillMaxWidth()  
                    )  
                }  
                if (errorMessage != null) {  
                    Spacer(modifier = Modifier.height(8.dp))  
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)  
                }  
            }  
        },  
        confirmButton = {  
            Button(  
                onClick = {  
                    val m = montant.toDoubleOrNull()  
                    if (m != null && m > 0 && type.isNotBlank() && (!numeroRequis || numero.isNotBlank())) {  
                        onConfirm(m, type.trim(), mode, numero.trim())  
                    }  
                },  
                enabled = !isSubmitting &&  
                    montant.toDoubleOrNull() != null &&  
                    (!numeroRequis || numero.isNotBlank())  
            ) {  
                if (isSubmitting) {  
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)  
                } else {  
                    Text("Payer")  
                }  
            }  
        },  
        dismissButton = {  
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {  
                Text("Annuler")  
            }  
        }  
    )  
}  
  
@Composable  
fun PaiementRow(paiement: PaiementDto) {  
    Card(modifier = Modifier.fillMaxWidth()) {  
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {  
            Row(  
                modifier = Modifier.fillMaxWidth(),  
                horizontalArrangement = Arrangement.SpaceBetween,  
                verticalAlignment = Alignment.CenterVertically  
            ) {  
                Row(verticalAlignment = Alignment.CenterVertically) {  
                    Icon(  
                        Icons.Default.CheckCircle,  
                        contentDescription = null,  
                        tint = MaterialTheme.colorScheme.primary,  
                        modifier = Modifier.size(18.dp)  
                    )  
                    Spacer(modifier = Modifier.width(8.dp))  
                    Text(  
                        paiement.type.replaceFirstChar { it.uppercase() },  
                        fontWeight = FontWeight.Medium,  
                        fontSize = 15.sp  
                    )  
                }  
                Text(  
                    formatMontant(paiement.montant),  
                    fontWeight = FontWeight.Bold,  
                    fontSize = 15.sp,  
                    color = MaterialTheme.colorScheme.primary  
                )  
            }  
            Spacer(modifier = Modifier.height(6.dp))  
            paiement.mode?.let {  
                Text(  
                    "Mode: ${if (it == "mobile_money") "Mobile Money" else if (it == "virement") "Virement bancaire" else it}",  
                    fontSize = 11.sp,  
                    color = MaterialTheme.colorScheme.onSurfaceVariant  
                )  
                Spacer(modifier = Modifier.height(2.dp))  
            }  
            Row(  
                modifier = Modifier.fillMaxWidth(),  
                horizontalArrangement = Arrangement.SpaceBetween  
            ) {  
                Text(  
                    "Reçu: ${paiement.recu_numero}",  
                    fontSize = 11.sp,  
                    color = MaterialTheme.colorScheme.onSurfaceVariant  
                )  
                Text(  
                    paiement.date,  
                    fontSize = 11.sp,  
                    color = MaterialTheme.colorScheme.onSurfaceVariant  
                )  
            }  
        }  
    }  
}  
  
fun formatMontant(montant: Double): String {  
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)  
    return "${formatter.format(montant)} F"  
}  
