package com.example.prm_project_ii_camera_gps.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_project_ii_camera_gps.R
import com.example.prm_project_ii_camera_gps.activities.PhotoPreviewActivity
import kotlinx.android.synthetic.main.image_card.view.*

class GalleryRecyclerAdapter(private val context: Context, private val photos: MutableList<Bitmap>) : RecyclerView.Adapter<GalleryRecyclerAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val galleryView = LayoutInflater.from(context).inflate(
                R.layout.image_card,
                parent,
                false
            )

        return GalleryViewHolder(galleryView)
    }

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(photos[position])

        holder.mView.setOnClickListener {
            val intent = Intent(context, PhotoPreviewActivity::class.java)
            intent.putExtra("picPath",photos[position])
            context.startActivity(intent)
        }
    }

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.photoCard
        var mView: View = itemView

        fun bind(photo: Bitmap) {
            this.photo.setImageBitmap(photo)
        }
    }

}