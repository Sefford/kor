package com.sefford.kor.repositories.utils

sealed class RepositoryError {
    data class NotFound<K>(val id: K) : RepositoryError()
    data class CannotPersist(val throwable: Throwable) : RepositoryError()
    data class CannotRetrieve(val throwable: Throwable) : RepositoryError()
    object NotReady : RepositoryError()
}

