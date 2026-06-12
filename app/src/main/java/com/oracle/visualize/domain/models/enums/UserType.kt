package com.oracle.visualize.domain.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    @SerialName("ADMIN")
    ADMIN,

    @SerialName("WRITER")
    WRITER,

    @SerialName("CONSUMER")
    CONSUMER
}
