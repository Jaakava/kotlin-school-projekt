package com.example.companyapp

import android.R.attr.start
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.companyapp.ui.theme.CompanyAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    companion object {
        const val SHARED_PREFS = "shared_prefs"
        const val EMAIL_KEY = "email_key"
        const val NAME_KEY = "name_key"
        const val PASSWORD_KEY = "password_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val email = sharedPreferences.getString(EMAIL_KEY, null)
        val name = sharedPreferences.getString(NAME_KEY, null)

        setContent {
            CompanyAppTheme {
                HomeScreen(
                    email = email,
                    name = name,
                    onLogout = {
                        sharedPreferences.edit().clear().apply()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onNavigate = { destination ->
                        when (destination) {
                            "news" -> startActivity(Intent(this, NewsActivity::class.java))
                            "employees" -> startActivity(Intent(this, EmployeesActivity::class.java))
                            "offices" -> startActivity(Intent(this, OfficesActivity::class.java))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(email: String?, name: String?, onLogout: () -> Unit, onNavigate: (String) -> Unit) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Profiilikuva + nimi + email
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.dog),
                        contentDescription = "Profiilikuva",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = name ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email ?: "unknown@email.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = true,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("News") },
                    selected = false,
                    icon = { Icon(Icons.Default.Email, contentDescription = "News") },
                    badge = { Text("22") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("news")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Employees") },
                    selected = false,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Employees") },
                    badge = { Text("45") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("employees")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Offices") },
                    selected = false,
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Offices") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("offices")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()

                // Footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "(c) 2026 - VaJaa Company",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version: 10.01.66",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("CompanyApp") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Avaa valikko"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dog),
                        contentDescription = stringResource(id = R.string.dog_content_description),
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Row {
                        Text(
                            text = stringResource(R.string.welcome),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = name ?: stringResource(R.string.unknown_user),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    val sdf = SimpleDateFormat("'Today is: 'dd-MM 'at 'HH:mm", Locale.getDefault())
                    val currentDateAndTime = sdf.format(Date())
                    Text(
                        text = currentDateAndTime,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.Meetings),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column() {
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {

                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(40.dp)
                            )

                            Text(
                                text = "Fri 03.4.2026 at 10:00 - 12:00\nclient week report",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )

                        }
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {

                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(40.dp)
                            )

                            Text(
                                text = "Mon 06.4.2026 at 09:00 - 10:00\nWeekly company meeting",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        }

                        Row() {

                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = "Calendar",
                                modifier = Modifier.size(40.dp)
                            )

                            Text(
                                text = "Wed 08.4.2026 at 14:00 - 16:00\nClient dinner",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        }
                    }

                    Text(
                        text = stringResource(R.string.Todos),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column() {
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {

                            Icon(
                                imageVector = Icons.Filled.PendingActions,
                                contentDescription = "Clipboard",
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Due at 02.4.2026 \nMake client week report",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.PlaylistRemove,
                                contentDescription = "Trashcan",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(start = 24.dp)
                            )

                        }
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {

                            Icon(
                                imageVector = Icons.Filled.PendingActions,
                                contentDescription = "Clipboard",
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Due Mon 05.4.2026 \nMake weekly report",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.PlaylistRemove,
                                contentDescription = "Trashcan",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(start = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}