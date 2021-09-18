package com.mats.giveawayapp.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.FragmentItemBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.ui.activities.AddItemActivity
import com.mats.giveawayapp.ui.activities.LoginActivity
import com.mats.giveawayapp.ui.adapters.ItemsListAdapter

class ItemFragment : BaseFragment() {

    //private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.actionAddItem.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null)
            {
                startActivity(Intent(activity, AddItemActivity::class.java))
            }
            else {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        getItemsListFromFireStore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successItemsListFromFireStore(itemList: ArrayList<Item>) {
        hideProgressDialog()

        if (itemList.size > 0) {
            binding.rvMyItemItems.visibility = View.VISIBLE
            binding.tvNoItemsFound.visibility = View.GONE

            binding.rvMyItemItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyItemItems.setHasFixedSize(true)
            val adapterItems = ItemsListAdapter(this, requireActivity(), itemList)
            binding.rvMyItemItems.adapter = adapterItems
        } else {
            binding.rvMyItemItems.visibility = View.GONE
            binding.tvNoItemsFound.visibility = View.VISIBLE
        }
    }

    private fun getItemsListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getItemsList(this)
    }

    fun deleteItem(itemID: String, imagesURL: ArrayList<String?>) {
        showAlertDialogToDeleteItem(itemID, imagesURL)
    }

    fun itemDeleteSuccess() {
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.msg_item_delete_success),
             Toast.LENGTH_SHORT
        ).show()

        // Get the latest item list from cloud firestore.
        getItemsListFromFireStore()
    }

    private fun showAlertDialogToDeleteItem(itemId: String, imagesURL: ArrayList<String?>) {
        val builder = AlertDialog.Builder(requireActivity())
        // set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        // set message for alert dialog
        builder.setMessage(resources.getString(R.string.msg_delete_dialog))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        // performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteItem(this, itemId, imagesURL)
            dialogInterface.dismiss()
        }

        // performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}