package no.nav.tms.eventtestproducer.innboks

import kotlinx.serialization.Serializable

@Serializable
class ProduceInnboksDto(val tekst: String,
                        val link: String,
                        val grupperingsid: String,
                        val eksternVarsling: Boolean = false,
                        val prefererteKanaler: List<String> = emptyList(),
                        val epostVarslingstekst: String? = null,
                        val epostVarslingstittel: String? = null,
                        val smsVarslingstekst: String? = null
) {
    override fun toString(): String {
        return "ProduceInnboksDto{tekst='$tekst', link='$link', grupperingsid='$grupperingsid, eksternVarsling='$eksternVarsling', prefererteKanaler='$prefererteKanaler', epostVarslingstekst='$epostVarslingstekst', epostVarslingstittel='$epostVarslingstittel', smsVarslingstekst='$smsVarslingstekst'}"
    }
}
