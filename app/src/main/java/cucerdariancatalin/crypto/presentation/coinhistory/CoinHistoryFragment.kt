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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar
import cucerdariancatalin.crypto.R
import cucerdariancatalin.crypto.databinding.FragmentCoinHistoryBinding
import cucerdariancatalin.crypto.domain.model.NetworkUnavailable
import cucerdariancatalin.crypto.presentation.CoinsSharedViewModel
import cucerdariancatalin.crypto.presentation.CoinsSharedViewModelFactory
import cucerdariancatalin.crypto.presentation.SharedViewEffects
import kotlinx.coroutines.flow.collect

class CoinHistoryFragment : Fragment() {

  companion object {
    const val COIN_ID = "COIN_ID"
    const val COIN_NAME = "COIN_NAME"
  }

  private val sharedViewModel: CoinsSharedViewModel by activityViewModels {
    CoinsSharedViewModelFactory
  }

  private val binding: FragmentCoinHistoryBinding get() = _binding!!
  private val viewModel: CoinHistoryFragmentViewModel by viewModels { CoinHistoryFragmentViewModelFactory }

  private var _binding: FragmentCoinHistoryBinding? = null

  private val screenTitle: String by lazy {
    val defaultTitle = getString(R.string.app_name)
    arguments?.getString(COIN_NAME, defaultTitle) ?: defaultTitle
  }

  private val coinId: String by lazy {
    requireArguments().getString(COIN_ID).orEmpty()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentCoinHistoryBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupUI()
  }

  private fun setupUI() {
    requestCoinHistory()
    setupToolbar()
    setupChartXAxis()
    setupChartYAxis()
    setupChartBackground()
    observeViewStateUpdates()
    subscribeToViewEffects()
    subscribeToSharedViewEffects()
  }

  private fun requestCoinHistory() {
    viewModel.getCoinHistory(coinId)
  }

  private fun setupToolbar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = screenTitle
      setDisplayHomeAsUpEnabled(true)
    }
  }

  private fun setupChartXAxis() {
    binding.coinHistoryChart.xAxis.isEnabled = false
  }

  private fun setupChartYAxis() {
    binding.coinHistoryChart.axisLeft.apply {
      axisMinimum = 0f

      setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
      setDrawGridLines(false)

      valueFormatter = object: ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
          return "$$value"
        }
      }
    }

    binding.coinHistoryChart.axisRight.isEnabled = false
  }

  private fun setupChartBackground() {
    binding.coinHistoryChart.apply {
      description.isEnabled = false

      setTouchEnabled(false)
      setPinchZoom(false)
      setDrawGridBackground(false)
      invalidate()
    }
  }

  private fun observeViewStateUpdates() {
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.viewState.collect {
        updateUi(it)
      }
    }
  }

  private fun updateUi(viewState: CoinHistoryFragmentViewState) {
    val (minYAxisValue, history, chartColor) = viewState

    if (history.pricesOverTime.isEmpty()) return

    val dataSet = LineDataSet(history.pricesOverTime, "Price variation over the last 24 hours.").apply {
      mode = LineDataSet.Mode.CUBIC_BEZIER
      color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
      fillDrawable = ContextCompat.getDrawable(requireContext(), chartColor)
      cubicIntensity = 0.2f

      setDrawIcons(false)
      setDrawFilled(true)
      setDrawCircles(false)
    }

    val data = LineData(dataSet)

    binding.coinHistoryChart.apply {
      isVisible = true
      animateX(250)
      axisLeft.axisMinimum = minYAxisValue
      axisLeft.axisMaximum = history.highestValue
      invalidate()
      setData(data)
      notifyDataSetChanged()
    }

    binding.loadingProgressBar.isVisible = false
  }

  private fun subscribeToViewEffects() {
    // Handle view effects
  }

  private fun subscribeToSharedViewEffects() {
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      sharedViewModel.sharedViewEffects.collect {
        when (it) {
          is SharedViewEffects.PriceVariation -> notifyOfPriceVariation(it.variation)
        }
      }
    }
  }

  private fun notifyOfPriceVariation(variation: Int){
    showSnackbar(getString(R.string.price_variation_message, variation))
  }

  private fun handleFailure(cause: Throwable) {
    binding.loadingProgressBar.isInvisible = true

    val message = when (cause) {
      is NetworkUnavailable -> getString(R.string.network_unavailable_error_message)
      else -> getString(R.string.generic_error_message)
    }

    showSnackbar(message)
  }

  private fun showSnackbar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }
}