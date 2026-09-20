package com.solankys123.creditcardcrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CreditCardCrmApp() }
    }
}

@Composable
private fun CreditCardCrmApp(
    vm: CrmViewModel = viewModel(factory = CrmViewModelFactory(LocalContext.current))
) {
    val customers by vm.customers.collectAsStateWithLifecycle()
    val followUps by vm.followUps.collectAsStateWithLifecycle()

    var screen by remember { mutableStateOf("dashboard") }
    var selectedCustomer by remember { mutableStateOf<CustomerRecord?>(null) }
    var showAdd by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // Keep detail screen synchronized with the DB after edits.
    LaunchedEffect(customers, selectedCustomer?.id) {
        selectedCustomer?.let { selected ->
            selectedCustomer = customers.firstOrNull { it.id == selected.id }
            if (selectedCustomer == null) screen = "customers"
        }
    }

    MaterialTheme {
        when (screen) {
            "customers" -> CustomerListScreen(
                customers = customers,
                query = query,
                onQueryChange = { query = it },
                onBack = { screen = "dashboard" },
                onOpenCustomer = { selectedCustomer = it; screen = "detail" },
                onOpenFollowUps = { screen = "followups" },
                onAddCustomer = { showAdd = true }
            )
            "detail" -> {
                val customer = selectedCustomer
                if (customer == null) screen = "customers"
                else CustomerDetailScreen(
                    customer = customer,
                    onBack = { screen = "customers" },
                    onEdit = { selectedCustomer = it; showAdd = true },
                    onDelete = {
                        vm.deleteCustomer(customer)
                        selectedCustomer = null
                        screen = "customers"
                    },
                    onOpenFollowUps = { screen = "followups" }
                )
            }
            "followups" -> FollowUpScreen(
                followUps = followUps,
                onBack = { screen = if (selectedCustomer == null) "dashboard" else "detail" },
                onOpenCustomer = { name ->
                    customers.firstOrNull { it.name == name }?.let {
                        selectedCustomer = it
                        screen = "detail"
                    }
                }
            )
            else -> DashboardScreen(
                customers = customers,
                onCustomers = { screen = "customers" },
                onFollowUps = { selectedCustomer = null; screen = "followups" },
                onAddCustomer = { showAdd = true },
                onOpenCustomer = { selectedCustomer = it; screen = "detail" }
            )
        }

        if (showAdd) {
            CustomerEditorDialog(
                initial = selectedCustomer,
                onDismiss = { showAdd = false },
                onSave = { customer ->
                    if (customer.id == 0L) vm.addCustomer(customer) else vm.updateCustomer(customer)
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
        floatingActionButton = { FloatingActionButton(onClick = onAddCustomer) { Text("+") } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Today's Work", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Customers", customers.size.toString())
                StatCard("High Priority", customers.count { it.priority.equals("HIGH", true) }.toString())
                StatCard("Pending", customers.count { it.status.contains("Pending", true) }.toString())
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
                items(customers.take(5)) { item -> CustomerCard(item, onOpenCustomer) }
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
    onOpenFollowUps: () -> Unit,
    onAddCustomer: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customers") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = { TextButton(onClick = onAddCustomer) { Text("+ Add") } }
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
                value = query, onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search name, status or inquiry") }
            )
            Spacer(Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(customers.filter {
                    it.name.contains(query, true) ||
                    it.status.contains(query, true) ||
                    it.inquiryType.contains(query, true)
                }) { item -> CustomerCard(item, onOpenCustomer) }
            }
        }
    }
}

@Composable
private fun CustomerCard(item: CustomerRecord, onOpenCustomer: (CustomerRecord) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(item.inquiryType)
            Text(item.priority + ": " + item.status)
            Text("Next: " + item.nextAction)
            if (item.pendingReason.isNotBlank()) Text("Pending: " + item.pendingReason)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onOpenCustomer(item) }) { Text("View") }
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
private fun CustomerEditorDialog(
    initial: CustomerRecord?,
    onDismiss: () -> Unit,
    onSave: (CustomerRecord) -> Unit
) {
    var name by remember(initial?.id) { mutableStateOf(initial?.name ?: "") }
    var phone by remember(initial?.id) { mutableStateOf(initial?.phone ?: "") }
    var inquiry by remember(initial?.id) { mutableStateOf(initial?.inquiryType ?: "Short Inquiry") }
    var status by remember(initial?.id) { mutableStateOf(initial?.status ?: "New Inquiry") }
    var priority by remember(initial?.id) { mutableStateOf(initial?.priority ?: "MEDIUM") }
    var nextAction by remember(initial?.id) { mutableStateOf(initial?.nextAction ?: "Contact customer") }
    var application by remember(initial?.id) { mutableStateOf(initial?.applicationNumber ?: "") }
    var pending by remember(initial?.id) { mutableStateOf(initial?.pendingReason ?: "") }
    var note by remember(initial?.id) { mutableStateOf(initial?.note ?: "") }
    var error by remember(initial?.id) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Customer" else "Edit Customer") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(name, { name = it }, label = { Text("Customer name *") }, singleLine = true) }
                item { OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, singleLine = true) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(inquiry == "Short Inquiry", { inquiry = "Short Inquiry" }, label = { Text("Short") })
                        FilterChip(inquiry == "Long Inquiry", { inquiry = "Long Inquiry" }, label = { Text("Long") })
                    }
                }
                item { OutlinedTextField(status, { status = it }, label = { Text("Status") }, singleLine = true) }
                item { OutlinedTextField(priority, { priority = it }, label = { Text("Priority") }, singleLine = true) }
                item { OutlinedTextField(nextAction, { nextAction = it }, label = { Text("Next action") }, singleLine = true) }
                item { OutlinedTextField(application, { application = it }, label = { Text("Application / Reference") }, singleLine = true) }
                item { OutlinedTextField(pending, { pending = it }, label = { Text("Pending reason") }, singleLine = true) }
                item { OutlinedTextField(note, { note = it }, label = { Text("Customer note") }, minLines = 2) }
                if (error.isNotBlank()) item { Text(error, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) {
                    error = "Customer name is required"
                } else {
                    onSave(CustomerRecord(
                        id = initial?.id ?: 0L,
                        name = name.trim(),
                        inquiryType = inquiry,
                        status = status.trim().ifBlank { "New Inquiry" },
                        priority = priority.trim().ifBlank { "MEDIUM" },
                        nextAction = nextAction.trim().ifBlank { "Contact customer" },
                        phone = phone.trim(),
                        applicationNumber = application.trim(),
                        pendingReason = pending.trim(),
                        lastContact = initial?.lastContact ?: "",
                        note = note.trim()
                    ))
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
