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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        components.core.store.flowScoped(this) { flow ->
            flow.collect {
                Log.d("Wanzi123", "collect selectedTabId:${it.selectedTabId} tabs.size:${it.tabs.size}")
            }
        }

        binding.selectTab.setOnClickListener {
            val selectedTab = Random.nextInt(components.core.store.state.tabs.size + 1)
            components.useCases.tabsUseCases.selectTab("NO.$selectedTab")
        }
        binding.addTab.setOnClickListener {
            Log.d("Wanzi123", "点击按钮，发送事件")
            val tabCount = components.core.store.state.tabs.size
            components.useCases.tabsUseCases.addTab("No.$tabCount")
        }
    }
}