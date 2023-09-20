package com.zireddinismayilov.languagetranslationapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.zireddinismayilov.languagetranslationapp.R
import com.zireddinismayilov.languagetranslationapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUpSpinners()
        binding.translateBtn.setOnClickListener {
            translateText2langs()
        }
        return binding.root
    }

    private fun setUpSpinners() {
        val translateFrom = binding.spinnerTranslateFrom
        val translateTo = binding.spinnerTranslateTo
        val languages = resources.getStringArray(R.array.Languages)

        translateFrom.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, languages
        )
        translateFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectemItem = p0?.getItemAtPosition(p2).toString()
                binding.translateFromLanguageTV.text = selectemItem
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        translateTo.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)

        translateTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectemItem = p0?.getItemAtPosition(p2).toString()
                binding.translateToLanguageTV.text = selectemItem
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun translateText2langs() {
        val translateFrom = binding.spinnerTranslateFrom
        val translateTo = binding.spinnerTranslateTo

        val options =
            TranslatorOptions.Builder().setSourceLanguage(translateFrom.selectedItem.toString())
                .setTargetLanguage(translateTo.selectedItem.toString()).build()

        Log.d("translate from : ", translateFrom.selectedItem.toString())
        Log.d("translate from : ", translateTo.selectedItem.toString())

        val translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder().requireWifi().build()
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            translator.translate(binding.translateFromEditText.text.toString())
                .addOnSuccessListener { translatedText ->
                    binding.translatedTextEditText.setText(translatedText.toString())
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(), "Failed to translate!", Toast.LENGTH_SHORT
                    ).show()
                }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                requireContext(), "Failed to download language model!", Toast.LENGTH_SHORT
            ).show()
        }

    }
}