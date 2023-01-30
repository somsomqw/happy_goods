package jp.co.happy_goods.data

data class UserModel(
    val email: String,
    val password: String,
    val profileImageUrl: String,
) {
    constructor(): this("", "", "")
}