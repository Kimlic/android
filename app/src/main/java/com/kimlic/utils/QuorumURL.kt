package com.kimlic.utils

enum class QuorumURL(val url: String) {

    config("http://mobile-api-dev.kimlic.com/api/config"),

    emailVerify("http://mobile-api-dev.kimlic.com/api/verifications/email"),
    emailVerifyApprove("http://mobile-api-dev.kimlic.com/api/verifications/email/approve"),

    phoneVerify("http://mobile-api-dev.kimlic.com/api/verifications/phone"),
    phoneVierifyApprove("http://mobile-api-dev.kimlic.com/api/verifications/phone/approve")
}