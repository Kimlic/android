package com.kimlic.utils

enum class QuorumURL(val url: String) {

    config("https://mobile-api-dev.kimlic.com/api/config"),

    emailVerify("https://mobile-api-dev.kimlic.com/api/verifications/email"),
    emailVerifyApprove("https://mobile-api-dev.kimlic.com/api/verifications/email/approve"),

    phoneVerify("https://mobile-api-dev.kimlic.com/api/verifications/phone"),
    phoneVierifyApprove("https://mobile-api-dev.kimlic.com/api/verifications/phone/approve")
}