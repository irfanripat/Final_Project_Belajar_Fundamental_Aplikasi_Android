package com.irfan.githubuser.fragment.followers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.irfan.githubuser.activity.detail.DetailActivity
import com.irfan.githubuser.activity.main.adapter.GithubAdapter
import com.irfan.githubuser.databinding.FragmentFollowersBinding
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.Commons.hide
import com.irfan.githubuser.util.Commons.show

class FollowersFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentFollowersBinding
    private lateinit var followersViewModel: FollowersViewModel
    private var username = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFollowersBinding.inflate(inflater, container, false)
        followersViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FollowersViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val data = requireActivity().intent.getParcelableExtra(DetailActivity.EXTRA_GITHUB_USER)?:DetailUser()
        username = data.login
        getData(username)
        updateIfGetDataHasFinished()
        binding.layoutError.btnRefresh.setOnClickListener(this)
    }

    private fun getData(username: String) {
        showShimmer()
        followersViewModel.getListFollower(username).observe(viewLifecycleOwner,  {
            val githubAdapter = GithubAdapter(it){}
            hideShimmer()
            if (it.isNullOrEmpty()) {
                binding.layoutNoData.root.show()
            } else {
                binding.layoutNoData.root.hide()
            }
            binding.recyclerView.adapter = githubAdapter
        })
    }

    private fun updateIfGetDataHasFinished() {
        followersViewModel.isSuccess.observe(viewLifecycleOwner, {isSuccess ->
            when(isSuccess) {
                0 -> {
                    binding.layoutError.root.show()
                    binding.shimmerLayout.hide()
                }
            }
        })
    }

    private fun showShimmer() {
        binding.layoutError.root.hide()
        binding.layoutNoData.root.hide()
        binding.shimmerLayout.apply {
            startShimmer()
            show()
        }
    }

    private fun hideShimmer() {
        binding.shimmerLayout.apply {
            hide()
            stopShimmer()
        }
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.layoutError.btnRefresh -> getData(username)
        }
    }
}
