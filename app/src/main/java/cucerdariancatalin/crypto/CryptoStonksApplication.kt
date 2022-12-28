package cucerdariancatalin.crypto

import android.app.Application
import cucerdariancatalin.crypto.domain.repositories.CoinsRepository
import cucerdariancatalin.crypto.utils.DefaultDispatchersProvider
import cucerdariancatalin.crypto.data.api.CoinCapApi
import cucerdariancatalin.crypto.data.mappers.CoinMapper
import cucerdariancatalin.crypto.data.mappers.HistoryMapper
import cucerdariancatalin.crypto.data.repositories.CoinCapCoinsRepository
import cucerdariancatalin.crypto.presentation.CoinsSharedViewModelFactory
import cucerdariancatalin.crypto.presentation.coinhistory.CoinHistoryFragmentViewModelFactory
import cucerdariancatalin.crypto.presentation.coinlist.CoinListFragmentViewModelFactory

class CryptoStonksApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val repository = createRepository()

        CoinsSharedViewModelFactory.inject(repository)
        CoinListFragmentViewModelFactory.inject(repository)
        CoinHistoryFragmentViewModelFactory.inject(repository)
    }

    private fun createRepository(): CoinsRepository {
        return CoinCapCoinsRepository(
            DefaultDispatchersProvider(),
            CoinMapper(),
            HistoryMapper(),
            CoinCapApi.create(this)
        )
    }
}