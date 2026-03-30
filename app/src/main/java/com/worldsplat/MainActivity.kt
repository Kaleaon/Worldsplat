package com.worldsplat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorldsplatTheme {
                WorldsplatApp()
            }
        }
    }
}

@Composable
private fun WorldsplatApp() {
    val planner = remember { PipelinePlanner() }
    val allUseCases = remember { UseCaseProfile.entries.toList() }
    var selectedUseCase by remember { mutableStateOf(UseCaseProfile.ARCHITECTURE) }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Worldsplat Android v0",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Use-case driven runtime selection for capture, preprocessing, and splat viewing.",
                style = MaterialTheme.typography.bodyMedium
            )

            UseCaseSelector(
                selected = selectedUseCase,
                all = allUseCases,
                onSelected = { selectedUseCase = it }
            )

            val plan = planner.buildPlan(selectedUseCase)
            PipelinePlanCard(plan = plan)
        }
    }
}

@Composable
private fun UseCaseSelector(
    selected: UseCaseProfile,
    all: List<UseCaseProfile>,
    onSelected: (UseCaseProfile) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(all) { profile ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = profile.label, modifier = Modifier.weight(1f))
                    Button(onClick = { onSelected(profile) }, enabled = profile != selected) {
                        Text(if (profile == selected) "Selected" else "Use")
                    }
                }
            }
        }
    }
}

@Composable
private fun PipelinePlanCard(plan: PipelinePlan) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Recommended plan", style = MaterialTheme.typography.titleMedium)
            Text(text = "Capture: ${plan.capture}")
            Text(text = "On-device preprocessing: ${plan.preprocessRuntime}")
            Text(text = "Splat training: ${plan.training}")
            Text(text = "Viewer: ${plan.viewer}")
            Text(text = "Notes: ${plan.notes}")
        }
    }
}
