package app.robo.news.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.robo.news.R
import app.robo.news.databinding.ActivityHomeBinding
import app.robo.news.ui.news.NewsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = HomeActivity::class.java.simpleName
    }

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NewsFragment.newInstance())
                .commitNow()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount < 1) {
                binding.toolbar.setNavigationIcon(R.drawable.ic_toolbar_logo)
            } else {
                binding.toolbar.setNavigationIcon(R.drawable.ic_back)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount < 1) {
            } else {
                onBackPressed()
            }
        }
    }
}