package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import zzz.bing.himalaya.repository.PlayerManager


class HomeViewModel : ViewModel() {

    private val playerManager by lazy { PlayerManager.instance }

    val isPlayIng get() = playerManager.playManager.isPlaying
    val nowVoice: PlayableModel get() = playerManager.playManager.currSound
    val playState get() = playerManager.playerState

    fun stop() {
        playerManager.stop()
    }

    fun play() {
        playerManager.play()
    }
}