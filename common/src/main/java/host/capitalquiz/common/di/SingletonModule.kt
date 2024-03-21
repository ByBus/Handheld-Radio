package host.capitalquiz.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import host.capitalquiz.common.presentation.ResourceProvider

@Module
@InstallIn(SingletonComponent::class)
interface SingletonModule {
    @Binds
    fun bindStringResourceProvider(impl: ResourceProvider.StringProvider): ResourceProvider<String>
}