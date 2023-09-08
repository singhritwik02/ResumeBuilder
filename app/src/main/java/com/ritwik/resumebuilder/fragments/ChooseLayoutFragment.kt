package com.ritwik.resumebuilder.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databinding.FragmentChooseLayoutBinding
import com.ritwik.resumebuilder.datamodel.ResumeLayout
import com.ritwik.resumebuilder.helper.LoadingView


class ChooseLayoutFragment : Fragment() {
    private lateinit var binding:FragmentChooseLayoutBinding
    private var layoutList = ArrayList<ResumeLayout>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChooseLayoutBinding.inflate(inflater, container, false)
        // set nav bar color to white
        requireActivity().window.navigationBarColor = requireContext().getColor(R.color.white)
        val recyclerView = binding.fclRecyclerView
        // setting grid layout manager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = LayoutRecyclerAdapter()
        // getting layout links
        //
        val loadingView = LoadingView(requireContext())
        binding.root.post {
            loadingView.showWindow()
        }
        getLayouts {
            layoutList.clear()
            layoutList.addAll(it)
            binding.fclRecyclerView.adapter?.notifyDataSetChanged()
            loadingView.hideWindow()
        }
        return binding.root
    }
    inner class LayoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val previewImage = itemView.findViewById<ImageView>(R.id.slp_PreviewImage)

        // function to set image
        fun setImage(previewLink:String)
        {
            // loading image with glide
            Glide.with(requireContext()).load(previewLink).fitCenter().into(previewImage)
        }
    }
    inner class LayoutRecyclerAdapter:RecyclerView.Adapter<LayoutViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
            val view = layoutInflater.inflate(R.layout.single_layout_preview,parent,false)
            return LayoutViewHolder(view)
        }

        override fun getItemCount(): Int {
            return layoutList.size
        }

        override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
            holder.setImage(layoutList[position].layoutPreviewLink)
            holder.itemView.setOnClickListener {
                openPreview(layoutList[position].layoutLink)
            }
        }

    }
    private fun openPreview(layoutLink:String)
    {
        val fragment = ExportFragment()
        val bundle = Bundle()
        bundle.putString("layoutLink",layoutLink)
        fragment.arguments = bundle
        val fragmentTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }
    private fun getLayouts(function:(ArrayList<ResumeLayout>) -> Unit) {
        FirebaseDatabase.getInstance("https://resumebuilderweb-4ddfa-default-rtdb.firebaseio.com/")
            .reference.child("Layouts")
            .addValueEventListener(
                object:ValueEventListener
                {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(!snapshot.exists())
                        {
                            return
                        }
                        val layouts = ArrayList<ResumeLayout>()
                        var tempLayout = ResumeLayout()
                        for(layout in snapshot.children)
                        {
                            tempLayout = ResumeLayout()
                            if(layout.hasChild("LayoutLink"))
                                tempLayout.layoutLink = layout.child("LayoutLink").value.toString()
                            if(layout.hasChild("LayoutPreviewLink"))
                                tempLayout.layoutPreviewLink = layout.child("LayoutPreviewLink").value.toString()
                            layouts.add(tempLayout)


                        }
                        function(layouts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, "onCancelled: ${error.message}")
                    }

                }
            )

    }

    companion object {
        private const val TAG = "ChooseLayoutFragment"
    }
}