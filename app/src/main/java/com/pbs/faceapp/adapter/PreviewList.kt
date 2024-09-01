package com.pbs.faceapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pbs.faceapp.Modal.FaceModal
import com.pbs.faceapp.databinding.PreviewlistBinding

class PreviewList(private val faceList: List<FaceModal>): RecyclerView.Adapter<PreviewList.ViewHolder>() {

    class ViewHolder(val binding: PreviewlistBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PreviewlistBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return faceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val face = faceList[position]

        holder.binding.faceNoText.text = "Face no: ${face.faceNo}"
        holder.binding.smiletext.text = "Smile probability: ${face.smileProbability}%"
        holder.binding.lefteyetext.text = "Left eye open probability: ${face.leftEyeOpenProbability}%"
        holder.binding.righteyetext.text = "Right eye open probability: ${face.rightEyeOpenProbability}%"

    }


}