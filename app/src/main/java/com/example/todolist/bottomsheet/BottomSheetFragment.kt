package com.example.todolist.bottomsheet

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class BottomSheetFragment : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val viewModel: BottomSheetViewModel by viewModels()
    private lateinit var viewDataBinding: BottomSheetBinding
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0
    private var calendar: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = BottomSheetBinding.inflate(inflater)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.navigateToTime.observe(viewLifecycleOwner, Observer {
            if (it) {
                getDateTimeCalender()
                DatePickerDialog(this.requireContext(), this, year, month, day).show()
                viewModel.finishedNavTime()
            }
        })

        viewModel.navigateToNote.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewDataBinding.noteText.visibility = View.VISIBLE
            }
        })

        viewModel.navigateBackToHome.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.addNew(calendar)
                findNavController().popBackStack()
                viewModel.finishedNav()
            }
        })
        createChannel(
            getString(R.string.reminder_notification_channel_id),
            getString(R.string.reminder_notification_channel_name)
        )
        return viewDataBinding.root
    }

    private fun getDateTimeCalender() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar = Calendar.getInstance()
        calendar!!.set(year, month, dayOfMonth)
        getDateTimeCalender()
        TimePickerDialog(context, this, hour, minute, true).show()
    }


    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar!!.set(Calendar.MINUTE, minute)
        calendar!!.set(Calendar.SECOND, 0)
    }


    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = "Aye what's poppin"
            val notificationManager =
                requireActivity().getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}