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

package cucerdariancatalin.crypto.data.repositories

import cucerdariancatalin.crypto.domain.Result
import cucerdariancatalin.crypto.domain.repositories.CoinsRepository
import cucerdariancatalin.crypto.domain.requireValue
import cucerdariancatalin.crypto.utils.DispatchersProvider
import cucerdariancatalin.crypto.data.api.CoinCapApi
import cucerdariancatalin.crypto.data.mappers.CoinMapper
import cucerdariancatalin.crypto.data.mappers.HistoryMapper
import kotlinx.coroutines.withContext
import java.time.Instant

class CoinCapCoinsRepository(
    private val dispatchersProvider: DispatchersProvider,
    private val coinMapper: CoinMapper,
    private val historyMapper: HistoryMapper,
    private val coinCapApi: CoinCapApi
) : CoinsRepository {
  override suspend fun getCoins() = withContext(dispatchersProvider.io()) {
    Result {
      coinCapApi.getCoins().data
          .orEmpty()
          .map { coinMapper.toDomain(it) }
          .map { it.requireValue() }
    }
  }

  override suspend fun getCoinHistory(coinId: String) = withContext(dispatchersProvider.io()) {
        Result {
          val dayInSeconds = 86400L
          val now = Instant.now()
          val aDayAgo = now.minusSeconds(dayInSeconds)
          val response = coinCapApi.getCoinHistory(
              coinId = coinId,
              start = aDayAgo.toEpochMilli(),
              end = now.toEpochMilli()
          ).data.orEmpty()

          historyMapper
              .toDomain(response)
              .requireValue()
        }
      }
}