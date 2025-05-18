package com.example.smart.ui.account.carousel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smart.R
import com.example.smart.databinding.FragmentFifthPageBinding
import org.json.JSONObject

class FifthPageFragment : Fragment() {
    private lateinit var binding: FragmentFifthPageBinding

    // Your deployed cloud function URL
    private val cloudFunctionUrl = "https://testsendstaffnotification-6gfachf5gq-uc.a.run.app"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFifthPageBinding.inflate(inflater, container, false)

        binding.apply {
            cvNext.setOnClickListener {
                findNavController().navigate(R.id.action_fifthPageFragment_to_sixthPageFragment)
            }

            tvSkip.setOnClickListener {
                Log.d("FCM_TEST", "🔘 Sending request to Cloud Function...")

                val requestQueue = Volley.newRequestQueue(requireContext())

                val jsonRequest = JsonObjectRequest(
                    Request.Method.POST,
                    cloudFunctionUrl,
                    null,  // You can also send a JSONObject body if needed
                    { response ->
                        val message = response.optString("message")
                        val success = response.optBoolean("success")
                        val tokens = response.optJSONArray("tokens")  // if returned
                        Log.d("FCM_TEST", "✅ Success: $success - $message")
                        Log.d("FCM_TEST", "📝 Tokens: $tokens")
                        Log.d("FCM_TEST", "🗃 Data: $response")
                    },
                    { error ->
                        val statusCode = error.networkResponse?.statusCode
                        Log.e("FCM_TEST", "❌ Error: HTTP $statusCode", error)
                    }
                )

                requestQueue.add(jsonRequest)
            }
        }

        return binding.root
    }
}
