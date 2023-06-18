package com.example.mynewhumblr.ui.profile_fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.models.MeResponse
import com.example.mynewhumblr.databinding.FragmentProfileBinding
import com.example.mynewhumblr.ui.MainActivity
import com.example.mynewhumblr.ui.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfileViewModel.userProfileStateFlow.launchAndCollectIn(viewLifecycleOwner) {
            userProfileViewModel.getCurrentUserProfile()
            showUserProfile(it)
        }

        binding.buttonFriends.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment2_to_friendsFragment)
        }

        binding.buttonLogout.setOnClickListener {

            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle(R.string.logout_title)
                .setPositiveButton(R.string.yes) { _, _ ->
                    userProfileViewModel.clearToken()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    dialog.create().hide()
                }
            dialog.create().show()
        }
    }

    private fun showUserProfile(userProfile: MeResponse?) {
        userProfile?.let {
            with(binding) {
                Glide
                    .with(imageViewAvatar.context)
                    .load(it.snoovatar_img)
                    .into(imageViewAvatar)
                textViewProfileUserName.text = it.name
                textViewComments.text = it.subreddit.subscribers.toString()
                textViewKarma.text = it.total_karma.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}