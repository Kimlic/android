package com.kimlic.documents

import android.os.Bundle

interface DocumentCallback {
    fun callback(bundle: Bundle = Bundle())
}