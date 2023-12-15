package com.compose.report.presentation.screens.auth

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import com.compose.report.util.Constants.CLIENT_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    authenticated : Boolean,
    loadingState : Boolean,
    oneTapState : OneTapSignInState,
    messageBarState : MessageBarState,
    onButtonClicked : () -> Unit,
    onDialogDismissed : (String) -> Unit,
    navigateToHome : () -> Unit,
    onSuccessFirebaseSignIn : (String) -> Unit,
    onFailedFirebaseSignIn : (Exception) -> Unit,
){
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
       content = {
           ContentWithMessageBar(messageBarState = messageBarState) {
               AuthenticationContent(
                   loadingState = loadingState,
                   onButtonClicked = onButtonClicked
               )
           }
       }
    )
    
    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = CLIENT_ID,
        onTokenIdReceived = { tokenId ->
            val credential = GoogleAuthProvider.getCredential(tokenId, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        onSuccessFirebaseSignIn(tokenId)
                    }
                    else{
                        task.exception?.let { onFailedFirebaseSignIn(it) }
                    }
                }

        },
        onDialogDismissed = { message ->
            onDialogDismissed(message)
        }
    )
    
    LaunchedEffect(key1 = authenticated){
        if(authenticated){
            navigateToHome()
        }
    }
}
