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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FollowUpScreen(
    followUps: List<FollowUp> = SampleData.followUps
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Follow-ups", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("Today") }
            )
            FilterChip(
                selected = false,
                onClick = {},
                label = { Text("Overdue") }
            )
            FilterChip(
                selected = false,
                onClick = {},
                label = { Text("Upcoming") }
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(followUps) { followUp ->
                FollowUpCard(followUp)
            }
        }
    }
}

@Composable
private fun FollowUpCard(item: FollowUp) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(item.customerName, style = MaterialTheme.typography.titleMedium)
            Text(item.reason)
            Text("Due: " + item.dueAt)
            Text("Priority: " + item.priority)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}) { Text("Call") }
                OutlinedButton(onClick = {}) { Text("Done") }
                OutlinedButton(onClick = {}) { Text("Reschedule") }
            }
        }
    }
}
