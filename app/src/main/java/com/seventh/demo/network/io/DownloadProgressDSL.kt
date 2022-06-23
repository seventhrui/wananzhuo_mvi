package com.seventh.demo.network.io

class DownloadProgressDSL {
    internal var before: (() -> Unit)? = null
        private set
    internal var start: (() -> Unit)? = null
        private set
    internal var progress: ((progress: Int) -> Unit)? = null
        private set
    internal var finish: ((localPath: String?) -> Unit)? = null
        private set
    internal var failure: ((errorInfo: String) -> Unit)? = null
        private set

    fun onBefore(block: () -> Unit) {
        this.before = block
    }
    fun onStart(block: () -> Unit) {
        this.start = block
    }
    fun onProgress(block: (progress: Int) -> Unit) {
        this.progress = block
    }
    fun onFinish(block: (localPath: String?) -> Unit) {
        this.finish = block
    }
    fun onFailure(block: (errorInfo: String) -> Unit) {
        this.failure = block
    }


}