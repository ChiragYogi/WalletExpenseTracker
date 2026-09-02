package com.babacode.walletexpensetracker.utiles

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.recoverWithDefault(default: T): Flow<T> = catch { exception ->
    FirebaseCrashlytics.getInstance().recordException(exception)
    emit(default)
}
