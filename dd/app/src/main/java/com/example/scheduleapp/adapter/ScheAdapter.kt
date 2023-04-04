package com.example.scheduleapp.adapter

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.example.scheduleapp.database.ScheDatabase
import com.example.scheduleapp.databinding.DialogAcBinding
import com.example.scheduleapp.databinding.ListItemScheBinding
import com.example.scheduleapp.model.ScheInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class ScheAdapter : RecyclerView.Adapter<ScheAdapter.ScheViewHolder>() {
    private var lastSche : ArrayList<ScheInfo> = ArrayList()
    private lateinit var roomDatabase : ScheDatabase
    fun addListItem(ScheItem: ScheInfo){
        lastSche.add(0,ScheItem)
    }
    inner class ScheViewHolder(private val binding: ListItemScheBinding ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(scheItem: ScheInfo) {
            binding.tvContent.setText(scheItem.ScheContent)
            binding.tvDate.setText(scheItem.ScheDate)

            binding.btnRemove.setOnClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setTitle("[주의]")
                    .setMessage("제거하시면 데이터는 복구되지 않습니다.\n정말 제거하시겠습니까?")
                    .setPositiveButton("제거", DialogInterface.OnClickListener { dialogInterface, i ->
                        lastSche.remove(scheItem)
                        CoroutineScope(Dispatchers.IO).launch {
                            roomDatabase.scheDao().deleteScheData(scheItem)
                            (binding.root.context as Activity).runOnUiThread {
                                notifyDataSetChanged()
                            }
                        }
                        Toast.makeText(binding.root.context, "제거가 되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .show()

            }
            binding.root.setOnClickListener {
                val cal = Calendar.getInstance()
                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val innerLstSche = roomDatabase.scheDao().getAllReadDate()
                        for (item in innerLstSche) {
                            if (item.ScheDate == scheItem.ScheDate) {
                                item.ScheDate = SimpleDateFormat(year.toString() + "년" + (month + 1) + "월" + dayOfMonth + "일").format(Date())
                                roomDatabase.scheDao().updateScheData(item)
                            }
                        }
                        scheItem.ScheDate = SimpleDateFormat(year.toString() + "년" + (month + 1) + "월" + dayOfMonth + "일").format(Date())
                        lastSche.set(adapterPosition, scheItem)
                        (binding.root.context as Activity).runOnUiThread {
                            notifyDataSetChanged()
                        }
                    }
                }
                val bindingDialog = DialogAcBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)
                bindingDialog.etMemo.setText(scheItem.ScheContent)
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Sche 수정")
                    .setView(bindingDialog.root)
                    .setPositiveButton(
                        "수정완료",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val innerLstSche = roomDatabase.scheDao().getAllReadDate()
                                for (item in innerLstSche) {
                                    if (item.ScheContent == scheItem.ScheContent) {
                                        item.ScheContent = bindingDialog.etMemo.text.toString()
                                        roomDatabase.scheDao().updateScheData(item)
                                    }
                                }
                                scheItem.ScheContent = bindingDialog.etMemo.text.toString()
                                lastSche.set(adapterPosition, scheItem)
                                (binding.root.context as Activity).runOnUiThread {
                                    notifyDataSetChanged()
                                    DatePickerDialog(binding.root.context, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                                }
                            }
                        })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->

                    }).show()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheViewHolder {
        val binding = ListItemScheBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        roomDatabase = ScheDatabase.getInstance(binding.root.context)!!

        return ScheViewHolder(binding)
    }
    // 뷰 홀더가 바인딩 이루어질 때  해줘야 될 처리들을 구현
    override fun onBindViewHolder(holder: ScheAdapter.ScheViewHolder, position: Int) {
        holder.bind(lastSche[position])
    }
    // 리스트 총 갯수
    override fun getItemCount(): Int {
        return lastSche.size
    }
}