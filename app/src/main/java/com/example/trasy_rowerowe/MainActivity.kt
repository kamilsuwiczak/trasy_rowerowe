package com.example.trasy_rowerowe

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trasy_rowerowe.ui.theme.Trasy_roweroweTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Trasa(val nazwa: String, val dystans: String, val szczegoly: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Trasy_roweroweTheme {
                AplikacjaTras()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AplikacjaTras() {
    val konfiguracja = LocalConfiguration.current
    val czyPoziomo = konfiguracja.orientation == Configuration.ORIENTATION_LANDSCAPE

    val listaTras = listOf(
        Trasa("Trasa nad jezioro", "15 km", "Długa trasa wzdłuż jeziora jaroszewskiego."),
        Trasa("Wycieczka w góry", "25 km", "Trudne podejście w Górach Sowich."),
        Trasa("Wycieczka w góry 2", "18 km", "Lżejszy wariant w Górach Sowich."),
        Trasa("Szybki spacer", "5 km", "Spacer po parku miejskim.")
    )

    var wybranaNazwaTras by rememberSaveable { mutableStateOf<String?>(null) }
    val wybranaTrasa = listaTras.find { it.nazwa == wybranaNazwaTras }

    // --- WYNIESIONY STAN STOPERA ---
    // Stan jest zdefiniowany TUTAJ, więc nie zginie przy zmianie gałęzi if/else
    var czasWSekundach by rememberSaveable(wybranaNazwaTras) { mutableStateOf(0) }
    var czyDziala by rememberSaveable(wybranaNazwaTras) { mutableStateOf(false) }
    var historiaCzasow by rememberSaveable(wybranaNazwaTras) { mutableStateOf(listOf<String>()) }

    LaunchedEffect(czyDziala) {
        while (czyDziala) {
            delay(1000L)
            czasWSekundach++
        }
    }

    if (!czyPoziomo && wybranaTrasa != null) {
        BackHandler { wybranaNazwaTras = null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                title = { Text(text = if (wybranaTrasa != null && !czyPoziomo) "Szczegóły" else "Lista tras", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (czyPoziomo) {
            Row(modifier = Modifier.padding(padding).fillMaxSize()) {
                Column(modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState())) {
                    listaTras.forEach { trasa ->
                        KartaTrasy(trasa = trasa, onClick = { wybranaNazwaTras = trasa.nazwa })
                    }
                }
                Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp).verticalScroll(rememberScrollState())) {
                    if (wybranaTrasa != null) {
                        WidokSzczegolow(
                            trasa = wybranaTrasa,
                            czas = czasWSekundach,
                            czyDziala = czyDziala,
                            historia = historiaCzasow,
                            onStart = { czyDziala = true },
                            onPause = { czyDziala = false },
                            onReset = { czyDziala = false; czasWSekundach = 0 },
                            onSave = { czas -> historiaCzasow = historiaCzasow + czas }
                        )
                    } else {
                        Text(text = "Wybierz trasę z listy.", color = Color.Gray, fontSize = 18.sp)
                    }
                }
            }
        } else {
            if (wybranaTrasa == null) {
                Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
                    listaTras.forEach { trasa ->
                        KartaTrasy(trasa = trasa, onClick = { wybranaNazwaTras = trasa.nazwa })
                    }
                }
            } else {
                Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
                    WidokSzczegolow(
                        trasa = wybranaTrasa,
                        czas = czasWSekundach,
                        czyDziala = czyDziala,
                        historia = historiaCzasow,
                        onStart = { czyDziala = true },
                        onPause = { czyDziala = false },
                        onReset = { czyDziala = false; czasWSekundach = 0 },
                        onSave = { czas -> historiaCzasow = historiaCzasow + czas }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { wybranaNazwaTras = null }) { Text("Wróć do listy") }
                }
            }
        }
    }
}

@Composable
fun WidokSzczegolow(
    trasa: Trasa,
    czas: Int,
    czyDziala: Boolean,
    historia: List<String>,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSave: (String) -> Unit
) {
    Text(text = trasa.nazwa, fontWeight = FontWeight.Bold, fontSize = 24.sp)
    Text(text = "Dystans: ${trasa.dystans}", color = MaterialTheme.colorScheme.primary)
    Text(text = trasa.szczegoly, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(24.dp))

    KomponentStopera(
        nazwaTrasy = trasa.nazwa,
        czasWSekundach = czas,
        czyDziala = czyDziala,
        historiaCzasow = historia,
        onStart = onStart,
        onPause = onPause,
        onReset = onReset,
        onSave = onSave
    )
}

@Composable
fun KomponentStopera(
    nazwaTrasy: String,
    czasWSekundach: Int,
    czyDziala: Boolean,
    historiaCzasow: List<String>,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSave: (String) -> Unit
) {
    val sformatowanyCzas = String.format(Locale.getDefault(), "%02d:%02d", czasWSekundach / 60, czasWSekundach % 60)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Czas przejazdu", fontWeight = FontWeight.Bold)
            Text(text = sformatowanyCzas, fontSize = 48.sp, fontWeight = FontWeight.Bold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = onStart) { Icon(Icons.Filled.PlayArrow, "Start", tint = Color(0xFF4CAF50)) }
                IconButton(onClick = onPause) { Icon(Icons.Filled.Pause, "Pauza", tint = Color(0xFFFF9800)) }
                IconButton(onClick = onReset) { Icon(Icons.Filled.Stop, "Reset", tint = Color(0xFFF44336)) }
                IconButton(onClick = {
                    val data = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date())
                    onSave("[$data] $sformatowanyCzas")
                }) {
                    Icon(Icons.Filled.Add, "Zapisz", tint = MaterialTheme.colorScheme.primary)
                }
            }

            if (historiaCzasow.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                historiaCzasow.forEach { Text(it, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun KartaTrasy(trasa: Trasa, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trasa.nazwa, fontWeight = FontWeight.Bold)
            Text(text = trasa.dystans, fontSize = 14.sp)
        }
    }
}