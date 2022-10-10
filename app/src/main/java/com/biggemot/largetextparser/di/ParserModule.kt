package com.biggemot.largetextparser.di

import com.biggemot.largetextparser.data.ParserRepoImpl
import com.biggemot.largetextparser.domain.ParserInteractor
import com.biggemot.largetextparser.domain.ParserInteractorImpl
import com.biggemot.largetextparser.domain.ParserRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ParserModule {

    @Binds
    abstract fun bindParserInteractor(
        parserInteractorImpl: ParserInteractorImpl
    ): ParserInteractor

    @Binds
    abstract fun bindParserRepo(
        parserRepoImpl: ParserRepoImpl
    ): ParserRepo
}