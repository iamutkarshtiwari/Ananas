package kuanweitw.github.io.ananas.editimage.adapter.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kuanweitw.github.io.ananas.R

class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var icon: ImageView = itemView.findViewById<View>(R.id.filter_image) as ImageView
    @JvmField
    var text: TextView = itemView.findViewById<View>(R.id.filter_name) as TextView
}