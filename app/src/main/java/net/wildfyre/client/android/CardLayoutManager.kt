package net.wildfyre.client.android

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.wildcard.view.*

class CardAdapter(val text: String, var c: Int) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var context: Context? = null
    public var count: Int = 0

    init {
        count = c
    }

    override fun getItemCount(): Int {return 1} //We only have one card per page

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        assert(position == 0)
        Log.e("CardAdapter", count.toString())
        holder.text.text = text + count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var layoutInflater = LayoutInflater.from(context)
        var cardView = layoutInflater.inflate(R.layout.wildcard, parent, false)
        return ViewHolder(cardView, count)
    }

    class ViewHolder(itemView: View, var count: Int) : RecyclerView.ViewHolder(itemView) {
        var text: TextView
        init {
            text = itemView.section_label
        }
    }
}