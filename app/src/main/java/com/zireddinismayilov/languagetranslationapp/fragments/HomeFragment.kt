package com.zireddinismayilov.languagetranslationapp.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.zireddinismayilov.languagetranslationapp.Constants.langsTags
import com.zireddinismayilov.languagetranslationapp.R
import com.zireddinismayilov.languagetranslationapp.databinding.FragmentHomeBinding
import java.util.Locale

class HomeFragment : Fragment(), TextToSpeech.OnInitListener, RecognitionListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var translator: Translator
    private lateinit var speechRecognizer: SpeechRecognizer
    private val speechRecognizerRequestCode = 213
    private val conditions = DownloadConditions.Builder().requireWifi().build()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        textToSpeech = TextToSpeech(requireContext(), this)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(this)

        setUpSpinners()
        setUpTranslateBtn()
        requireMicPermission()
        setUpSpeechRecognition()
        setUpClearTextBtn()
        changeLanguages()
        setSoundClickListeners()

        return binding.root
    }


    private fun requireMicPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                speechRecognizerRequestCode
            )
        }
    }

    private fun setUpClearTextBtn() {
        binding.clearTextBtn.setOnClickListener {
            binding.translateFromEditText.text.clear()
            binding.translatedTV.text = ""
        }
    }

    private fun setUpTranslateBtn() {
        binding.translateBtn.setOnClickListener {
            translateText2langs()
        }
    }

    private fun changeLanguages() {
        val translateFrom = binding.spinnerTranslateFrom
        val translateTo = binding.spinnerTranslateTo

        binding.changeLangs.setOnClickListener {
            binding.translateFromLanguageTV.text = translateTo.selectedItem.toString()
            binding.translateToLanguageTV.text = translateFrom.selectedItem.toString()
            val translateFromSelectedItem = translateFrom.selectedItemPosition
            translateFrom.setSelection(translateTo.selectedItemPosition)
            translateTo.setSelection(translateFromSelectedItem)
        }
    }

    private fun LanguageToTag(language: String): String {
        return langsTags[language] ?: "Unknown"
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
                binding.translateFromEditText.text.clear()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        translateTo.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)

        translateTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectemItem = p0?.getItemAtPosition(p2).toString()
                binding.translateToLanguageTV.text = selectemItem
                binding.translatedTV.text = ""
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun translateText2langs() {
        val translateFrom = binding.spinnerTranslateFrom
        val translateTo = binding.spinnerTranslateTo

        val options = TranslatorOptions.Builder().setSourceLanguage(
            TranslateLanguage.fromLanguageTag(LanguageToTag(translateFrom.selectedItem.toString()))!!
        ).setTargetLanguage(
            TranslateLanguage.fromLanguageTag(LanguageToTag(translateTo.selectedItem.toString()))!!
        ).build()

        translator = Translation.getClient(options)

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            translator.translate(binding.translateFromEditText.text.toString())
                .addOnSuccessListener { translatedText ->
                    Log.d("translated text", translatedText)
                    binding.translatedTV.text = translatedText.toString()
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

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(
                requireContext(), "Text-to-speech initialization failed!", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun speakText(text: String, languageTag: String) {
        val result = textToSpeech.setLanguage(Locale.forLanguageTag(languageTag))
        if (result == TextToSpeech.LANG_AVAILABLE) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Toast.makeText(
                requireContext(), "Language not supported!", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setSoundClickListeners() {
        val srcLangSound = binding.srcLangSound
        val targetLangSound = binding.targetLangSound

        srcLangSound.setOnClickListener {
            val text = binding.translateFromEditText.text.toString().lowercase().trim()
            val selectedSrcLanguageTag =
                LanguageToTag(binding.translateFromLanguageTV.text.toString())
            speakText(text, selectedSrcLanguageTag)
        }

        targetLangSound.setOnClickListener {
            val text = binding.translatedTV.text.toString().lowercase().trim()
            val selectedTargetLanguageTag =
                LanguageToTag(binding.translateToLanguageTV.text.toString())
            speakText(text, selectedTargetLanguageTag)
        }
    }

    private fun setUpSpeechRecognition() {
        val recognizeIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizeIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        binding.microphone.setOnClickListener {
            recognizeIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.forLanguageTag(LanguageToTag(binding.spinnerTranslateFrom.selectedItem.toString()))
            )
            try {
                speechRecognizer.startListening(recognizeIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d("SpeechRecognition", "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        binding.translateFromEditText.setText("Starting...")
        Log.d("SpeechRecognition", "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        binding.translateFromEditText.setText("Listening...")
        Log.d("SpeechRecognition", "onRmsChanged")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d("SpeechRecognition", "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        binding.translateFromEditText.setText("Analyzing...")
        Log.d("SpeechRecognition", "onEndOfSpeech")
    }

    override fun onError(p0: Int) {
        binding.translateFromEditText.text.clear()
        Toast.makeText(
            requireContext(), "Speech recognition error. Please try again.", Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResults(results: Bundle?) {
        Log.d("SpeechRecognition", "onResults")
        val textFromSpeech = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!textFromSpeech.isNullOrEmpty()) {
            binding.translateFromEditText.setText(textFromSpeech[0])
        }
    }

    override fun onPartialResults(p0: Bundle?) {}
    override fun onEvent(p0: Int, p1: Bundle?) {}

    override fun onDestroy() {
        super.onDestroy()
        translator.close()
        speechRecognizer.destroy()
    }
}