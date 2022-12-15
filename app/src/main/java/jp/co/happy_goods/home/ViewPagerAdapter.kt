package jp.co.happy_goods.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import jp.co.happy_goods.R

class ViewPagerAdapter(imgList: ArrayList<Int>, private val context: Context): RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    var item = imgList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :PagerViewHolder{
        return PagerViewHolder(LayoutInflater.from(context).inflate(R.layout.banner_img_list, parent, false))
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.idol.setImageResource(item[position%3])
    }

    inner class PagerViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val idol: ImageView = view.findViewById(R.id.imageView_idol)
    }
}