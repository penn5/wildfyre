package net.wildfyre.client.android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.wildcard.view.*

class CardAdapter() : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var context: Context? = null

    override fun getItemCount(): Int {return 1} //We only have one card per page

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        assert(position == 0)
        holder.text.text = /*context?.getString(R.string.)*/""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        var layoutInflater = LayoutInflater.from(context)
        var cardView = layoutInflater.inflate(R.layout.wildcard, parent, false)
        return ViewHolder(cardView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView
        init {
            text = itemView.section_label
        }
    }
}