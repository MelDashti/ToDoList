package com.example.todolist.viewpage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.todolist.TaskDatabase
import com.example.todolist.databinding.ViewPageFragmentBinding

class ViewPageFragment : DialogFragment() {


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val binding = ViewPageFragmentBinding.inflate(inflater)

        val application = requireNotNull(this.activity).application

        val h = ViewPageFragmentArgs.fromBundle(requireArguments()).taskId
        Log.e("msgg", h.toString())
        val viewModelFactory = ViewPageViewModelFactory(
            application, h
        )

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ViewPageViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.navigateBackToHome.observe(this, Observer {
            if (it) {
                findNavController().popBackStack()
                viewModel.finishedNav()
            }
        })


        return binding.root
    }

}
