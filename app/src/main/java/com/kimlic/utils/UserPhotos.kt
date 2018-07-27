package com.kimlic.utils

enum class UserPhotos(val fileName: String) {

    stagePortrait("portrait.jpg"),

    portraitFilePath("PORTRAIT_FILE_PATH"),
    frontFilePath("FRONT_FILE_PATH"),
    backFilePath("BACK_FILE_PATH"),

    passportPortrait("passportPortrait.jpg"),
    passportFrontSide("passportFront.jpg"),
    passportBackSide("passportBack.jpg"),

    driverLicensePortrait("licensePortait.jpg"),
    driverLicensFrontSide("licenseFront.jpg"),
    driverLicensBackSide("licenseBack.jpg"),

    IDCardPortrait("ldPortrait.jpg"),
    IDCardFrontSide("ldFront.jpg"),
    IDCardBackSide("ldBack.jpg"),

    PermitPortrait("permitPortrait.jpg"),
    PermitFrontSide("permitFront.jpg"),
    PermitBackSide("permitBack.jpg"),


    bill("bill.jpg"),

    default("default.jpg"),
}