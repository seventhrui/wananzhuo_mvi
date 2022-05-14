package com.seventh.demo.vo

import java.io.Serializable

data class AppVersionVO(
    var version_code: Int,
    var version_name: String,
    var ismust: Int,
    var url: String,
    var desc: String,
    var updatetime: String,
    var state: Int
): Serializable
