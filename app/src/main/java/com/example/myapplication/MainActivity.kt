package com.example.myapplication

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        private const val API = "https://api.themoviedb.org/3/movie/popular?api_key=38c715feda33c530131692821f9a617a"
    }
    var movieList: MutableList<Movies>? = null
    var recyclerView: RecyclerView? = null

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    abstract inner class GetData : AsyncTask<String?, String?, String>() {
        protected fun doInBackground(vararg strings: String): String {
            var current = ""
            try {
                val url: URL
                var urlConnection: HttpURLConnection? = null
                return try {
                    url = URL(API)
                    urlConnection = url.openConnection() as HttpURLConnection
                    val `is` = urlConnection.inputStream
                    val isr = InputStreamReader(`is`)
                    var data = isr.read()
                    while (data != -1) {
                        current += data.toChar()
                        data = isr.read()
                    }
                    current
                } catch (e: MalformedURLException) {
                    throw RuntimeException(e)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                } finally {
                    urlConnection?.disconnect()
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
            return current
        }

        override fun onPostExecute(s: String) {
            try {
                val jsonObject = JSONObject(s)
                val jsonArray = jsonObject.getJSONArray("moviz")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject1 = jsonArray.getJSONObject(i)
                    val model = Movies()
                    model.setId(jsonObject1.getString("vote_average"))
                    model.setName(jsonObject1.getString("title"))
                    model.setImg(jsonObject1.getString("poster_path"))
                    movieList!!.add(model)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            PutDataIntoRecyclerView(movieList)
        }
    }

    private fun PutDataIntoRecyclerView(movieList: List<Movies>?) {
        val adapter = Adapter(this, movieList)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
    }

}