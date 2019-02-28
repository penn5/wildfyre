package net.wildfyre.client.android

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.kevinsawicki.etag.CacheRequest
import com.github.kevinsawicki.etag.EtagCache
import com.github.kevinsawicki.etag.EtagCache.TEN_MB
import kotlinx.android.synthetic.main.item_comment.view.*
import net.wildfyre.posts.Comment
import java.io.File
import kotlin.concurrent.thread


class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    @Suppress("PrivatePropertyName")
    private val TAG = "WildFyreCommentsAdapter"
    @Suppress("PrivatePropertyName")
    private val AVATARS_DIR = "avatars"

    private var cache: EtagCache? = null

    private var context: Context? = null
    private var handler: Handler? = null
    private var comments: ArrayList<Comment> = ArrayList()

    override fun getItemCount(): Int {
        return comments.size //We only have one card per page
    }

    private fun displayImage(file: File, imageView: ImageView) {
        val bm = BitmapFactory.decodeFile(file.absolutePath)
        imageView.setImageBitmap(bm).toString()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v("WildFyre", "bind comments view holder")
        val comment = comments[position]
//        holder.content.text = /*context!!.getString(R.string.)*/comment.text()
//        try {
//            holder.author.text = comment.author().name()
//        } catch (e: RuntimeException) {
//            holder.author.text = context!!.resources.getText(R.string.error_getting_author)
//            Log.e(TAG, "Error getting author", e)
//        }

        thread(start = true) {
            val content = comment.text()
            var author: String?
            try {
                author = comment.author().name()
            } catch (e: RuntimeException) {
                author = context!!.resources.getText(R.string.error_getting_author) as String
                Log.e(TAG, "Error getting author", e)
            }
            handler!!.post {
                holder.content.text = content
                holder.author.text = author
            }
            val url = comment.author().avatarUrl()
            if (author == "ZedNotNaught") {
                Log.e(TAG, "ZedNotNaught!!!!!")
                Log.e(TAG, url.isPresent.toString())
//                Log.e(TAG, url.get().toString())
            }
            File(context!!.cacheDir, AVATARS_DIR).mkdirs()
            val file = File(File(context!!.cacheDir, AVATARS_DIR), comment.author().id.toString())
            if (url.isPresent && file.exists().not())
                try {
                    val req = CacheRequest.get(url.get(), cache)
                    req.receive(file)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get avatar", e)
                    handler!!.post {
                        Toast.makeText(
                            context,
                            context!!.getText(R.string.error_getting_comment_avatar),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            if (url.isPresent)
                handler!!.post {
                    displayImage(file, holder.avatar)
                }
        }
    }

    fun addItem(comment: Comment) {
        comments.add(comment)
        Log.w(TAG, "adding item $comment")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        handler = parent.handler
        val cacheFile = File(context!!.cacheDir, "http-cache")
        cache = EtagCache.create(cacheFile, TEN_MB)
        val layoutInflater = LayoutInflater.from(context)
        val cardView = layoutInflater.inflate(R.layout.item_comment, parent, false)
        return ViewHolder(cardView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content: TextView = itemView.comment_content
        var avatar: ImageView = itemView.comment_avatar
        var author: TextView = itemView.comment_author
    }

}