import default.*

object Avro: DependencyGroup {
    override val groupId get() = "io.confluent"
    override val version get() = "6.2.1"

    val avroSerializer get() = dependency("kafka-avro-serializer")
    val schemaRegistry get() = dependency("kafka-schema-registry")
}

object Brukernotifikasjon: DependencyGroup {
    override val groupId get() = "com.github.navikt"
    override val version get() = "1.2022.06.10-12.30-e99cac7ce3e2"

    val schemas get() = dependency("brukernotifikasjon-schemas")
}

object Utkast {
    val builder = "com.github.navikt:tms-utkast:20230203100430-ecf5208"
}

object NAV{
    val vaultJdbc get() = "no.nav:vault-jdbc:1.3.7"
    val kafkaEmbedded get() = "no.nav:kafka-embedded-env:2.8.1"
    val tokenValidatorKtor get() = "no.nav.security:token-validation-ktor:1.3.10"
    val tokenValidatorKtor2 get() = "no.nav.security:token-validation-ktor-v2:2.1.4"
}

object SulkyUlid: DependencyGroup {
    override val version get() = "8.2.0"
    override val groupId get() = "de.huxhorn.sulky"

    val sulkyUlid get() = dependency("de.huxhorn.sulky.ulid")
}
