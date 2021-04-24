package zzz.bing.himalaya.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues
import zzz.bing.himalaya.views.MirrorView

private const val PROPNAME_IS_MIRROR = "com.example.android.motion.demo:is_mirror"

private val MIRROR_PROPERTIES = arrayOf(PROPNAME_IS_MIRROR)

class SharedFade: Transition() {

    override fun getTransitionProperties(): Array<String>? {
        return MIRROR_PROPERTIES
    }

    private fun captureMirrorValues(transitionValues: TransitionValues) {
        val view = transitionValues.view ?: return
        transitionValues.values[PROPNAME_IS_MIRROR] = view is MirrorView
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val startView = startValues.view ?: return null
        val endView = endValues.view ?: return null
        if (startView is MirrorView) {
            // The view is appearing. We animate the substance view.
            // The MirrorView was used merely for matching the layout position by other Transitions.
            return ObjectAnimator.ofFloat(endView, View.ALPHA, 0f, 0f, 1f)
        } else if (endView is MirrorView) { // Disappearing
            // The view is disappearing. We mirror the substance view, and animate the MirrorView.
            endView.substance = startView
            return ObjectAnimator.ofFloat(endView, View.ALPHA, 1f, 0f, 0f)
        }
        return null
    }
}