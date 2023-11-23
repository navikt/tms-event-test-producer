import default.*

object Avro: DependencyGroup {
    override val groupId get() = "io.confluent"
    override val version get() = "6.2.1"

    val avroSerializer get() = dependency("kafka-avro-serializer")
    val schemaRegistry get() = dependency("kafka-schema-registry")
}

object Utkast: DependencyGroup {
    override val version get() = "20230203100430-ecf5208"
    override val groupId get() = "no.nav.tms.utkast"

    val builder get() = dependency("builder")
}

object SulkyUlid: DependencyGroup {
    override val version get() = "8.2.0"
    override val groupId get() = "de.huxhorn.sulky"

    val sulkyUlid get() = dependency("de.huxhorn.sulky.ulid")
}

object TmsVarselBuilder: DependencyGroup {
    override val groupId get() = "no.nav.tms.varsel"
    override val version get() = "1.0.0"

    val kotlinBuilder get() = dependency("kotlin-builder")
    val javabuilder get() = dependency("java-builder")
}
