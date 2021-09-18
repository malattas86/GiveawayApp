package com.mats.giveawayapp.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityMessageChatBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.utils.Constants

class MessageChatActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMessageChatBinding

    private var mReceiverID: String = ""
    private var mSender: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_ITEM_OWNER_ID)) {
            mReceiverID = intent.getStringExtra(Constants.EXTRA_ITEM_OWNER_ID)!!
        }
        mSender = FirestoreClass().getCurrentUserID()

        binding.ivSendBtn.setOnClickListener(this)
    }

    private fun onClickSendMessage() {
        val message = binding.etTextMessage.text.toString()
        if (validateTextMessage(message)) {
            sendMessageToUser(mSender, mReceiverID, message)
        }
        binding.etTextMessage.setText(R.string.action_add_item)
    }

    private fun sendMessageToUser(senderID: String, receiverID: String, message: String) {
        val reference = FirebaseDatabase
            .getInstance("https://giveawayapp-1caa9-default-rtdb.europe-west1.firebasedatabase.app")
            .reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderID
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverID
        messageHashMap["isSeen"] = false
        messageHashMap["ur"] = senderID
        messageHashMap["messageID"] = messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHashMap)
    }

    private fun validateTextMessage(message: String): Boolean {
        return when {
            TextUtils.isEmpty(message.trim {it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_message), true)
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v) {
                binding.ivSendBtn -> {
                    onClickSendMessage()
                }
            }
        }
    }
}