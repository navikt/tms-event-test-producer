package no.nav.tms.eventtestproducer.common.kafka

data class RecordKeyValueWrapper <K, V> (
    val key: K,
    val value: V
)
