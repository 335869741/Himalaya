package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import zzz.bing.himalaya.repository.PlayerManager


class PlayerViewModel : ViewModel() {

    /**
     *
     */
    private val playerManager get() = PlayerManager.instance

    val playerDuration get() = playerManager.playerDuration
    val playOrder get() = playerManager.playOrder
    val playerMode get() = playerManager.playerMode
    val playerNow get() = playerManager.playerNow
    val playerBuffer get() = playerManager.playerBuffer
    val playerState get() = playerManager.playerState
    val playList get() = playerManager.playList


    /**
     *
     * @return PlayableModel
     */
    fun nowVoice(): PlayableModel = playerManager.playManager.currSound

    /**
     *
     * @return Int
     */
    fun duration() = playerManager.playManager.duration

    /**
     *
     * @return Boolean
     */
    fun isPlaying() = playerManager.playManager.isPlaying

    /**
     *
     */
    fun play() {
        playerManager.play()
    }

    /**
     *
     * @param position Int
     */
    fun play(position: Int) {
        playerManager.playManager.play(position)
    }

    /**
     *
     * @param playSwitch Function2<[@kotlin.ParameterName] PlayableModel?, [@kotlin.ParameterName] PlayableModel?, Unit>
     */
    fun playSwitch(playSwitch: (lastModel: PlayableModel?, curModel: PlayableModel?) -> Unit) {
        playerManager.playSwitch = playSwitch
    }

    /**
     *
     */
    fun playListOrder() {
        playerManager.playListOrder()
    }

    /**
     *
     * @param progress Int
     */
    fun seekTo(progress: Int) {
        playerManager.playManager.seekTo(progress)
    }

    /**
     *
     */
    fun playModeSwitch() {
        playerManager.playModeSwitch()
    }

    /**
     *
     */
    fun playPre() {
        playerManager.playPre()
    }

    /**
     *
     */
    fun playNext() {
        playerManager.playNext()
    }

    /**
     *
     */
    fun stop() {
        playerManager.stop()
    }
}