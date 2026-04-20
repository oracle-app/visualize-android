package com.oracle.visualize.di

import com.oracle.visualize.data.repositories.TeamRepositoryImpl
import com.oracle.visualize.domain.repositories.TeamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTeamRepository(teamRepositoryImpl: TeamRepositoryImpl): TeamRepository
}