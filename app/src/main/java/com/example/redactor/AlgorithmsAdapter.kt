package com.example.redactor

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlgorithmsAdapter (private var photos: List<ItemBlock>, private var activity: Activity) :
    RecyclerView.Adapter<AlgorithmsAdapter.AlgorithmsViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: Int)
    }

        var listner: OnItemClickListener? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlgorithmsAdapter.AlgorithmsViewHolder {
        val viewshka = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false);
        return AlgorithmsViewHolder(viewshka);
    }

    override fun onBindViewHolder(holder: AlgorithmsAdapter.AlgorithmsViewHolder, position: Int) {
        holder.imageView.setImageResource(photos[position].photo)
        holder.titleTextView.text = photos[position].name
        holder.titleTextView.setSelected(true)
        holder.itemView.setOnClickListener {
            listner?.onItemClick(position,photos[position].photo)
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class AlgorithmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iconImg)
        val titleTextView: TextView = itemView.findViewById(R.id.iconText)
    }
}