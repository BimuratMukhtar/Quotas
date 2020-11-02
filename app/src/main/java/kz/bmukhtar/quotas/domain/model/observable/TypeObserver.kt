package kz.bmukhtar.quotas.domain.model.observable

interface TypedObserver<T : Any?> {

    fun update(data: T)
}

fun <T : Any?> newTypedObserver(onUpdate: (T) -> Unit) = object : TypedObserver<T> {

    override fun update(data: T) {
        onUpdate(data)
    }
}
