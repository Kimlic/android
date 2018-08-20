package com.kimlic.API

import android.os.AsyncTask

class DoAsync : AsyncTask<Runnable, Unit, Unit>() {

    override fun doInBackground(vararg params: Runnable?) {
        params[0]?.run()
    }
}