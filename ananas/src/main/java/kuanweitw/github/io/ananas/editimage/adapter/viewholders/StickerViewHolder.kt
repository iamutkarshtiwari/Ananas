package kuanweitw.github.io.ananas.editimage.adapter.viewholders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kuanweitw.github.io.ananas.R

class StickerViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var image: ImageView = itemView.findViewById(R.id.img)
}