package com.kimlic.documents

enum class Status(val state: String) {

    CREATED("Created"),
    PENDING("Pending"),
    VERIFIED("Verified"),
    UNVERIFIED("Unverified")
}