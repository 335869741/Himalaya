package zzz.bing.himalaya.domain

import android.os.Bundle
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import zzz.bing.himalaya.R
import zzz.bing.himalaya.ui.fragment.DetailFragment

object Action {

    /**
     * 跳转到专辑详情页
     * @param navController NavController
     * @param coverImageBase64 String
     * @param albumTitle String
     * @param author String
     * @param imageView ImageView 共享元素动画所要用的view
     */
    fun toDetail(
        navController: NavController,
        coverImageBase64: String,
        albumTitle: String,
        author: String,
        imageView: ImageView
    ) {
        navController.navigate(
            R.id.action_homeFragment_to_detailFragment,
            Bundle().apply {
                putString(DetailFragment.ACTION_COVER_IMAGE_BASE64, coverImageBase64)
                putString(DetailFragment.ACTION_ALBUM_TITLE, albumTitle)
                putString(DetailFragment.ACTION_AUTHOR, author)
            }, null,
            FragmentNavigatorExtras(imageView to "imageItemIcon")
        )
    }
}