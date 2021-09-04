package com.mats.giveawayapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.FragmentDashboardBinding
import com.mats.giveawayapp.firestore.FirestoreClass
import com.mats.giveawayapp.models.Item
import com.mats.giveawayapp.ui.activities.ItemDetailsActivity
import com.mats.giveawayapp.ui.activities.LoginActivity
import com.mats.giveawayapp.ui.activities.SettingsActivity
import com.mats.giveawayapp.ui.adapters.DashboardItemListAdapter
import com.mats.giveawayapp.utils.Constants

class DashboardFragment : BaseFragment() {

    //private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getItemsListFromFireStore()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                if (FirebaseAuth.getInstance().currentUser != null)
                {
                    startActivity(Intent(activity, SettingsActivity::class.java))
                }
                else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successItemsListFromFireStore(dashboardItemList: ArrayList<Item>) {
        hideProgressDialog()

        if (dashboardItemList.size > 0) {
            binding.rvDashboardItems.visibility = VISIBLE
            binding.tvNoItemsFound.visibility = GONE


            binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvDashboardItems.setHasFixedSize(true)
            val adapterDashboardItems = DashboardItemListAdapter(this, requireActivity(), dashboardItemList)
            binding.rvDashboardItems.adapter = adapterDashboardItems

            adapterDashboardItems.setOnClickListener(object: DashboardItemListAdapter.OnClickListener{
                override fun onClick(position: Int, item: Item) {
                    val intent = Intent(context, ItemDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_ITEM_ID, item.item_id)
                    startActivity(intent)
                }

            })
        } else {
            binding.rvDashboardItems.visibility = GONE
            binding.tvNoItemsFound.visibility = VISIBLE
        }
    }

    private fun getItemsListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this)
    }
}