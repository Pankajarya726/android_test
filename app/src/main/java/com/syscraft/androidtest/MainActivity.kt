package com.syscraft.androidtest

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ContactAdapter.ContactListener {
    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    var rvContact: RecyclerView? = null
    private var contactAdapter: ContactAdapter? = null


    private var contactList: ArrayList<Contact>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvContact = findViewById<RecyclerView>(R.id.rv_contact)
        checkPermission()
//        listContacts!!.setOnClickListener {
//            loadContacts()
//        }
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
            //callback onRequestPermissionsResult
        } else {


            loadContacts()
        }
    }

    private fun loadContacts() {


        //creating the instance of DatabaseHandler class
        val databaseHandler: SqliteDb = SqliteDb(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        contactList = databaseHandler.getContact()

        if (contactList != null && contactList!!.size > 0) {

            Log.e(javaClass.simpleName, "Total contact" + contactList!!.size.toString());
            contactAdapter = ContactAdapter(this!!, contactList!!, this)
            rvContact!!.layoutManager = LinearLayoutManager(this)
            rvContact!!.hasFixedSize()
            rvContact!!.adapter = contactAdapter!!

        } else {
            getContacts()
        }


//        var builder = StringBuilder()
//        Log.e("tag", "laod Contact")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
//                Manifest.permission.READ_CONTACTS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(
//                arrayOf(Manifest.permission.READ_CONTACTS),
//                PERMISSIONS_REQUEST_READ_CONTACTS
//            )
//            //callback onRequestPermissionsResult
//        } else {
//
//            listContacts!!.text = builder.toString()
//        }
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
//                                    Toast.makeText(
//                                        applicationContext,
//                                        "record save",
//                                        Toast.LENGTH_LONG
//                                    ).show()

                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "id or name or email cannot be blank",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

//                            builder.append("Contact: ").append(name).append(", Phone Number: ")
//                                .append(
//                                    phoneNumValue
//                                ).append("\n\n")
//                            Log.e("Name ===>", phoneNumValue);
                        }
                    }
                    cursorPhone.close()
                }
//                Log.e("Name ===>", phoneNumValue);
            }
        } else {
            //   toast("No contacts available!")
        }
        cursor.close()
        return builder

    }

    override fun onFavoriteClick(contact: Contact) {
        TODO("Not yet implemented")
    }
}