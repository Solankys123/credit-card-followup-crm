package com.solankys123.creditcardcrm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FollowUpScreen(
    followUps: List<FollowUp> = emptyList(),
    customers: List<CustomerRecord> = emptyList(),
    onBack: () -> Unit = {},
    onOpenCustomer: (String) -> Unit = {},
    onAddFollowUp: (FollowUp) -> Unit = {},
    onCompleteFollowUp: (FollowUp) -> Unit = {},
    onDeleteFollowUp: (FollowUp) -> Unit = {}
) {
    var filter by remember { mutableStateOf("TODAY") }
    var showAdd by remember { mutableStateOf(false) }

    val visible = when (filter) {
        "TODAY" -> followUps.filter { it.dueAt.contains("Today", true) && !it.completed }
        "OVERDUE" -> followUps.filter { it.dueAt.contains("Overdue", true) && !it.completed }
        "UPCOMING" -> followUps.filter { (it.dueAt.contains("Tomorrow", true) || it.dueAt.contains("Upcoming", true)) && !it.completed }
        "DONE" -> followUps.filter { it.completed }
        else -> followUps
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Follow-ups") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = { TextButton(onClick = { showAdd = true }) { Text("+ Add") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("TODAY" to "Today", "OVERDUE" to "Overdue", "UPCOMING" to "Upcoming", "DONE" to "Done").forEach { (key, label) ->
                    FilterChip(selected = filter == key, onClick = { filter = key }, label = { Text(label) })
                }
            }

            Spacer(Modifier.height(12.dp))

            if (visible.isEmpty()) {
                Text("No follow-ups in this view.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(visible, key = { if (it.id != 0L) it.id else it.hashCode().toLong() }) { followUp ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(14.dp)) {
                                Text(followUp.customerName, style = MaterialTheme.typography.titleMedium)
                                Text(followUp.reason)
                                Text("Due: " + followUp.dueAt)
                                Text("Priority: " + followUp.priority)
                                if (followUp.completed) Text("Status: COMPLETED")
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { onOpenCustomer(followUp.customerName) }) { Text("Customer") }
                                    if (!followUp.completed) {
                                        Button(onClick = { onCompleteFollowUp(followUp) }) { Text("Done") }
                                    } else {
                                        OutlinedButton(onClick = { onDeleteFollowUp(followUp) }) { Text("Delete") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        FollowUpEditorDialog(
            customers = customers,
            onDismiss = { showAdd = false },
            onSave = {
                onAddFollowUp(it)
                showAdd = false
            }
        )
    }
}

@Composable
private fun FollowUpEditorDialog(
    customers: List<CustomerRecord>,
    onDismiss: () -> Unit,
    onSave: (FollowUp) -> Unit
) {
    var customer by remember { mutableStateOf(customers.firstOrNull()?.name ?: "") }
    var reason by remember { mutableStateOf("") }
    var dueAt by remember { mutableStateOf("Today, 4:00 PM") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var error by remember { mutableStateOf("") }
    val dueFormatOk = dueAt.matches(Regex("^(Today|Tomorrow|Upcoming|Overdue), [0-2]?[0-9]:[0-5][0-9] ?(AM|PM)$"))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Follow-up") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (customers.isEmpty()) {
                    Text("Add a customer first.")
                } else {
                    OutlinedTextField(customer, { customer = it }, label = { Text("Customer") }, singleLine = true)
                }
                OutlinedTextField(reason, { reason = it }, label = { Text("Reason *") }, singleLine = true)
                OutlinedTextField(dueAt, { dueAt = it }, label = { Text("Due (e.g. Today, 4:00 PM) *") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("HIGH", "MEDIUM", "LOW").forEach { value ->
                        FilterChip(selected = priority == value, onClick = { priority = value }, label = { Text(value) })
                    }
                }
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                when {
                    customers.isEmpty() -> error = "Add a customer first"
                    customer.isBlank() -> error = "Customer is required"
                    reason.isBlank() -> error = "Reason is required"
                    dueAt.isBlank() -> error = "Due time is required"
                    !dueFormatOk -> error = "Use format: Today, 4:00 PM"
                    else -> onSave(FollowUp(0, customer.trim(), reason.trim(), dueAt.trim(), priority))
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
