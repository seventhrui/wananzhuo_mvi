package com.seventh.demo.extension

fun Int?.toNonNullInt() : Int{
    if(this == null){
        return 0
    }
    return this
}

fun Double?.toNonNullDouble() : Double{
    if(this == null){
        return 0.0
    }
    return this
}

fun Float?.toNonNullFloat() : Float{
    if(this == null){
        return 0F
    }
    return this
}

fun Boolean?.toNonNullBoolean() : Boolean{
    if(this == null){
        return false
    }
    return this
}