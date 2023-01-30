package jp.co.orix.credit.nextage.utils


fun Any?.toEmptyString(): String{
    return try {
        this?.toString() ?: ""
    }catch (E: Exception){
        ""
    }
}
