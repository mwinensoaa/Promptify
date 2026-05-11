package com.mwinensoaa.promptify.screens



import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,

        startDestination = "permission"
    ) {

        composable("permission") {

            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(
                        "home"
                    )
                }
            )
        }

        /*
         * HOME SCREEN
         */
        composable("home") {

            HomeScreen(
            ) { extractedText ->

                // Encode text for navigation
                val encodedText =
                    Uri.encode(extractedText)

                navController.navigate(
                    "prompt/$encodedText"
                )
            }
        }

        /*
         * PROMPT SCREEN
         */
        composable(
            route = "prompt/{text}",

            arguments = listOf(
                navArgument("text") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val extractedText =
                backStackEntry.arguments
                    ?.getString("text")
                    ?: ""

            PromptScreen(
                extractedText = extractedText,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}