package com.mlprogramming.anothertodolist.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mlprogramming.anothertodolist.model.Place
import com.mlprogramming.anothertodolist.model.ToDoTask

data class UiState(
    val navDirection: Any? = null,
    val places: MutableLiveData<ArrayList<Place>>? = null,
    val loading: Boolean? = false,
    val save: Boolean? = false
)

sealed class UiIntent {
    data class RemovePlace(var place: Place) : UiIntent()
    data class AddPlace(var place: Place) : UiIntent()
    object ShowPlaces : UiIntent()
    object Save : UiIntent()
    object Cancel : UiIntent()
    object NavigationCompleted : UiIntent()
}

sealed class Command {
    data class RemovePlace(var place: Place) : Command()
    data class AddPlace(var place: Place) : Command()
    object ShowPlaces : Command()
    object Save : Command()
    object Cancel : Command()
    object NavigationCompleted : Command()
}

class PlaceViewModel(private val task: ToDoTask) : ViewModel() {
    var places = MutableLiveData<ArrayList<Place>>(task.places?.clone() as ArrayList<Place>?)

    private val _uiState = MutableLiveData<UiState>().apply {
        value = getInitialState()
    }

    val uiState: MutableLiveData<UiState>
        get() = _uiState

    fun onHandleIntent(intent: UiIntent) {
        return when (intent) {
            is UiIntent.ShowPlaces -> onCommand(Command.ShowPlaces)

            is UiIntent.Cancel -> onCommand(Command.Cancel)

            is UiIntent.Save -> onCommand(Command.Save)

            is UiIntent.RemovePlace -> onCommand(Command.RemovePlace(intent.place))

            is UiIntent.AddPlace -> onCommand(Command.AddPlace(intent.place))

            is UiIntent.NavigationCompleted -> onCommand(Command.NavigationCompleted)
        }
    }

    private fun onCommand(command: Command) {
        val currentState = _uiState.value ?: return
        _uiState.value = reduce(currentState, command)
    }

    private fun reduce(state: UiState, command: Command): UiState {
        return when (command) {

            is Command.ShowPlaces -> {
                if (places.value == null) {
                    places.value = ArrayList<Place>()
                }
                state.copy(
                    places = places
                )
            }

            is Command.RemovePlace -> {
                places.value!!.remove(command.place)
                state.copy(
                    places = places
                )
            }

            is Command.AddPlace -> {
                if (places.value == null) {
                    places.value = ArrayList<Place>()
                }
                val place = command.place
                place.marker!!.title = ("Place " + places.value!!.size + 1)

                places.value!!.add(place)
                state.copy(
                    places = places
                )
            }

            is Command.Save -> {
                task.places = places.value

                val fragmentDirections =
                    PlaceFragmentDirections.actionPlaceFragmentToTaskFragment()

                state.copy(
                    navDirection = fragmentDirections,
                    loading = true,
                    save = true
                )
            }

            is Command.Cancel -> {
                val fragmentDirections =
                    PlaceFragmentDirections.actionPlaceFragmentToTaskFragment()

                state.copy(
                    navDirection = fragmentDirections,
                    loading = true
                )
            }

            is Command.NavigationCompleted -> state.copy(navDirection = null)
        }
    }

    private fun getInitialState() = UiState(
        navDirection = null,
        places = places,
        loading = null,
        save = null
    )
}