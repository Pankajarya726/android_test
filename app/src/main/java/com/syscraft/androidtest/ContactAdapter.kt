package com.syscraft.androidtest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.syscraft.androidtest.databinding.RowContactBinding


class ContactAdapter(
    context: Context,
    contactList: ArrayList<Contact>
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {


    private var ctx = context
    private var contactList = contactList
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

        if (contact.favorite == 1) {
            holder.binding.imgFavorite.setImageResource(R.drawable.favorite)
        } else {
            holder.binding.imgFavorite.setImageResource(R.drawable.unfavorite)
        }


        holder.binding.imgFavorite.setOnClickListener(View.OnClickListener {


            val databaseHandler: SqliteDb = SqliteDb(context = ctx)

            //calling the updateEmployee method of DatabaseHandler class to update record

//            else{
//                Toast.makeText(applicationContext,"id or name or email cannot be blank", Toast.LENGTH_LONG).show()
//            }
//
            if (contact.favorite == 1) {
                contactList[position].favorite = 0;
                holder.binding.imgFavorite.setImageResource(R.drawable.unfavorite)
                val status = databaseHandler.updateContact(
                    Contact(
                        id = contact.id,
                        name = contact.name,
                        number = contact.number,
                        favorite = 0
                    )
                )
                if (status > -1) {
                    Toast.makeText(ctx, "Contact removed from favorite", Toast.LENGTH_LONG).show()
                }

            } else {
                contactList[position].favorite = 1;
                holder.binding.imgFavorite.setImageResource(R.drawable.favorite)
                val status = databaseHandler.updateContact(
                    Contact(
                        id = contact.id,
                        name = contact.name,
                        number = contact.number,
                        favorite = 1
                    )
                )
                if (status > -1) {
                    Toast.makeText(ctx, "Contact saved to favorite", Toast.LENGTH_LONG).show()
                }
            }



            this.notifyItemChanged(position)


        })

    }

    fun filterList(filterdContact: ArrayList<Contact>) {
        this.contactList = filterdContact
        notifyDataSetChanged()
    }


    class ViewHolder(binding: RowContactBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding = binding
    }




}