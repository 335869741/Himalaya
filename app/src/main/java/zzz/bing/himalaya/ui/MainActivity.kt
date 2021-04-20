package zzz.bing.himalaya.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import zzz.bing.himalaya.databinding.ActivityMainBinding
import zzz.bing.himalaya.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private val mViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel
        setContentView(mBinding.root)
    }

//    fun findNavigationById(@IdRes viewId: Int): NavController {
//        val navHostFragment = supportFragmentManager.findFragmentById(viewId) as NavHostFragment
//        return navHostFragment.navController
//    }
//
//    fun getViewModel() = mViewModel
}