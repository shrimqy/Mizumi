package com.dokja.mizumi.presentation.auth

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.navgraph.AuthScreenGraph
import com.dokja.mizumi.presentation.onboarding.OnBoardingEvent

@Composable
fun LoginScreen(
    rootNavController: NavController,
    onEvent: (OnBoardingEvent) -> Unit
) {
    val viewModel: ViewModel = hiltViewModel()
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val notificationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {isGranted ->
            if (isGranted) {
                onEvent(OnBoardingEvent.SaveAppEntry)
            }
            else
                onEvent(OnBoardingEvent.SaveAppEntry)
        }
    )
    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp),
                    contentDescription = "Logo"
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isLoading = true // Set loading indicator to true
                        viewModel.login(username, password) { success, token ->
                            isLoading = false // Set loading indicator to false after login attempt
                            if (success) {
                                // Handle successful login (e.g., navigate to main screen)
                                notificationPermissionResultLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            } else {
//                                TODO
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = { rootNavController.navigate(AuthScreenGraph.Register.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up")
                }
            }
        }
    }
}
