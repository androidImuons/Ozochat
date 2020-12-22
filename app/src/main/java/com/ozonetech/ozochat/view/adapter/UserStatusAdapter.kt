package com.ozonetech.ozochat.view.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.ozonetech.ozochat.R
import java.io.File

class UserStatusAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = ArrayList<Uri>()
    lateinit var listerner: StatusAdapterListerner
    var selected_position = 0

    fun addImage(list: List<Uri>, listerner: StatusAdapterListerner) {
        this.listerner = listerner
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllUri() : List<Uri>{
        return list;
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.image_row_layout, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //Uri imageUri = Uri.fromFile(new File(list.get(position)));// For files on device


        /*  val f = File(list[position])
          val len = f.absolutePath.length
          Log.e("check", "f.absolutePath.subSequence(len-3,len)  " + f.absolutePath.subSequence(len - 3, len))
          val bitmap = if (f.absolutePath.subSequence(len - 3, len).equals("mp4")) {
              (holder as Holder).play.visibility = View.VISIBLE
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                  ThumbnailUtils.createVideoThumbnail(f, Size(500, 500), null)
              } else {
                  ThumbnailUtils.createVideoThumbnail(f.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
              }
          } else {
              (holder as Holder).play.visibility = View.GONE
              BitmapFactory.decodeFile(f.absolutePath)
              //BitmapDrawable(context.resources, f.absolutePath).bitmap
          }
          val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)

          val roundPx = bitmap!!.width.toFloat() * 0.06f
          roundedBitmapDrawable.cornerRadius = roundPx
          holder.iv.setImageDrawable(roundedBitmapDrawable)
  */
        /* val imageUri = Uri.fromFile(File(list.get(0)))
         holder.iv.setImageURI(imageUri)*/


        (holder as Holder).iv.setImageURI(list[position])
        holder.ll_item.setBackgroundColor(if (selected_position === position) Color.MAGENTA else Color.TRANSPARENT)


    }

    override fun getItemCount() = list.size

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
         var ll_item: LinearLayout = itemView.findViewById(R.id.ll_item);
         var iv: ImageView = itemView.findViewById(R.id.iv)
         var play: ImageView = itemView.findViewById(R.id.play)

        override fun onClick(v: View) {
            // Below line is just like a safety check, because sometimes holder could be null,
            // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
            if (adapterPosition == RecyclerView.NO_POSITION) return

            // Updating old as well as new positions
            notifyItemChanged(selected_position)
            selected_position = adapterPosition
            notifyItemChanged(selected_position)

            listerner.onStatusViewClicked(selected_position)

            // Do your another stuff for your onClick
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface StatusAdapterListerner {
        fun onStatusViewClicked(position: Int)

    }

}

/*
        holder.iv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(f.absolutePath))
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setDataAndType(Uri.parse(f.absolutePath), Files.probeContentType(f.toPath()))
                } else {
                    intent.data = Uri.parse(f.absolutePath)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            context.startActivity(intent)
        }
*/

/*Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(
    500f, com.fxn.utility.Utility.rotate(d,list.get(position).getOrientation()));*/