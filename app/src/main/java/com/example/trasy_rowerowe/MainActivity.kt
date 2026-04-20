package com.example.trasy_rowerowe

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trasy_rowerowe.ui.theme.Trasy_roweroweTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KategoriaSzlaku(val nazwa: String, val obrazekResId: Int, val krótkiOpis: String, val pełnyOpis: String)

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

    // Zaktualizowana lista - teraz są to kategorie szlaków z obrazkami i opisami
    val listaKategorii = listOf(
        KategoriaSzlaku(
            "Trasy Górskie",
            R.drawable.ic_mountains,
            "Wymagające podjazdy",
            "Ta kategoria jest dla zaawansowanych kolarzy. Oferuje trudne podejścia w Górach Sowich i okolicach. Trasy są strome, techniczne i idealne dla rowerów MTB. Przygotuj się na niesamowite widoki, ale też na duży wysiłek fizyczny."
        ),
        KategoriaSzlaku(
            "Trasy nad Wodą",
            R.drawable.ic_lake,
            "Relaks i płaski teren",
            "Długie, spokojne trasy wzdłuż jezior i rzek. Teren jest zazwyczaj płaski, co czyni te trasy doskonałymi na weekendowy wypoczynek z rodziną. Idealne do jazdy rekreacyjnej, z miejscami na piknik."
        ),
        KategoriaSzlaku(
            "Trasy Miejskie",
            R.drawable.ic_city,
            "Asfalt i ścieżki",
            "Szybkie przemieszczanie się po mieście. Trasy prowadzą przez parki i wydzielone ścieżki rowerowe. Doskonałe dla dojeżdżających do pracy lub do treningu szybkościowego na równej nawierzchni."
        )
    )

    var wybranaNazwaKategorii by rememberSaveable { mutableStateOf<String?>(null) }
    val wybranaKategoria = listaKategorii.find { it.nazwa == wybranaNazwaKategorii }

    // Stan stopera
    var czasWSekundach by rememberSaveable(wybranaNazwaKategorii) { mutableStateOf(0) }
    var czyDziala by rememberSaveable(wybranaNazwaKategorii) { mutableStateOf(false) }
    var historiaCzasow by rememberSaveable(wybranaNazwaKategorii) { mutableStateOf(listOf<String>()) }

    LaunchedEffect(czyDziala) {
        while (czyDziala) {
            delay(1000L)
            czasWSekundach++
        }
    }

    if (!czyPoziomo && wybranaKategoria != null) {
        BackHandler { wybranaNazwaKategorii = null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                title = { Text(text = if (wybranaKategoria != null && !czyPoziomo) "Szczegóły Szlaku" else "Wybór Szlaku", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (czyPoziomo) {
            Row(modifier = Modifier.padding(padding).fillMaxSize()) {

                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            KartaGlowna()
                        }
                        // Siatka kart kategorii
                        items(listaKategorii) { kategoria ->
                            CategoryGridCard(kategoria = kategoria, onClick = { wybranaNazwaKategorii = kategoria.nazwa })
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp).verticalScroll(rememberScrollState())) {
                    if (wybranaKategoria != null) {
                        WidokSzczegolow(
                            kategoria = wybranaKategoria,
                            czas = czasWSekundach,
                            czyDziala = czyDziala,
                            historia = historiaCzasow,
                            onStart = { czyDziala = true },
                            onPause = { czyDziala = false },
                            onReset = { czyDziala = false; czasWSekundach = 0 },
                            onSave = { czas -> historiaCzasow = historiaCzasow + czas }
                        )
                    } else {
                        Text(text = "Wybierz kategorię z siatki po lewej stronie.", color = Color.Gray, fontSize = 18.sp)
                    }
                }
            }
        } else {

            if (wybranaKategoria == null) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(padding).fillMaxSize().padding(8.dp)
                ) {

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        KartaGlowna()
                    }

                    items(listaKategorii) { kategoria ->
                        CategoryGridCard(kategoria = kategoria, onClick = { wybranaNazwaKategorii = kategoria.nazwa })
                    }
                }
            } else {

                Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
                    WidokSzczegolow(
                        kategoria = wybranaKategoria,
                        czas = czasWSekundach,
                        czyDziala = czyDziala,
                        historia = historiaCzasow,
                        onStart = { czyDziala = true },
                        onPause = { czyDziala = false },
                        onReset = { czyDziala = false; czasWSekundach = 0 },
                        onSave = { czas -> historiaCzasow = historiaCzasow + czas }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { wybranaNazwaKategorii = null }) { Text("Wróć do wyboru") }
                }
            }
        }
    }
}

@Composable
fun KartaGlowna() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Witaj w Trasach Rowerowych!",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Twoja osobista aplikacja do śledzenia czasu przejazdów. Wybierz kategorię szlaków poniżej, aby zmierzyć swój czas, zapisać wyniki i śledzić postępy.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun CategoryGridCard(kategoria: KategoriaSzlaku, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = kategoria.obrazekResId),
                contentDescription = kategoria.nazwa,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop // Przycinanie obrazka
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = kategoria.nazwa, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(text = kategoria.krótkiOpis, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun WidokSzczegolow(
    kategoria: KategoriaSzlaku,
    czas: Int,
    czyDziala: Boolean,
    historia: List<String>,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSave: (String) -> Unit
) {

    Image(
        painter = painterResource(id = kategoria.obrazekResId),
        contentDescription = kategoria.nazwa,
        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(text = kategoria.nazwa, fontWeight = FontWeight.Bold, fontSize = 24.sp)
    Text(text = kategoria.krótkiOpis, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
    Text(text = kategoria.pełnyOpis, fontSize = 16.sp)
    Spacer(modifier = Modifier.height(24.dp))

    KomponentStopera(
        czasWSekundach = czas,
        historiaCzasow = historia,
        onStart = onStart,
        onPause = onPause,
        onReset = onReset,
        onSave = onSave
    )
}

@Composable
fun KomponentStopera(
    czasWSekundach: Int,
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

