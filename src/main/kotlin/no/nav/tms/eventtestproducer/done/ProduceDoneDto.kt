package no.nav.tms.eventtestproducer.done

import kotlinx.serialization.Serializable

@Serializable
class ProduceDoneDto(val eventId: String) {
    override fun toString(): String {
        return "ProduceDoneDto{eventId='$eventId'}"
    }

}
