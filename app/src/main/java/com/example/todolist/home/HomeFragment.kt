package com.example.todolist.home

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.TaskDatabase

import com.example.todolist.databinding.FragmentHomeBinding
import com.example.todolist.repository.TaskRepository
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

// annotating android classes with @androidEntryPoint creates a dependency container that follows the android class lifecycle
//now hilt will create a dependencies container that is attached to homefragment's lifecycle and will be able to inject instances to Home fragment
@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.toolbarRef.secondaryViewModel = viewModel
        val adapter = TaskAdapter(TaskListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToViewPageFragment(it)
            )
        })

        binding.taskList.adapter = adapter

        //this is where we can diverge into different
        //scopes of programming

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        val dividerItemDecoration =
            DividerItemDecoration(binding.taskList.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.divider
            )!!
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
                binding.dateList.visibility = View.GONE } })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    private fun initializeSearch() {
        val searchView = binding.toolbarRef.searchBar
        searchView.queryHint = "Search Tasks"
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
