package jp.co.happy_goods.home

data class ItemListModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String,
){
    constructor(): this("", "", 0, "", "")
}