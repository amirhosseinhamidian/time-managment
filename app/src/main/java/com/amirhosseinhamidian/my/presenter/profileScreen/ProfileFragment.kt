package com.amirhosseinhamidian.my.presenter.profileScreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.presenter.addTargetScreed.AddTargetActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        llAddTarget.setOnClickListener {
            val intent = Intent(requireContext(),AddTargetActivity::class.java)
            requireContext().startActivity(intent)
        }
    }

}