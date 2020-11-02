package kz.bmukhtar.quotas.domain.model.observable

interface MutableTypedObservable<T : Any?> : TypedObservable<T> {

    fun notifyObservers(data: T)
}

interface TypedObservable<T : Any?> {

    fun addObserver(observer: TypedObserver<T>)

    fun deleteObserver(observer: TypedObserver<T>)
}
