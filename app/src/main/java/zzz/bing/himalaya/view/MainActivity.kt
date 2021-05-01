package zzz.bing.himalaya.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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

    val viewModel by lazy { mViewModel }
}