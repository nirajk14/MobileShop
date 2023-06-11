package com.example.mobileshop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.ApiRecyclerView.ApiState
import com.example.mobileshop.databinding.ActivityMainBinding
import androidx.activity.viewModels
import com.example.mobileshop.db.AppDatabase
import com.example.mobileshop.db.DBState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val PREFS_NAME = "MyPrefs"
    private val PREF_FIRST_RUN = "isFirstRun"
    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter

//    var appDb = AppDatabase.getDatabase(this)
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean(PREF_FIRST_RUN, true)

        if (isFirstRun) {
            // Code to be executed only once during installation
            initRepository()

            // Update the flag to indicate that the code has been executed
            val editor = sharedPreferences.edit()
            editor.putBoolean(PREF_FIRST_RUN, false)
            editor.apply()
        }


        initRecyclerView()





        mainViewModel.getAllProducts()
        lifecycleScope.launchWhenStarted {
            mainViewModel.productDataStateFlow.collect{
                when (it) {
                    is DBState.Loading->{
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = true
                    }
                    is DBState.Failure-> {
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = false
                        Log.d("HEHE YOU GOT AN ERROR", "GET REKT IT'S DB CALL ${it.msg}")

                    }

                    is DBState.Success-> {
                        binding.recyclerView.isVisible = true
                        binding.progressBar.isVisible=false
                        productAdapter.setData(it.data)
                    }

                    is DBState.Empty-> {

                    }
                }
            }
        }

    }

    private fun initRepository() {
        GlobalScope.launch(Dispatchers.IO) {

        }
    }

    private fun initRecyclerView() {
        productAdapter=ProductAdapter(ArrayList(), {products, position ->
            val clickedItem=productAdapter._mList[position]
            val intent= Intent(this,SingleView::class.java)
            intent.putExtra("singleItemData", clickedItem)
            startActivity(intent)
        } )
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@MainActivity)
            adapter=productAdapter
        }
    }
}