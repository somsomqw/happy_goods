package jp.co.happy_goods.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemListModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val stock: Int,
    val description: String,
    val imageUrl: String,
    val bank: String,
    val account: String,
    val accountName: String
): Parcelable {
    constructor(): this("", "", 0, "", 0,
        "", "", "", "", "")
}