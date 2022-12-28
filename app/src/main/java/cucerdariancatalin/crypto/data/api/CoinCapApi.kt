/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cucerdariancatalin.crypto.data.api

import android.content.Context
import android.util.Log
import cucerdariancatalin.crypto.data.api.ApiConstants.ASSETS
import cucerdariancatalin.crypto.data.api.ApiConstants.BASE_ENDPOINT
import cucerdariancatalin.crypto.data.api.ApiConstants.HISTORY
import cucerdariancatalin.crypto.data.api.interceptors.NetworkStatusInterceptor
import cucerdariancatalin.crypto.data.api.model.CoinHistoryResponse
import cucerdariancatalin.crypto.data.api.model.CoinListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinCapApi {

  @GET("$BASE_ENDPOINT$ASSETS")
  suspend fun getCoins(@Query("limit") limit: Int = 50): CoinListResponse

  @GET("$BASE_ENDPOINT$ASSETS/{id}/$HISTORY")
  suspend fun getCoinHistory(
      @Path("id") coinId: String,
      @Query("interval") interval: String = "m5",
      @Query("start") start: Long,
      @Query("end") end: Long
  ): CoinHistoryResponse

  companion object {

    fun create(context: Context): CoinCapApi {
      return Retrofit.Builder()
          .baseUrl(BASE_ENDPOINT)
          .client(createOkHttpClient(context))
          .addConverterFactory(MoshiConverterFactory.create())
          .build()
          .create(CoinCapApi::class.java)
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
      return OkHttpClient.Builder()
          .addInterceptor(NetworkStatusInterceptor(ConnectionManager(context)))
          .addInterceptor(httpLoggingInterceptor)
          .build()
    }

    private val httpLoggingInterceptor: HttpLoggingInterceptor
      get() = HttpLoggingInterceptor { message ->
        Log.i("Network", message)
      }.apply {
        level = HttpLoggingInterceptor.Level.BODY
      }
  }
}