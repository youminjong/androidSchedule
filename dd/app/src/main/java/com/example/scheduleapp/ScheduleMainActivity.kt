package com.example.scheduleapp


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.scheduleapp.adapter.ScheAdapter
import com.example.scheduleapp.database.ScheDatabase
import com.example.scheduleapp.databinding.ActivityScheduleMainBinding
import com.example.scheduleapp.databinding.DialogAcBinding
import com.example.scheduleapp.model.ScheInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleMainActivity : AppCompatActivity() {


    private lateinit var binding : ActivityScheduleMainBinding
    private lateinit var ScheAdapter : ScheAdapter
    val calender = Calendar.getInstance()
    val yearr = calender.get(Calendar.YEAR).toString()
    private lateinit var roomDatabase : ScheDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ScheAdapter = ScheAdapter()

        binding.rvSche.adapter = ScheAdapter

        val cal = findViewById<CalendarView>(R.id.cd_view)

        roomDatabase = ScheDatabase.getInstance(applicationContext)!!

        CoroutineScope(Dispatchers.IO).launch {
            val lstSche = roomDatabase.scheDao().getAllReadDate() as ArrayList<ScheInfo>
            for(ScheItem in lstSche){
                ScheAdapter.addListItem(ScheItem)

            }
            runOnUiThread {
                ScheAdapter.notifyDataSetChanged()
            }

        }


        cal.setOnDateChangeListener { calendarView, yeaa, month, dayofMonth ->

            val bindingDialog = DialogAcBinding.inflate(LayoutInflater.from(binding.root.context),binding.root,false)

            AlertDialog.Builder(this)
                .setTitle("Sche 남기기")
                .setView(bindingDialog.root)
                .setPositiveButton("작성완료",DialogInterface.OnClickListener { dialogInterface, i ->

                    val ScheItem = ScheInfo()
                    ScheItem.ScheContent = bindingDialog.etMemo.text.toString()
                    ScheItem.ScheDate = SimpleDateFormat(yeaa.toString() + "년" + (month + 1) + "월" + dayofMonth + "일").format(Date())
                    ScheAdapter.addListItem(ScheItem)
                    CoroutineScope(Dispatchers.IO).launch {
                        roomDatabase.scheDao().insertScheData(ScheItem)
                        runOnUiThread {
                            ScheAdapter.notifyDataSetChanged()
                        }
                    }

                })
                .setNegativeButton("취소",DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()

          }







        }


    }



