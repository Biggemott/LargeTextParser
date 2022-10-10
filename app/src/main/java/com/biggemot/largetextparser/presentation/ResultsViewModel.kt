package com.biggemot.largetextparser.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biggemot.largetextparser.domain.ParseResult
import com.biggemot.largetextparser.domain.ParserInteractor
import com.biggemot.largetextparser.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val interactor: ParserInteractor
) : ViewModel() {

    private val _items = MutableLiveData<List<String>>()
    val items: LiveData<List<String>> = _items

    private val _selectedItems = MutableLiveData<Set<Int>>(setOf())
    val selectedItems: LiveData<Set<Int>> = _selectedItems

    private val _messageEvent = SingleLiveEvent<Message>()
    val messageEvent: LiveData<Message> = _messageEvent

    private var url: String? = null
    private var pattern: String? = null

    private var job: Job? = null

    fun setArgs(args: ResultsFragmentArgs) {
        if (url != args.url || pattern != args.pattern) {
            url = args.url
            pattern = args.pattern
            parseFile(args.url, args.pattern)
        }
    }

    private fun parseFile(url: String, pattern: String) {
        reset()

        job = viewModelScope.launch {
            val list = mutableListOf<String>()
            interactor.parseFile(url, pattern)
                .onEach { result ->
                    when (result) {
                        is ParseResult.Data -> {
                            list.add(result.value)
                            _items.postValue(list)
                        }
                        ParseResult.Error -> _messageEvent.postValue(Message.ParseError)
                        ParseResult.Finish -> _messageEvent.postValue(Message.ParseFinish)
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun itemClick(position: Int) {
        _selectedItems.value?.toMutableSet()?.let { set ->
            if (!set.add(position)) set.remove(position)
            _selectedItems.value = set
        }
    }

    private fun reset() {
        job?.cancel()
        _items.value = emptyList()
        _selectedItems.value = emptySet()
    }

    fun copyClick() {
        val items = _items.value ?: return
        val selected = _selectedItems.value?.sorted() ?: return

        val strings = mutableListOf<String>()
        selected.forEach { pos ->
            items.getOrNull(pos)?.let { strings.add(it) }
        }
        interactor.copyToClipboard(strings)
        _messageEvent.value = Message.CopySuccess
        _selectedItems.value = emptySet()
    }

    sealed class Message {
        object CopySuccess : Message()
        object ParseError : Message()
        object ParseFinish : Message()
    }
}