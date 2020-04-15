package com.mlprogramming.anothertodolist.alarm

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.main.MainActivity
import com.mlprogramming.anothertodolist.main.Navigator
import com.mlprogramming.anothertodolist.main.SharedViewModel
import com.mlprogramming.anothertodolist.model.Alarm
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.utils.TouchHelperSwipe
import com.mlprogramming.anothertodolist.utils.UiUtils
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.item_alarm.view.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
import kotlin.collections.ArrayList

class AlarmFragment : Fragment() {
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var navigator: Navigator
    private lateinit var task: ToDoTask

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)
    private val cal = Calendar.getInstance()
    private lateinit var deleteIcon: Drawable
    private var adapter: AlarmAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigator = Navigator((activity as MainActivity).getNavController())

        arguments?.let {
            task = AlarmFragmentArgs.fromBundle(it).task!!
        }

        alarmViewModel =
            ViewModelProviders.of(this, AlarmViewModelFactory(task))
                .get(AlarmViewModel::class.java)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        sharedViewModel.task.observe(this, Observer<ToDoTask> { data ->
            data?.let {
                task = data
            }
        })

        deleteIcon = UiUtils.getDeleteIcon(activity!!.applicationContext)

        setupView()
        initDatePicker()
        initTimePicker()
        setupStateObserver()
    }

    private fun setupStateObserver() {
        alarmViewModel.uiState.observe(this, Observer { state ->
            state.save?.let {
                when(it){
                    true -> sharedViewModel.updateData(task)
                }
            }
            state.navDirection?.let {
                navigator.navigate(it as NavDirections)
                alarmViewModel.onHandleIntent(UiIntent.NavigationCompleted)
            }
            state.alarms?.let {
                if (adapter == null) {
                    adapter = AlarmAdapter(it)
                    alarmsRecyclerView.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
            state.alarmDate?.let {
                alarm_date.editText!!.setText(it)
            }
            state.alarmTime?.let {
                alarm_time.editText!!.setText(it)
            }
        })
    }

    private fun setupView() {
        alarm_add.setOnClickListener {
            if (alarm_date.editText?.text != null && alarm_time.editText?.text != null) {
                alarmViewModel.onHandleIntent(UiIntent.AddAlarm(Alarm(cal.time.time)))
            }
        }

        cancel.setOnClickListener {
            alarmViewModel.onHandleIntent(UiIntent.Cancel)
        }

        save.setOnClickListener {
            alarmViewModel.onHandleIntent(UiIntent.SaveAlarms)
        }

        val manager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        alarmsRecyclerView.apply {
            layoutManager = manager
            setHasFixedSize(true)
        }
        enableSwipe()
    }

    private fun enableSwipe() {
        UiUtils.getItemTouchHelper(deleteIcon, object : TouchHelperSwipe {
            override fun onSwipe(position: Int) {
                alarmViewModel.onHandleIntent(
                    UiIntent.RemoveAlarm(
                        alarmViewModel.alarms.value!![position]
                    )
                )
            }
        }).attachToRecyclerView(alarmsRecyclerView)
    }

    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                alarmViewModel.onHandleIntent(UiIntent.SetAlarmDate(dateFormatter.format(cal.time)))
            }

        alarm_date.editText!!.setOnClickListener {
            DatePickerDialog(
                activity!!,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun initTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            alarmViewModel.onHandleIntent(UiIntent.SetAlarmTime(timeFormatter.format(cal.time)))
        }

        alarm_time.editText!!.setOnClickListener {
            TimePickerDialog(
                activity!!,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            private val dateTimeFormatter = SimpleDateFormat("dd MMM yyyy 'at' HH:mm", Locale.US)
        }

        fun bindAlarm(alarm: Alarm) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = alarm.time!!
            itemView.time.text = dateTimeFormatter.format(cal.time)
        }
    }

    class AlarmAdapter(_alarms: MutableLiveData<ArrayList<Alarm>>) :
        RecyclerView.Adapter<AlarmViewHolder>() {

        private val alarms = _alarms

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
            val viewHolder = LayoutInflater.from(parent.context).inflate(
                R.layout.item_alarm, parent, false
            )
            return AlarmViewHolder(viewHolder)
        }

        override fun getItemCount(): Int {
            alarms.value?.let {
                return it.size
            }
            return 0
        }

        override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
            holder.bindAlarm(alarms.value?.get(position)!!)
        }
    }
}