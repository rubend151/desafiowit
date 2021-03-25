package com.example.desafio2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.desafio2.R
import com.example.desafio2.model.Detail

internal class DetailAdapter(private val context: Context, private val detailsArr: ArrayList<Detail>) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var value: TextView
    private lateinit var label: TextView
    private lateinit var icon: ImageView

    override fun getCount(): Int {
        return detailsArr.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.detail_view, null)
        }

        if (convertView != null) {
            value = convertView.findViewById(R.id.value)
            label = convertView.findViewById(R.id.label)
            icon = convertView.findViewById(R.id.icon)
        }
        value.text = detailsArr[position].value
        label.text = detailsArr[position].label
        icon.setImageResource(detailsArr[position].drawable)
        return convertView
    }
}
