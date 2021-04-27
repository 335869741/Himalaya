package zzz.bing.himalaya.view.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ximalaya.ting.android.opensdk.model.track.Track
import zzz.bing.himalaya.databinding.FragmentPlayCoverBinding
import zzz.bing.himalaya.utils.ImageBlur
import zzz.bing.himalaya.utils.getImageUrl

class PlayerAdapter(requireActivity: FragmentActivity) : FragmentStateAdapter(requireActivity) {

    companion object{
        const val IMAGE_COVER_URL = "image_cover_url"
    }

    val playList by lazy { ArrayList<Track>() }

    override fun getItemCount() = playList.size

    override fun createFragment(position: Int) : Fragment {
        return PlayFragment().apply {
            arguments = Bundle().also { bundle ->
                bundle.putString(IMAGE_COVER_URL, playList[position].getImageUrl())
            }
        }
    }

    /**
     * PlayFragment()
     */
    class PlayFragment : Fragment(){
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View {
            val binding = FragmentPlayCoverBinding.inflate(layoutInflater)
            val str = arguments?.getString(IMAGE_COVER_URL)
            Glide.with(this).load(str).listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.also { drawable ->
                        val bitmap = ImageBlur.renderScriptBlur(
                            requireContext(),drawable.toBitmap(),1,0.1f
                        )
                        binding.imageBackground.setImageBitmap(bitmap)
                    }
                    return false
                }
            }).into(binding.imageCover)

            return binding.root
        }
    }
}
