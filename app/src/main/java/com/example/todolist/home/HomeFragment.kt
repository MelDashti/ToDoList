package com.example.todolist.home

import android.content.SharedPreferences
import android.content.res.loader.ResourcesLoader
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.todolist.R
import com.example.todolist.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.prefs.Preferences
import androidx.appcompat.widget.SwitchCompat

import android.view.MenuInflater
import android.widget.CompoundButton
import android.widget.SearchView
import android.widget.Toast

import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.example.todolist.SharedViewModel
import com.example.todolist.util.PREF_DARK_THEME
import kotlin.reflect.jvm.internal.impl.load.java.Constant

// annotating android classes with @androidEntryPoint creates a dependency container that follows the android class lifecycle
//now hilt will create a dependencies container that is attached to homefragment's lifecycle and will be able to inject instances to Home fragment
@AndroidEntryPoint
class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding
//    private val viewModel: HomeViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkModeEnabled = false

    private val viewModel: SharedViewModel by activityViewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.toolbarRef.secondaryViewModel = viewModel

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())


        val adapter = TaskAdapter(TaskListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewPageFragment(it)
            )
        })

        binding.taskList.adapter = adapter

        //this is where we can diverge into different
        //scopes of programming

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.reversed())
        })

        val dividerItemDecoration =
            DividerItemDecoration(binding.taskList.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
        )

        binding.taskList.addItemDecoration(dividerItemDecoration)
        viewModel.navigateToBottomSheet.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(R.id.action_homeFragment_to_bottomSheetFragment)
                viewModel.onNavigatedToSearch()
            }
        })
        viewModel.searchResultList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.chipFilterResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.startSearch.observe(viewLifecycleOwner, Observer {
            if (it) {
                initializeSearch()
                binding.dateList.visibility = View.GONE
            }
        })

        binding.toolbarRef.searchBar.setOnCloseListener(androidx.appcompat.widget.SearchView.OnCloseListener {
            binding.dateList.visibility = View.VISIBLE
            false
        })
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarRef.toolbar)
        setHasOptionsMenu(true)
        return binding.root
    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        val menuItem = menu.findItem(R.id.my_switch)
//        isDarkModeEnabled = sharedPreferences.getBoolean(PREF_DARK_THEME, false)
//        if (isDarkModeEnabled) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        } else
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
//    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        val menuItem = menu.findItem(R.id.my_switch)
        val mySwitch = menuItem.actionView as SwitchCompat
        mySwitch.trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.track)
        mySwitch.thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.thumb)

        mySwitch.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            when {
                isChecked -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    mySwitch.isChecked = true
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    private fun initializeSearch() {
        val searchView = binding.toolbarRef.searchBar
        searchView.isIconified = false
        searching(searchView)
    }

    private fun searching(search: androidx.appcompat.widget.SearchView) {
        search.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchNow(newText)
                return false
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}
