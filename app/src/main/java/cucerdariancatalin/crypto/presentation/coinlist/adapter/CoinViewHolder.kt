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

package cucerdariancatalin.crypto.presentation.coinlist.adapter

import androidx.recyclerview.widget.RecyclerView
import cucerdariancatalin.crypto.databinding.RecyclerViewCoinItemBinding
import cucerdariancatalin.crypto.presentation.coinlist.CoinClickListener
import cucerdariancatalin.crypto.presentation.coinlist.model.UiCoin
import cucerdariancatalin.crypto.presentation.loadImage

class CoinViewHolder(
    private val binding: RecyclerViewCoinItemBinding,
    private val listener: CoinClickListener
): RecyclerView.ViewHolder(binding.root) {

  fun bind(coin: UiCoin) {
    val (id, symbol, name, supply, marketCapUsd, priceUsd, changePercent24Hr, image) = coin

    with (binding) {
      coinImage.loadImage(image)
      coinName.text = name
      coinSymbol.text = symbol
      coinPrice.text = priceUsd
      coinSupply.text = supply
      coinMarketCap.text = marketCapUsd
      coinChange.text = changePercent24Hr
    }

    itemView.setOnClickListener { listener.onCoinClicked(id, name) }
  }
}
