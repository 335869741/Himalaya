package zzz.bing.himalaya.domain

import android.os.Bundle
import androidx.navigation.NavController
import zzz.bing.himalaya.R

object Action {

    fun toDetail(navController: NavController) {
        navController.navigate(
            R.id.action_homeFragment_to_detailFragment,
            Bundle().apply {

            }
        )
    }
}