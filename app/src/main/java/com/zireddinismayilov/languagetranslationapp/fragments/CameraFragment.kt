package com.zireddinismayilov.languagetranslationapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zireddinismayilov.languagetranslationapp.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {
    lateinit var binding: FragmentCameraBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }
}