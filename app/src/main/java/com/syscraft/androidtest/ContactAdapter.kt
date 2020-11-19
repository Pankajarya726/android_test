package com.syscraft.androidtest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.inflate
import androidx.recyclerview.widget.RecyclerView
import com.syscraft.androidtest.databinding.RowContactBinding


class ContactAdapter(
    context: Context,
    contactList: ArrayList<Contact>,
    listener: ContactListener
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {


    private var ctx = context
    private var contactList = contactList
    private var listener = listener
    lateinit var contactBinding: RowContactBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactAdapter.ViewHolder {
        contactBinding =
            DataBindingUtil.inflate(LayoutInflater.from(ctx), R.layout.row_contact, parent, false)


        return ViewHolder(contactBinding)

    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
        val contact = contactList[position]
        holder.binding.txtName.text = contact.name
        holder.binding.txtNumber.text = contact.number

        if(contact.favorite==1){
            holder.binding.imgFavorite.setImageResource(R.drawable.favorite)
        }else{
            holder.binding.imgFavorite.setImageResource(R.drawable.unfavorite)
        }


        holder.binding.imgFavorite.setOnClickListener(View.OnClickListener {

            if(contact.favorite==1){
                contactList[position].favorite = 0;
                holder.binding.imgFavorite.setImageResource(R.drawable.unfavorite)
            }else{
                contactList[position].favorite = 1;
                holder.binding.imgFavorite.setImageResource(R.drawable.favorite)
            }

            this.notifyItemChanged(position)


        })

    }

    fun removeItem(position: Int) {
        contactList.removeAt(position)
        notifyDataSetChanged()


    }

    class ViewHolder(binding: RowContactBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding = binding
    }


    interface ContactListener {
        fun onFavoriteClick(contact: Contact);

    }

}