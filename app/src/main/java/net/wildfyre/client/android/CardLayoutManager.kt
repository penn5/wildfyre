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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kevinsawicki.etag.CacheRequest
import com.github.kevinsawicki.etag.EtagCache
import kotlinx.android.synthetic.main.wildcard.view.*
import net.wildfyre.posts.Comment
import net.wildfyre.posts.Post
import java.io.File
import kotlin.concurrent.thread

class CardAdapter : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    @Suppress("PrivatePropertyName")
    private val TAG = "WildFyreCardAdapter"
    @Suppress("PrivatePropertyName")
    private val AVATARS_DIR = "avatars"

    private var context: Context? = null
    private var handler: Handler? = null
    private var cache: EtagCache? = null
    var post: Post? = null

    override fun getItemCount(): Int {
        return 1 //We only have one card per page
    }


    private fun displayImage(file: File, imageView: ImageView) {
        val bm = BitmapFactory.decodeFile(file.absolutePath)
        Log.e(TAG, imageView.setImageBitmap(bm).toString())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        assert(position == 0)
        Log.v(TAG, "bind view holder")
        holder.comments.adapter = CommentsAdapter()
        holder.comments.layoutManager = LinearLayoutManager(context)
        Log.v(TAG, "connected linearlayoutmanager")
        thread(start = true) {
            post = Post(/*41934846545*/77586922757, "fun")
            post!!.update()

            // First load post critical info...

            handler!!.post {
                holder.text.text = post!!.text()
                holder.author.text =
                    if (post!!.author().isPresent) post!!.author().get().name() else context!!.getText(R.string.anonymous_author)
            }

            // Then load comments...

            val comments: List<Comment> = post!!.commentsList()
            Log.d(TAG, "got comments $comments!")

            var i = 0
            for (comment: Comment in comments) {
                Log.v(TAG, "adding comment $comment")
                (holder.comments.adapter as CommentsAdapter).addItem(comment)
                handler!!.post {
                    (holder.comments.adapter as CommentsAdapter).notifyItemChanged(i++)
                }
            }
            handler!!.post {
                (holder.comments.adapter as CommentsAdapter).notifyDataSetChanged() // Just make sure, probably not needed
            }


            // And finally load the photo for the post


            if (post!!.author().isPresent) {
                val url = post!!.author().get().avatarUrl()
                File(context!!.cacheDir, AVATARS_DIR).mkdirs()
                val file = File(File(context!!.cacheDir, AVATARS_DIR), post!!.author().get().id.toString())
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
            } else {
                Log.w(TAG, "anonymous image NI")
                // TODO: display "anonymous" image
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        handler = parent.handler
        val cacheFile = File(context!!.cacheDir, "http-cache")
        cache = EtagCache.create(cacheFile, EtagCache.TEN_MB)
        val layoutInflater = LayoutInflater.from(context)
        val cardView = layoutInflater.inflate(R.layout.wildcard, parent, false)
        return ViewHolder(cardView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.post_content
        var author: TextView = itemView.post_author
        var avatar: ImageView = itemView.post_avatar
        var comments: RecyclerView = itemView.comments
    }
}