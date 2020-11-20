package com.syscraft.androidtest

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    var rvContact: RecyclerView? = null
    var searchEditText: EditText? = null
    private var contactAdapter: ContactAdapter? = null
    private var contactList: ArrayList<Contact>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvContact = findViewById<RecyclerView>(R.id.rv_contact)
        searchEditText = findViewById<EditText>(R.id.editTextSearch)
        checkPermission()

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString())
            }
        })

    }

    private fun filter(text: String) {
        //new array list that will hold the filtered data
        val filterdConact: ArrayList<Contact> = ArrayList()

        //looping through existing elements
        for (c in contactList!!) {
            //if the existing elements contains the search input
            if (c.name.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdConact.add(c)
            }
        }

        //calling a method of the adapter class and passing the filtered list
        contactAdapter!!.filterList(filterdConact!!)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {


            loadContacts()
        }
    }

    private fun loadContacts() {


        val databaseHandler: SqliteDb = SqliteDb(this)
        contactList = databaseHandler.getContact()

        if (contactList != null && contactList!!.size > 0) {

            Log.e(javaClass.simpleName, "Total contact" + contactList!!.size.toString());
            contactAdapter = ContactAdapter(this!!, contactList!!)
            rvContact!!.layoutManager = LinearLayoutManager(this)
            rvContact!!.hasFixedSize()
            rvContact!!.adapter = contactAdapter!!








        } else {
            getContacts()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }

    private fun getContacts(): StringBuilder {
        val builder = StringBuilder()
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )

                            val id = id.toString()
                            val name = name.toString()
                            val number = phoneNumValue.toString()
                            val favorite = 0
                            val databaseHandler: SqliteDb = SqliteDb(this)
                            if (id.trim() != "" && name.trim() != "" && number.trim() != "") {
                                val status = databaseHandler.addContact(
                                    Contact(
                                        id,
                                        name,
                                        number,
                                        favorite
                                    )
                                )
                                if (status) {

                                    Log.e("tag", "Data inserted");
                                    Log.e(
                                        "data :",
                                        "id : $id\t name : $name\t number : $number\t favorite : $favorite"
                                    );

                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "id or name or email cannot be blank",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    }
                    cursorPhone.close()
                }
            }
        } else {
        }
        cursor.close()
        return builder

    }


}