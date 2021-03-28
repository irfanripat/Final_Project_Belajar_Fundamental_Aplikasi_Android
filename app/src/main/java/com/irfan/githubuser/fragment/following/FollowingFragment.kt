package com.irfan.githubuser.fragment.following

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.irfan.githubuser.activity.detail.DetailActivity
import com.irfan.githubuser.activity.main.adapter.GithubAdapter
import com.irfan.githubuser.databinding.FragmentFollowingBinding
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.Commons.hide
import com.irfan.githubuser.util.Commons.show

class FollowingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentFollowingBinding
    private lateinit var followingViewModel: FollowingViewModel
    private var username = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        followingViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FollowingViewModel::class.java)
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
        followingViewModel.getListFollowing(username).observe(viewLifecycleOwner, {
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
        followingViewModel.isSuccess.observe(viewLifecycleOwner, {isSuccess->
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