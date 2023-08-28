package com.wanzi.wanzizz

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wanzi.wanzizz.databinding.ActivityTestBinding
import com.wanzi.wanzizz.ext.components
import com.wanzi.wanzizz.state.ext.flowScoped
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private var tabCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        components.core.store.flowScoped(this) { flow ->
            flow.collect {
                Log.d("Wanzi", "collect selectedTabId:${it.selectedTabId} tabs.size:${it.tabs.size}")
            }
        }

        binding.selectTab.setOnClickListener {
            val selectedTab = Random.nextInt(tabCount + 1)
            components.useCases.tabsUseCases.selectTab("NO.$selectedTab")
        }
        binding.addTab.setOnClickListener {
            tabCount++
            components.useCases.tabsUseCases.addTab("No.$tabCount")
        }
    }
}