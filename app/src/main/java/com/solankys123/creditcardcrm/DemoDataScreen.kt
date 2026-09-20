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

@Composable
fun DemoDataScreen(onClose: () -> Unit) {
    var query by remember { mutableStateOf("") }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Demo Customer QA") },
                    navigationIcon = {
                        TextButton(onClick = onClose) { Text("Back") }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    "Synthetic data only",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search name, status or inquiry") }
                )
                Spacer(Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(
                        DemoFixtures.customers.filter {
                            it.name.contains(query, true) ||
                                it.status.contains(query, true) ||
                                it.inquiryType.contains(query, true)
                        }
                    ) { customer ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(14.dp)) {
                                Text(
                                    customer.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(customer.inquiryType)
                                Text("Status: " + customer.status)
                                Text("Priority: " + customer.priority)
                                Text("Next: " + customer.nextAction)
                                if (customer.applicationNumber.isNotBlank()) {
                                    Text("Application: " + customer.applicationNumber)
                                }
                                if (customer.pendingReason.isNotBlank()) {
                                    Text("Pending/Issue: " + customer.pendingReason)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(customer.note)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = {}) { Text("Call") }
                                    OutlinedButton(onClick = {}) { Text("Follow-up") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
