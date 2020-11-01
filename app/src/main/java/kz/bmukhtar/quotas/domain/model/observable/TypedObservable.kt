package kz.bmukhtar.quotas.domain.model.observable

interface TypedObservable<T : Any?> {
    fun notifyObservers(data: T)

    fun addObserver(observer: TypedObserver<T>)

    fun deleteObserver(observer: TypedObserver<T>)
}

interface TypedObserver<T : Any?> {

    fun update(data: T)
}

fun <T : Any?> newTypedObserver(onUpdate: (T) -> Unit) = object : TypedObserver<T> {

    override fun update(data: T) {
        onUpdate(data)
    }
}
