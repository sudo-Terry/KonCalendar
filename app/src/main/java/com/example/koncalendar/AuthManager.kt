package com.example.koncalendar

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.AuthenticationCallback
import okhttp3.*
import java.io.IOException
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


class AuthManager(private val activity: Activity) {

    private val CLIENT_ID = "3b35b268-c391-42ad-a3c0-260de3da96f6"  // 여기서 YOUR_CLIENT_ID를 실제 클라이언트 ID로 변경
    private val SCOPES = arrayOf("User.Read", "Calendars.Read")

    private var msalApp = PublicClientApplication.createMultipleAccountPublicClientApplication(activity.applicationContext,
        R.raw.auth_config_multiple_account);    //R.raw.auth_config_multiple_account

    var accessToken = mutableStateOf("")

    fun acquireToken() {
        msalApp.acquireToken(activity, SCOPES, getAuthInteractiveCallback())
    }

    private fun getAuthInteractiveCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                accessToken.value = authenticationResult.accessToken
                Log.d("MSAL", "Access Token: ${accessToken.value}")
            }

            override fun onError(exception: MsalException) {
                Log.e("MSAL", "Authentication failed: $exception")
                accessToken.value = "Authentication failed: $exception"
            }

            override fun onCancel() {
                Log.d("MSAL", "User cancelled login.")
                accessToken.value = "User cancelled login."
            }
        }
    }
}

@Composable
fun TestTokenScreen(authManager: AuthManager) {
    val accessToken by remember { authManager.accessToken }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Button(onClick = { authManager.acquireToken() }) {
            Text(text = "Acquire Token")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Access Token: $accessToken")
    }
}