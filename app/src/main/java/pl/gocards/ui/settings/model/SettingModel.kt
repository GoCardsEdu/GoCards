package pl.gocards.ui.settings.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Grzegorz Ziemski
 */
abstract class SettingModel(
    application: Application
) : AndroidViewModel(application) {

    fun set(field: MutableLiveData<String>, newValue: Any, min: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newValue is Int) {
                field.postValue(newValue.toString())
            } else if (newValue is Float) {
                field.postValue(newValue.toInt().toString())
            } else if (newValue is String) {
                if (newValue.isNotEmpty()) {
                    try {
                        field.postValue(newValue.trim().toInt().toString())
                    } catch (e: NumberFormatException) {
                        field.postValue(min)
                    }
                } else {
                    field.postValue(min)
                }
            }
        }
    }
}