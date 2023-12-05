package com.compose.multi_mod_arch.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.compose.multi_mod_arch.ui.presentation.screens.auth.AuthenticationScreen
import com.compose.multi_mod_arch.ui.presentation.screens.auth.AuthenticationViewModel
import com.compose.multi_mod_arch.util.Constants.APP_ID
import com.compose.multi_mod_arch.util.Constants.REPORT_SCREEN_ARG_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
fun SetupNavGraph(startDestination: String, navHostController: NavHostController){
    NavHost(
        navController = navHostController,
        startDestination = startDestination)
    {
        authenticationRoute(
            navigateToHome = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Home.route)
            }
        )
        homeRoute()
        reportRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome : () -> Unit){
    composable(route = Screen.Authentication.route){
        val viewModel : AuthenticationViewModel = viewModel()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState= messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onTokenReceived = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )

            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome
        )
    }
}

fun NavGraphBuilder.homeRoute(){
    composable(route = Screen.Home.route){
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) 
        {
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    App.create(APP_ID).currentUser?.logOut()
                }
            }) {
                Text(text = "Logout")
            }

        }
    }
}

fun NavGraphBuilder.reportRoute(){
    composable(
        route = Screen.Report.route,
        arguments = listOf(navArgument(name = REPORT_SCREEN_ARG_KEY){
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ){

    }
}