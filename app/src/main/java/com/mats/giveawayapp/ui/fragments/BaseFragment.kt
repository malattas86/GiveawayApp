package com.mats.giveawayapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.DialogProgressBinding

open class BaseFragment : Fragment() {

    private lateinit var binding: DialogProgressBinding
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(requireActivity())

        /* Set the screen content from a layout resource.
        the resource will be inflated. adding all top-level views to the screen.
         */
        binding = DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(binding.root)
        binding.tvProgressText.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        // Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}