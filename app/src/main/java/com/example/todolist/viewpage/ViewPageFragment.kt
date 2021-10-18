package com.example.todolist.viewpage

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.todolist.TaskDatabase
import com.example.todolist.databinding.ViewPageFragmentBinding
import com.example.todolist.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ViewPageFragment : DialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {


    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0
    private var calendar: Calendar? = null

    private val viewModel: ViewPageViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = ViewPageFragmentBinding.inflate(inflater)

        val application = requireNotNull(this.activity).application

        val h = ViewPageFragmentArgs.fromBundle(requireArguments()).taskId

        binding.viewModel = viewModel
        viewModel.fetchTaskInfo(h)
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.navigateBackToHome.observe(this, Observer {
            if (it) {
                findNavController().popBackStack()
                viewModel.finishedNav()
            }
        })

        viewModel.navigateToDatePicker.observe(this, Observer {
            if (it) {
                DatePickerDialog(this.requireContext(), this, year, month, day).show()
            }
            viewModel.finishedSettingReminder()
        })


        return binding.root
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


}
