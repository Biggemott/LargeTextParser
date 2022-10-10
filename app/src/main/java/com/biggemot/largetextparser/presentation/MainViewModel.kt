package com.biggemot.largetextparser.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.biggemot.largetextparser.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {

    private val _navEvent = SingleLiveEvent<NavDirections>()
    val navEvent: LiveData<NavDirections>
        get() = _navEvent

    fun startClick(url: String, pattern: String) {
        _navEvent.value = MainFragmentDirections.actionMainFragmentToResultsFragment(url, pattern)
    }
}