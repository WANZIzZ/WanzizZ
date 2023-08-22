package com.wanzi.wanzizz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.wanzi.wanzizz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT

        val controller = WindowCompat.getInsetsController(window, binding.root)
//        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.isAppearanceLightStatusBars = true
//        controller.show(WindowInsetsCompat.Type.ime()) // 显示软键盘

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContent) { v, insets ->
            v.updatePadding(
                top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawer) { v, insets ->
            v.updatePadding(
                top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            )
            insets
        }

        binding.drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.d("Wanzi123", "onDrawerSlide")
            }

            override fun onDrawerOpened(drawerView: View) {
                controller.isAppearanceLightStatusBars = false
                Log.d("Wanzi123", "onDrawerOpened")
            }

            override fun onDrawerClosed(drawerView: View) {
                controller.isAppearanceLightStatusBars = true
                Log.d("Wanzi123", "onDrawerClosed")
            }

            override fun onDrawerStateChanged(newState: Int) {
                Log.d("Wanzi123", "onDrawerStateChanged")
            }
        })

        binding.text1.setOnClickListener {
//            binding.drawerLayout.open()
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
        binding.text2.setOnClickListener {
            binding.drawerLayout.close()
        }
    }
}