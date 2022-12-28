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

package cucerdariancatalin.crypto.presentation.coinhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cucerdariancatalin.crypto.R
import cucerdariancatalin.crypto.domain.Result
import cucerdariancatalin.crypto.domain.model.CoinHistory
import cucerdariancatalin.crypto.domain.repositories.CoinsRepository
import cucerdariancatalin.crypto.presentation.coinhistory.CoinHistoryFragmentViewState
import cucerdariancatalin.crypto.presentation.coinhistory.mappers.UiCoinHistoryMapper
import cucerdariancatalin.crypto.presentation.coinlist.CoinListFragmentViewEffects
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CoinHistoryFragmentViewModel(
    private val coinsRepository: CoinsRepository,
    private val uiCoinHistoryMapper: UiCoinHistoryMapper
) : ViewModel() {

  private val _viewState = MutableStateFlow(CoinHistoryFragmentViewState())

  val viewState: StateFlow<CoinHistoryFragmentViewState> = _viewState.asStateFlow()

  fun getCoinHistory(coinId: String) {
    _viewState.value = CoinHistoryFragmentViewState()

    viewModelScope.launch {
      when (val history = coinsRepository.getCoinHistory(coinId)) {
        is Result.Success -> handleCoinHistory(history.value)
        is Result.Failure -> handleFailures(history.cause)
      }
    }
  }

  private fun handleCoinHistory(history: CoinHistory) {
    val pricesOverTime = history.pricesOverTime
    val firstTimeSample = Collections.min(pricesOverTime.keys)
    val lastTimeSample = Collections.max(pricesOverTime.keys)
    val firstValue = pricesOverTime.getValue(firstTimeSample)
    val lastValue = pricesOverTime.getValue(lastTimeSample)
    val uiHistory = uiCoinHistoryMapper.toUi(history)
    val lowestValue = Collections.min(pricesOverTime.values).toFloat()
    val minYAxisValue = lowestValue - (lowestValue * 0.05f)

    val newState = if (lastValue < firstValue) {
      CoinHistoryFragmentViewState(minYAxisValue, uiHistory, R.drawable.chart_gradient_price_decrease)
    } else {
      CoinHistoryFragmentViewState(minYAxisValue, uiHistory)
    }

    _viewState.value = newState
  }

  private suspend fun handleFailures(cause: Throwable) {
    // Handle failures
  }
}

