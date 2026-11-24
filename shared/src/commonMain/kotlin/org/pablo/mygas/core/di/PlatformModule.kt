package org.pablo.mygas.core.di

import org.koin.core.module.Module

/** Bindings that need platform types: the database builder and the [org.pablo.mygas.core.utils.MapLauncher]. */
expect val platformModule: Module
