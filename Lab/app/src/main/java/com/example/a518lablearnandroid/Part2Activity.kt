package com.example.a518lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ─── ViewModel ───────────────────────────────────────────────────────────────

class ContactViewModel : ViewModel() {

    // Mock names grouped by first letter A-Z
    private val mockDatabase: Map<Char, List<String>> = mapOf(
        'A' to listOf("Alice Anderson", "Aaron Adams", "Amy Allen"),
        'B' to listOf("Bob Brown", "Barbara Baker", "Brian Bell"),
        'C' to listOf("Carol Clark", "Chris Collins", "Catherine Cook"),
        'D' to listOf("David Davis", "Diana Diaz", "Derek Dixon"),
        'E' to listOf("Emma Evans", "Edward Ellis", "Eleanor Edwards"),
        'F' to listOf("Frank Foster", "Fiona Fisher", "Felix Ford"),
        'G' to listOf("Grace Green", "George Gray", "Gina Griffin"),
        'H' to listOf("Henry Hall", "Hannah Harris", "Hugo Hill"),
        'I' to listOf("Ivan Ingram", "Iris Irwin"),
        'J' to listOf("James Johnson", "Jessica Jones", "Jack Jenkins"),
        'K' to listOf("Kevin King", "Karen Kelly", "Kyle Knox"),
        'L' to listOf("Laura Lewis", "Leo Lee", "Linda Long"),
        'M' to listOf("Michael Moore", "Maria Martinez", "Mark Mitchell"),
        'N' to listOf("Nancy Nelson", "Nathan Newman"),
        'O' to listOf("Oliver Owen", "Olivia O'Brien")
    )

    private val extraDatabase: Map<Char, List<String>> = mapOf(
        'P' to listOf("Peter Parker", "Patricia Price", "Paul Perry"),
        'Q' to listOf("Quinn Quinn"),
        'R' to listOf("Rachel Roberts", "Ryan Robinson", "Rebecca Reed"),
        'S' to listOf("Sarah Scott", "Samuel Smith", "Sophia Stewart"),
        'T' to listOf("Thomas Taylor", "Tina Turner", "Tyler Thompson"),
        'U' to listOf("Uma Underwood", "Ulysses Upton"),
        'V' to listOf("Victor Vasquez", "Victoria Vincent"),
        'W' to listOf("William Walker", "Wendy Ward", "Walter Wright"),
        'X' to listOf("Xander Xavier"),
        'Y' to listOf("Yasmine Young", "Yusuf York"),
        'Z' to listOf("Zoe Zhang", "Zachary Zimmerman")
    )

    // State: grouped list (letter -> names)
    private val _contacts = MutableStateFlow<Map<Char, List<String>>>(emptyMap())
    val contacts: StateFlow<Map<Char, List<String>>> = _contacts

    // State: loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var hasLoadedMore = false

    init {
        // Load initial data (A-O)
        _contacts.value = mockDatabase
    }

    /** Called when user scrolls to the bottom — simulate loading more data */
    fun loadMore() {
        if (_isLoading.value || hasLoadedMore) return
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // simulate network delay
            val merged = (_contacts.value + extraDatabase)
                .toSortedMap() // keep A-Z order
            _contacts.value = merged
            hasLoadedMore = true
            _isLoading.value = false
        }
    }
}

// ─── Activity ─────────────────────────────────────────────────────────────────

class Part2Activity : ComponentActivity() {

    private val viewModel: ContactViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("📋 Contact List", fontWeight = FontWeight.Bold) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF1565C0),
                                titleContentColor = Color.White
                            )
                        )
                    }
                ) { innerPadding ->
                    ContactListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(viewModel: ContactViewModel, modifier: Modifier = Modifier) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Flatten: [(letter, name), ...] preserving group order
    // We build a flat list of items: either a Header or a Name entry
    data class ContactItem(val letter: Char, val name: String)

    val flatItems: List<ContactItem> = contacts.entries
        .sortedBy { it.key }
        .flatMap { (letter, names) ->
            names.map { ContactItem(letter, it) }
        }

    // LazyListState to detect end-of-list
    val listState = rememberLazyListState()

    // Trigger loadMore when last item becomes visible
    LaunchedEffect(listState) {
        snapshotFlow {
            val total = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            Pair(total, lastVisible)
        }.collect { (total, lastVisible) ->
            if (total > 0 && lastVisible >= total - 1) {
                viewModel.loadMore()
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        // Group items by letter and emit a stickyHeader per group
        val grouped = flatItems.groupBy { it.letter }

        grouped.entries.sortedBy { it.key }.forEach { (letter, items) ->

            // ── Sticky Header ──
            stickyHeader(key = "header_$letter") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1565C0))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = letter.toString(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Items under that header ──
            itemsIndexed(items, key = { _, item -> item.name }) { _, contactItem ->
                ContactRow(name = contactItem.name, letter = contactItem.letter)
            }
        }

        // ── Loading indicator at the bottom ──
        if (isLoading) {
            item(key = "loading_indicator") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }
        }
    }
}

// ─── Row Item ─────────────────────────────────────────────────────────────────

@Composable
fun ContactRow(name: String, letter: Char) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circle with first letter
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = avatarColor(letter),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Simple palette cycling through 8 colors based on letter index
private fun avatarColor(letter: Char): Color {
    val colors = listOf(
        Color(0xFF1976D2), // Blue
        Color(0xFF388E3C), // Green
        Color(0xFFD32F2F), // Red
        Color(0xFF7B1FA2), // Purple
        Color(0xFFF57C00), // Orange
        Color(0xFF0097A7), // Cyan
        Color(0xFF5D4037), // Brown
        Color(0xFF455A64)  // Blue-Grey
    )
    return colors[(letter - 'A') % colors.size]
}
