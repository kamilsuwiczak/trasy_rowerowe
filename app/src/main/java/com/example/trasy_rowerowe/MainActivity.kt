package com.example.trasy_rowerowe

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trasy_rowerowe.ui.theme.Trasy_roweroweTheme

data class Trasa(val nazwa: String, val dystans: String, val szczegoly: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Trasy_roweroweTheme() {
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

    var wybranaTrasa by rememberSaveable { mutableStateOf<Trasa?>(null) }

    val listaTras = listOf(
        Trasa("Trasa nad jezioro", "15 km", "Długa trasa wzdłuż jeziora jaroszewskiego."),
        Trasa("Wycieczka w góry", "25 km", "Trudne podejście w Górach Sowich."),
        Trasa("Wycieczka w góry 2", "18 km", "Lżejszy wariant w Górach Sowich."),
        Trasa("Szybki spacer", "5 km", "Spacer po parku miejskim.")
    )

    if (!czyPoziomo && wybranaTrasa != null) {
        BackHandler {
            wybranaTrasa = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                title = {
                    Text(
                        text = if (wybranaTrasa != null && !czyPoziomo) "Szczegóły trasy" else "Lista tras",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        if (czyPoziomo) {

            Row(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    listaTras.forEach { trasa ->
                        KartaTrasy(trasa = trasa, onClick = { wybranaTrasa = trasa })
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    if (wybranaTrasa != null) {
                        WidokSzczegolow(trasa = wybranaTrasa!!)
                    } else {
                        Text(text = "Wybierz trasę z listy po lewej stronie.", color = Color.Gray, fontSize = 18.sp)
                    }
                }
            }
        } else {
            if (wybranaTrasa == null) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    listaTras.forEach { trasa ->
                        KartaTrasy(trasa = trasa, onClick = { wybranaTrasa = trasa })
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    WidokSzczegolow(trasa = wybranaTrasa!!)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { wybranaTrasa = null }) {
                        Text("Wróć do listy")
                    }
                }
            }
        }
    }
}

@Composable
fun WidokSzczegolow(trasa: Trasa) {
    Text(text = "Wybrałeś trasę:", fontSize = 16.sp, color = Color.Gray)
    Text(text = trasa.nazwa, fontWeight = FontWeight.Bold, fontSize = 24.sp)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Dystans: ${trasa.dystans}", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = trasa.szczegoly, fontSize = 18.sp)
}

@Composable
fun KartaTrasy(trasa: Trasa, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = trasa.nazwa, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Dystans: ${trasa.dystans}", color = Color.DarkGray)
        }
    }
}