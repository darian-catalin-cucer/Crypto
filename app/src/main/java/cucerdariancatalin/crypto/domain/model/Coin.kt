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

package cucerdariancatalin.crypto.domain.model

import cucerdariancatalin.crypto.domain.Result
import java.math.BigDecimal
import java.util.*

class Coin private constructor(
    val id: String,
    val symbol: String,
    val name: String,
    val supply: BigDecimal,
    val marketCapUsd: BigDecimal,
    val priceUsd: BigDecimal,
    val changePercent24Hr: Float,
    val image: String
) {
  companion object {
    const val EMPTY_CHANGE_PERCENT = -1f
    val EMTPY_MARKET_CAP = BigDecimal.ZERO
    val EMPTY_SUPPLY = BigDecimal.ZERO

    private const val IMAGES_ENDPOINT = "https://static.coincap.io/assets/icons/"
    private const val IMAGES_SUFFIX = "@2x.png"

    fun of(
        id: String?,
        symbol: String?,
        name: String?,
        supply: String?,
        marketCapUsd: String?,
        priceUsd: String?,
        changePercent24Hr: String?
    ): Result<Coin> {
      return Result {
        requireNotNull(id)
        require(id.isNotBlank())

        requireNotNull(symbol)
        require(symbol.isNotBlank())

        requireNotNull(name)
        require(name.isNotBlank())

        requireNotNull(priceUsd)
        require(priceUsd.isNotBlank())

        val image = "$IMAGES_ENDPOINT${symbol.lowercase(Locale.getDefault())}$IMAGES_SUFFIX"

        Coin(
            id = id,
            symbol = symbol,
            name = name,
            supply = supply?.toBigDecimal() ?: EMPTY_SUPPLY,
            marketCapUsd = marketCapUsd?.toBigDecimal() ?: EMTPY_MARKET_CAP,
            priceUsd = priceUsd.toBigDecimal(),
            changePercent24Hr = changePercent24Hr?.toFloat() ?: EMPTY_CHANGE_PERCENT,
            image = image
        )
      }
    }
  }
}
