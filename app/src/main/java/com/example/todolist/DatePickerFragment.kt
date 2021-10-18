package com.example.todolist
//
//import android.app.DatePickerDialog
//import android.app.Dialog
//import android.app.TimePickerDialog
//import android.os.Bundle
//import android.text.format.DateFormat.is24HourFormat
//import android.widget.DatePicker
//import android.widget.TimePicker
//import androidx.fragment.app.DialogFragment
//import java.text.DateFormat
//import java.util.*
//
//
//private var day = 0
//private var month = 0
//private var year = 0
//private var hour = 0
//private var minute = 0
//private var calendar: Calendar? = null
//
//private fun getDateTimeCalender() {
//    val cal = Calendar.getInstance()
//    day = cal.get(Calendar.DAY_OF_MONTH)
//    month = cal.get(Calendar.MONTH)
//    year = cal.get(Calendar.YEAR)
//    hour = cal.get(Calendar.HOUR_OF_DAY)
//    minute = cal.get(Calendar.MINUTE)
//}
//
//class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        // Use the current time as the default values for the picker
////        val c = Calendar.getInstance()
////        val hour = c.get(Calendar.HOUR_OF_DAY)
////        val minute = c.get(Calendar.MINUTE)
//        // Create a new instance of TimePickerDialog and return it
//        getDateTimeCalender()
//        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
//    }
//
//    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
//
//    }
//}
//
//class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
//    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        getDateTimeCalender()
//        return DatePickerDialog(this.requireContext(), this, year, month, day)
//    }
//}