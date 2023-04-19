package com.calberto.chatgptkotlin


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.calberto.chatgptkotlin.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import org.json.JSONObject
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // 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
    private lateinit var messagesList: MessagesList

    private lateinit var user:User
    private lateinit var chatgpt:User

    private lateinit var adapter : MessagesListAdapter<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messagesList = binding.messagesList

        var imageLoader : ImageLoader = object : ImageLoader{
            override fun loadImage(imageView: ImageView?, url: String?, payload: Any?) {
                Picasso.get().load(url).into(imageView)
            }

        }

        adapter = MessagesListAdapter<Message>("1", imageLoader)
        messagesList.setAdapter(adapter)

        user = User("1", "Carlos", "")
        chatgpt = User("2", "ChatGPT", "")
    }

    fun buttonMessage(view: View) {
        var message : Message = Message("n1", binding.editTextMessage.text.toString(), user, Calendar.getInstance().time, "" )
        adapter.addToStart(message, true)
        //generateImages(binding.editTextMessage.text.toString())

        if(binding.editTextMessage.text.toString().startsWith("generate image")){
            generateImages(binding.editTextMessage.text.toString())
        } else {
            performAction(binding.editTextMessage.text.toString())
        }
        binding.editTextMessage.text.clear()
    }


    fun performAction( input: String){
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openai.com/v1/completions"

        val jsonObject = JSONObject()
        jsonObject.put("prompt", input)
        jsonObject.put("model", "text-davinci-003")
        jsonObject.put("temperature", 0)
        jsonObject.put("max_tokens", 4000)

        // Request a string response from the provided URL.
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
            Response.Listener<JSONObject> { response ->
                // Display the response string.
                val choicesArray = response.getJSONArray("choices")
                if(choicesArray.length() > 0){
                    val answer = choicesArray.getJSONObject(0).getString("text")
                    //binding.textMessage.text = answer

                    var message : Message = Message("n2", answer.trim(), chatgpt, Calendar.getInstance().time, "" )
                    adapter.addToStart(message, true)
                }
                else {
                    Toast.makeText(this, "Error: empty response", Toast.LENGTH_LONG ).show()
                }
            },
            Response.ErrorListener { error ->
                // Display the error message.
                val errorMessage = error.message ?: "Error: null"
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG ).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer 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"
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            15,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    fun generateImages( input: String){
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openai.com/v1/images/generations"

        val jsonObject = JSONObject()
        jsonObject.put("prompt", input)
        jsonObject.put("n", 3)
        jsonObject.put("size", "1024x1024")

        // Request a string response from the provided URL.
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
            Response.Listener<JSONObject> { response ->
                // Display the response string.
                val choicesArray = response.getJSONArray("data")
                if(choicesArray.length() > 0){
                    for (i in 0..choicesArray.length() -1){
                        val answer = choicesArray.getJSONObject(i).getString("url")

                        var message : Message = Message("n2", "image", chatgpt, Calendar.getInstance().time, answer )
                        adapter.addToStart(message, true)
                    }
                }
                else {
                    Toast.makeText(this, "Error: empty response", Toast.LENGTH_LONG ).show()
                }
            },
            Response.ErrorListener { error ->
                // Display the error message.
                val errorMessage = error.message ?: "Error: null"
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG ).show()
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer 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"
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            60000,
            15,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }
}

