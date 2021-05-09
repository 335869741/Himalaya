package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import zzz.bing.himalaya.repository.PlayerManager


class PlayerViewModel : ViewModel() {

    /**
     *
     */
    private val playerManager by lazy { PlayerManager.instance }

    val playerDuration by lazy { playerManager.playerDuration }
    val playOrder by lazy { playerManager.playOrder }
    val playerMode by lazy { playerManager.playerMode }
    val playerNow by lazy { playerManager.playerNow }
    val playerBuffer by lazy { playerManager.playerBuffer }
    val playerState by lazy { playerManager.playerState }
    val playList by lazy { playerManager.playList }


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