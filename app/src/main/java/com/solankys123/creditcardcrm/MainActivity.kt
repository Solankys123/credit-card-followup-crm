package com.solankys123.creditcardcrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.FloatingActionButton
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private data class Customer(
    val name: String,
    val inquiry: String,
    val status: String,
    val nextAction: String,
    val priority: String,
    val note: String
)

private class CrmViewModel : ViewModel() {
    var customers by mutableStateOf(
        listOf(
            Customer("Rahul Sharma", "Long Inquiry", "Documents Pending", "Call today 4:00 PM", "HIGH", "Salary slip pending"),
            Customer("Amit Verma", "Short Inquiry", "Application Started", "Follow up today", "MEDIUM", "Customer asked for callback"),
            Customer("Neha Singh", "Long Inquiry", "Verification", "Check status", "MEDIUM", "YONO available")
        )
    )
        private set

    fun addCustomer(name: String, inquiry: String) {
        if (name.isBlank()) return
        val newCustomer = Customer(
            name.trim(),
            inquiry,
            "New Inquiry",
            "Contact customer",
            "MEDIUM",
            ""
        )
        customers = listOf(newCustomer) + customers
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CreditCardCrmApp() }
    }
}

@Composable
private fun CreditCardCrmApp(vm: CrmViewModel = viewModel()) {
    var showAdd by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Credit Card CRM") }) },
            floatingActionButton = {
                FloatingActionButton(onClick = { showAdd = true }) { Text("+") }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Today's Work", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Overdue", "2")
                    StatCard("Follow-ups", "5")
                    StatCard("Pending", "3")
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search customer") }
                )

                Spacer(Modifier.height(12.dp))
                Text("DO NOW", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(
                        vm.customers.filter {
                            it.name.contains(query, ignoreCase = true) ||
                            it.status.contains(query, ignoreCase = true) ||
                            it.inquiry.contains(query, ignoreCase = true)
                        }
                    ) { item ->
                        CustomerCard(item)
                    }
                }
            }
        }

        if (showAdd) {
            AddCustomerDialog(
                onDismiss = { showAdd = false },
                onSave = { name, inquiry ->
                    vm.addCustomer(name, inquiry)
                    showAdd = false
                }
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(Modifier.weight(1f)) {
        Column(Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(label)
        }
    }
}

@Composable
private fun CustomerCard(item: Customer) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(item.inquiry)
            Spacer(Modifier.height(4.dp))
            Text(item.priority + ": " + item.status)
            Text("Next: " + item.nextAction)

            if (item.note.isNotBlank()) {
                Text("Note: " + item.note)
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}) { Text("Call") }
                OutlinedButton(onClick = {}) { Text("Follow-up") }
                OutlinedButton(onClick = {}) { Text("View") }
            }
        }
    }
}

@Composable
private fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var inquiry by remember { mutableStateOf("Short Inquiry") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer name") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = inquiry == "Short Inquiry",
                        onClick = { inquiry = "Short Inquiry" },
                        label = { Text("Short") }
                    )
                    FilterChip(
                        selected = inquiry == "Long Inquiry",
                        onClick = { inquiry = "Long Inquiry" },
                        label = { Text("Long") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, inquiry) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
