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

@AndroidEntryPoint
class ViewPageFragment : DialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

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
                DatePickerDialog(this.requireContext(), this, 0, 0, 0).show()
            }
            viewModel.finishedSettingReminder()
        })


        return binding.root
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        TODO("Not yet implemented")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        TODO("Not yet implemented")
    }

}
