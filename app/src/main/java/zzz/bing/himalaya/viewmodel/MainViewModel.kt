package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.model.track.Track

class MainViewModel : ViewModel() {

    val playList by lazy { MutableLiveData<List<Track>>() }
//    val playState
    val playPosition by lazy { MutableLiveData<Int>().also { it.value = 0 } }
    val playVoice : Track
    get(){
        if (playList.value == null){
            playList.postValue(listOf(Track()))
        }
        if (playPosition.value == null){
            playPosition.postValue(0)
        }
        return playList.value!![playPosition.value!!]
    }
}