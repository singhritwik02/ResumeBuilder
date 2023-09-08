package com.ritwik.resumebuilder.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ritwik.resumebuilder.databinding.FragmentPreviewQrBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PreviewQRFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PreviewQRFragment : Fragment() {
    private lateinit  var binding:FragmentPreviewQrBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreviewQrBinding.inflate(layoutInflater)
        // getting link from arguments
        MobileAds.initialize(requireContext())
        val adView = binding.pqrfBottomBanner
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        val qrLink = arguments?.getString("qrLink")
        if(qrLink != null) {
            val qrBitmap = handleQRLink(qrLink)
            binding.qrImage.scaleType = ImageView.ScaleType.FIT_XY
            binding.qrImage.setImageBitmap(qrBitmap)
        }
        binding.fpqrCaptionField.addTextChangedListener {
            val text = binding.fpqrCaptionField.text.toString()
            if(text.isNotEmpty())
            {
            binding.fpqrCaptionText.visibility = View.VISIBLE
            binding.fpqrCaptionText.text = text
            }
            else
            {
                binding.fpqrCaptionText.visibility = View.GONE
            }

        }
        binding.fqrpShareButton.setOnClickListener {
            sharePalette()
        }
        return binding.root
    }
    private fun handleQRLink(link:String): Bitmap?
    {
        val multiFormatWriter = MultiFormatWriter()
        try {

            val bitmatrix = multiFormatWriter.encode(link, BarcodeFormat.QR_CODE, 720, 720)
            val bitmap = BarcodeEncoder().createBitmap(bitmatrix)
            return bitmap


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    private fun sharePalette() {
        val bitmap = createBitmap()
        val bitmapPath = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            "palette",
            "share palette"
        )
        val bitmapUri = Uri.parse(bitmapPath)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        startActivity(Intent.createChooser(intent, "Share"))
    }
    private fun createBitmap():Bitmap
    {
        binding.fqprQRLayout.layout(0,0,binding.fqprQRLayout.width,binding.fqprQRLayout.height)
        val bitmap = Bitmap.createBitmap(binding.fqprQRLayout.width,binding.fqprQRLayout.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        binding.fqprQRLayout.draw(canvas)
        return bitmap
    }

    companion object {
        private const val TAG = "PreviewQRFragment"
    }
}