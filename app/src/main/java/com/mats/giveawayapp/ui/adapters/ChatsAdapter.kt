package com.mats.giveawayapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.mats.giveawayapp.R
import com.mats.giveawayapp.models.Chat
import com.mats.giveawayapp.ui.activities.ViewFullImageActivity
import com.mats.giveawayapp.utils.Constants
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    private val mContext: Context,
    private val mChatList: List<Chat>,
    private val imageUrl: String
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder?>()
{
    private var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder
    {
        return if (position == 1)
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }
        else
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profileImage)

        //images Messages
        if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals(""))
        {
            //image message - right side
            if (chat.getSender().equals(firebaseUser.uid))
            {
                holder.showTextMessage!!.visibility = View.GONE
                holder.rightImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.rightImageView)

                holder.rightImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options) { _, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra(Constants.EXTRA_IMAGE_URL, chat.getUrl())
                            mContext.startActivity(intent)
                        } else if (which == 1) {
                            deleteSentMessage(position, holder)
                        }
                    }
                    builder.show()
                }
            }
            //image message - left side
            else if (!chat.getSender().equals(firebaseUser.uid))
            {
                holder.showTextMessage!!.visibility = View.GONE
                holder.leftImageView!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.leftImageView)

                holder.leftImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options) { _, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        }
                    }
                    builder.show()
                }
            }
        }
        //text Messages
        else
        {
            holder.showTextMessage!!.text = chat.getMessage()

            if (firebaseUser.uid == chat.getSender())
            {
                holder.showTextMessage!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message",
                        "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")

                    builder.setItems(options) { _, which ->
                        if (which == 0) {
                            deleteSentMessage(position, holder)
                        }
                    }
                    builder.show()
                }
            }
        }

        //sent and seen message
        if (position == mChatList.size-1)
        {
            if (chat.isIsSeen())
            {
                holder.textSeen!!.text = "Seen"

                if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            }
            else
            {
                holder.textSeen!!.text = "Sent"

                if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            }
        }
        else
        {
            holder.textSeen!!.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView? = null
        var showTextMessage: TextView? = null
        var leftImageView: ImageView? = null
        var textSeen: TextView? = null
        var rightImageView: ImageView? = null

        init {
            profileImage = itemView.findViewById(R.id.profile_image)
            showTextMessage = itemView.findViewById(R.id.show_text_message)
            leftImageView = itemView.findViewById(R.id.left_image_view)
            textSeen = itemView.findViewById(R.id.text_seen)
            rightImageView = itemView.findViewById(R.id.right_image_view)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if (mChatList[position].getSender().equals(firebaseUser.uid))
        {
            1
        }
        else
        {
            0
        }
    }


    private fun deleteSentMessage(position: Int, holder: ChatsAdapter.ViewHolder)
    {
        FirebaseDatabase
            .getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
            .child("Chats")
            .child(mChatList[position].getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(holder.itemView.context, "Deleted.", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(holder.itemView.context, "Failed, Not Deleted.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}