package com.kimlic.utils

enum class KimlicApi(val path: String) {

    QUORUM("/api/quorum"),
    CONFIG("/api/config"),

    PROFILE_SYNC("/api/sync"),

    EMAIL_VERIFY("/api/verifications/email"),
    EMAIL_APPROVE("/api/verifications/email/approve"),

    PHONE_VERIFY("/api/verifications/phone"),
    PHONE_APPROVE("/api/verifications/phone/approve"),

    VENDORS("/api/vendors")
}