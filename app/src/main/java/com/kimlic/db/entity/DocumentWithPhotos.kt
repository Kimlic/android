package com.kimlic.db.entity

import android.arch.persistence.room.Embedded

class DocumentWithPhotos {

    @Embedded lateinit var document: Document
}