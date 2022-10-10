package com.biggemot.largetextparser.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.biggemot.largetextparser.databinding.FragmentMainBinding
import com.biggemot.largetextparser.utils.throttleFirst
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMainBinding.inflate(inflater).apply {
            binding = this
            binding.run {
                startButton.setOnClickListener(
                    throttleFirst(lifecycleScope) {
                        urlEditText.text.toString().takeIf { it.isNotBlank() }?.let { url ->
                            viewModel.startClick(url, patternEditText.text.toString())
                        }
                    }
                )
                toolbar.setupWithNavController(findNavController())
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(it)
        }
    }

}