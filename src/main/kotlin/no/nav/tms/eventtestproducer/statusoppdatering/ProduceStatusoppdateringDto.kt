package no.nav.tms.eventtestproducer.statusoppdatering

import kotlinx.serialization.Serializable

@Serializable
class ProduceStatusoppdateringDto (val link: String,
                                   val statusGlobal: String,
                                   val statusIntern: String,
                                   val sakstema: String,
                                   val grupperingsid: String)
{
    override fun toString(): String {
        return "ProduceStatusoppdateringDto{link='$link', statusGlobal='$statusGlobal', statusIntern='$statusIntern', sakstema='$sakstema', grupperingsid='$grupperingsid'}"
    }
}
