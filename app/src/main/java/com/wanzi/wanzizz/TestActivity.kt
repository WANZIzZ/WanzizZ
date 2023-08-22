package com.wanzi.wanzizz

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wanzi.wanzizz.components.Core
import com.wanzi.wanzizz.databinding.ActivityTestBinding
import com.wanzi.wanzizz.example.browser.BrowserAction
import com.wanzi.wanzizz.example.browser.BrowserStore
import com.wanzi.wanzizz.example.browser.TabListAction
import com.wanzi.wanzizz.ext.components
import com.wanzi.wanzizz.state.ext.flowScoped
import kotlinx.coroutines.flow.collect
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
            components.core.store.dispatch(TabListAction.SelectTabAction("NO.$selectedTab"))
        }
        binding.addTab.setOnClickListener {
            tabCount++
            components.core.store.dispatch(TabListAction.AddTabAction("NO.$tabCount"))
        }
    }
}