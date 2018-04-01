package com.slim.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.slim.adapter.SlimAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var slimAdapter: SlimAdapter<String>
    private var index: Int = 20
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val data = mutableListOf<String>()
        for (i in 1..20) {
            data.add(i.toString())
        }
        slimAdapter = SlimAdapter.create<String>(findViewById(R.id.rv))
                .layout(LinearLayoutManager(this))
                .empty(R.layout.loading)
                .multiple({ _: String?, position: Int ->
                    when {
                        position % 2 == 0 ->
                            return@multiple R.layout.item_1
                        else ->
                            return@multiple R.layout.item_2
                    }
                })
                .register({ holder, item, _ ->
                    holder?.text(R.id.text, item)
                })
                .click({ _, item, _ ->
                    Toast.makeText(this, item, Toast.LENGTH_SHORT).show()
                })
                .initNew(data).loadMore {
                    recyclerView.postDelayed({
                        loadData()

                    }, 2000)
                }
    }

    private fun loadData() {
        val data = mutableListOf<String>()
        for (i in 1..20) {
            data.add((index + i).toString())
            if (i == 20) index += 20
        }
        slimAdapter.initMore(data)
    }
}
