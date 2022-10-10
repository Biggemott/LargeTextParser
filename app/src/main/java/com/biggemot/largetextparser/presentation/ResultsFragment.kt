package com.biggemot.largetextparser.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.biggemot.largetextparser.R
import com.biggemot.largetextparser.databinding.FragmentResultsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultsFragment : Fragment() {

    private lateinit var binding: FragmentResultsBinding
    private val viewModel: ResultsViewModel by viewModels()
    private val args: ResultsFragmentArgs by navArgs()

    private val adapter = ResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setArgs(args)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentResultsBinding.inflate(inflater).apply {
            binding = this
            resultsRecyclerView.adapter = adapter
            adapter.setOnItemClick {
                viewModel.itemClick(it)
            }
            toolbar.setupWithNavController(findNavController())
            toolbar.inflateMenu(R.menu.menu_results)
            toolbar.menu.findItem(R.id.copy).run {
                setOnMenuItemClickListener {
                    viewModel.copyClick()
                    true
                }
                isVisible = false
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.setItems(it)
        }

        viewModel.selectedItems.observe(viewLifecycleOwner) {
            binding.toolbar.menu.findItem(R.id.copy).isVisible = it.isNotEmpty()
            adapter.setSelectedItems(it)
        }

        viewModel.messageEvent.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                when (it) {
                    ResultsViewModel.Message.CopySuccess -> R.string.results_copy_success
                    ResultsViewModel.Message.ParseError -> R.string.results_parse_error
                    ResultsViewModel.Message.ParseFinish -> R.string.results_parse_finish
                },
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}