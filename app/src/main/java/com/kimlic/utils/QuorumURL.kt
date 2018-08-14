package com.kimlic.utils

enum class QuorumURL(val url: String) {


    quorum("https://mobile-api-test.kimlic.com/api/quorum"),
    config("https://mobile-api-test.kimlic.com/api/config"),

    profileSync("https://mobile-api-test.kimlic.com/api/sync"),

    emailVerify("https://mobile-api-test.kimlic.com/api/verifications/email"),
    emailVerifyApprove("https://mobile-api-test.kimlic.com/api/verifications/email/approve"),

    phoneVerify("https://mobile-api-test.kimlic.com/api/verifications/phone"),
    phoneVierifyApprove("https://mobile-api-test.kimlic.com/api/verifications/phone/approve"),

    vendors("https://dd2121ab.ngrok.io/api/vendors"),

}