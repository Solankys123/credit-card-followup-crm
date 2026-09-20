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
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FollowUpScreen(
    followUps: List<FollowUp> = SampleData.followUps,
    onBack: () -> Unit = {},
    onOpenCustomer: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Follow-ups") },
                    navigationIcon = {
                        TextButton(onClick = onBack) { Text("Back") }
                    }
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = true, onClick = {}, label = { Text("Today") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Overdue") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Upcoming") })
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(followUps) { followUp ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(14.dp)) {
                                Text(followUp.customerName, style = MaterialTheme.typography.titleMedium)
                                Text(followUp.reason)
                                Text("Due: " + followUp.dueAt)
                                Text("Priority: " + followUp.priority)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { onOpenCustomer(followUp.customerName) }) { Text("Customer") }
                                    OutlinedButton(onClick = {}) { Text("Call") }
                                    OutlinedButton(onClick = {}) { Text("Done") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
