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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private class LegacyCrmViewModel : ViewModel()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CreditCardCrmApp() }
    }
}

@Composable
private fun CreditCardCrmApp(vm: CrmViewModel = viewModel(factory = CrmViewModelFactory(LocalContext.current))) {
    var screen by remember { mutableStateOf("dashboard") }
    var selectedCustomer by remember { mutableStateOf<CustomerRecord?>(null) }
    var showAdd by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    MaterialTheme {
        when (screen) {
            "customers" -> {
                CustomerListScreen(
                    customers = vm.customers,
                    query = query,
                    onQueryChange = { query = it },
                    onBack = { screen = "dashboard" },
                    onOpenCustomer = {
                        selectedCustomer = it
                        screen = "detail"
                    },
                    onOpenFollowUps = { screen = "followups" }
                )
            }

            "detail" -> {
                val customer = selectedCustomer
                if (customer == null) {
                    screen = "customers"
                } else {
                    CustomerDetailScreen(
                        customer = customer,
                        onBack = { screen = "customers" },
                        onOpenFollowUps = { screen = "followups" }
                    )
                }
            }

            "followups" -> {
                FollowUpScreen(
                    followUps = SampleData.followUps,
                    onBack = {
                        screen = if (selectedCustomer == null) "dashboard" else "detail"
                    },
                    onOpenCustomer = { name ->
                        vm.customers.firstOrNull { it.name == name }?.let {
                            selectedCustomer = it
                            screen = "detail"
                        }
                    }
                )
            }

            else -> {
                DashboardScreen(
                    customers = vm.customers,
                    onCustomers = { screen = "customers" },
                    onFollowUps = {
                        selectedCustomer = null
                        screen = "followups"
                    },
                    onAddCustomer = { showAdd = true },
                    onOpenCustomer = {
                        selectedCustomer = it
                        screen = "detail"
                    }
                )
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
private fun DashboardScreen(
    customers: List<CustomerRecord>,
    onCustomers: () -> Unit,
    onFollowUps: () -> Unit,
    onAddCustomer: () -> Unit,
    onOpenCustomer: (CustomerRecord) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Credit Card CRM") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCustomer) { Text("+") }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text("Today's Work", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Overdue", "2")
                StatCard("Follow-ups", "5")
                StatCard("Pending", "3")
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCustomers) { Text("Customers") }
                OutlinedButton(onClick = onFollowUps) { Text("Follow-ups") }
            }

            Spacer(Modifier.height(12.dp))
            Text("DO NOW", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(customers.take(5)) { item ->
                    CustomerCard(item, onOpenCustomer)
                }
            }
        }
    }
}

@Composable
private fun CustomerListScreen(
    customers: List<CustomerRecord>,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onOpenCustomer: (CustomerRecord) -> Unit,
    onOpenFollowUps: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customers") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack) { Text("Dashboard") }
                OutlinedButton(onClick = onOpenFollowUps) { Text("Follow-ups") }
            }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search name, status or inquiry") }
            )
            Spacer(Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(
                    customers.filter {
                        it.name.contains(query, true) ||
                            it.status.contains(query, true) ||
                            it.inquiryType.contains(query, true)
                    }
                ) { item ->
                    CustomerCard(item, onOpenCustomer)
                }
            }
        }
    }
}

@Composable
private fun CustomerCard(
    item: CustomerRecord,
    onOpenCustomer: (CustomerRecord) -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(item.inquiryType)
            Text(item.priority + ": " + item.status)
            Text("Next: " + item.nextAction)
            if (item.pendingReason.isNotBlank()) {
                Text("Pending: " + item.pendingReason)
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}) { Text("Call") }
                OutlinedButton(onClick = { onOpenCustomer(item) }) { Text("View") }
            }
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
