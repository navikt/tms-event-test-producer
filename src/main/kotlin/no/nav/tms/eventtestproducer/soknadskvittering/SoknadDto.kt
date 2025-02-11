package no.nav.tms.eventtestproducer.soknadskvittering
import java.time.LocalDate

object SoknadRequest {
    data class Opprett(
        val tittel: String,
        val temakode: String = "UKJ",
        val skjemanummer: String = "Skjema-123",
        val fristEttersending: LocalDate = LocalDate.now().plusDays(14),
        val linkSoknad: String? = null,
        val journalpostId: String? = null,
        val mottatteVedlegg: List<MottattVedlegg> = defaultMottatt,
        val etterspurteVedlegg: List<EtterspurtVedlegg> = defaultEtterspurt,
    ) {
        class MottattVedlegg(
            val vedleggsId: String,
            val tittel: String,
            val linkVedlegg: String
        )

        class EtterspurtVedlegg(
            val vedleggsId: String,
            val brukerErAvsender: Boolean,
            val tittel: String,
            val beskrivelse: String?,
            val linkEttersending: String?
        )

        companion object {
            private val defaultMottatt = listOf(
                MottattVedlegg(
                    vedleggsId = "vedlegg-1",
                    tittel = "Vedlegg mottatt når søknad ble sendt inn",
                    linkVedlegg = "https://www.ansatt.nav.no"
                )
            )

            private val defaultEtterspurt = listOf(
                EtterspurtVedlegg(
                    vedleggsId = "vedlegg-2",
                    brukerErAvsender = true,
                    tittel = "Vedlegg som bruker må sende inn",
                    beskrivelse = null,
                    linkEttersending = "https://www.ansatt.nav.no",
                ),
                EtterspurtVedlegg(
                    vedleggsId = "vedlegg-3",
                    brukerErAvsender = false,
                    tittel = "Vedlegg som bruker sier tredjepart sender inn",
                    beskrivelse = "Eventuell detaljert beskrivelse av vedlegg som er etterspurt",
                    null
                ),
                EtterspurtVedlegg(
                    vedleggsId = "vedlegg-4",
                    brukerErAvsender = false,
                    tittel = "Annet vedlegg som bruker sier tredjepart sender inn",
                    beskrivelse = null,
                    null
                ),
            )
        }
    }

    data class Oppdater(
        val soknadsId: String,
        val fristEttersending: LocalDate? = null,
        val linkSoknad: String? = null,
        val journalpostId: String? = null
    )

    data class Ferdigstill(
        val soknadsId: String
    )

    data class EttersporrVedlegg(
        val soknadsId: String,
        val vedleggsId: String,
        val brukerErAvsender: Boolean,
        val tittel: String,
        val linkEttersending: String? = null,
        val beskrivelse: String? = null
    )
    data class MottaVedlegg(
        val soknadsId: String,
        val vedleggsId: String,
        val tittel: String,
        val brukerErAvsender: Boolean,
        val linkVedlegg: String? = null
    )
}
