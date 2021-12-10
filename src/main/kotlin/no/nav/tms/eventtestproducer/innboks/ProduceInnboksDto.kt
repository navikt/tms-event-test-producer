package no.nav.tms.eventtestproducer.innboks

import kotlinx.serialization.Serializable

@Serializable
class ProduceInnboksDto(val tekst: String,
                        val link: String,
                        val grupperingsid: String,
                        val eksternVarsling: Boolean = false,
                        val prefererteKanaler: List<String> = emptyList()) {
    override fun toString(): String {
        return "ProduceInnboksDto{tekst='$tekst', link='$link', grupperingsid='$grupperingsid, eksternVarsling='$eksternVarsling', prefererteKanaler='$prefererteKanaler'}"
    }
}
