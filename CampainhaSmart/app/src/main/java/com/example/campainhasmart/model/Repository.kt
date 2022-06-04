package com.example.campainhasmart.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.campainhasmart.model.database.CampainhaDatabase
import com.example.campainhasmart.util.RandomUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository private constructor(context: Context) {

    private val connectivityManager = context.getSystemService(
        Context
            .CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    private val deviceDatabase: CampainhaDatabase = CampainhaDatabase.getDatabase(context)
    private val user = User()
    private val firebaseDatabase = Firebase.database.reference

    private val _occurrences: MutableLiveData<MutableList<Occurrence>> = MutableLiveData(
        mutableListOf()
    )
    val occurrence: LiveData<MutableList<Occurrence>>
        get() = _occurrences

    companion object {

        private const val USERS = "users"
        private const val OCCURRENCES = "occurrences"
        private const val DEVICES = "devices"

        /**
         * INSTANCE will keep a reference to any reference returned cia GetInstance
         * This will help avoid repeatedly initializing the repository, which is expensive
         */
        @Volatile
        private var INSTANCE: Repository? = null

        /**
         * Helper function to get a database
         * Is the repository is already been retrieved, the previous repository will be
         * returned. Otherwise, create a new instance.
         * @param context The application context Singleton, used to get access to the
         * filesystem
         */
        fun getRepository(context: Context): Repository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Repository(context)
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

    //TODO adicionar listeners
    val childEventListener = object : ChildEventListener {
        override fun onChildAdded(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: occurrence added")
            updateOccurrences(dataSnapshot)
        }

        override fun onChildChanged(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: occurrence changed")
            updateOccurrences(dataSnapshot)
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            Timber.d("Event listener: occurrence removed")
        }

        override fun onChildMoved(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: occurrence movec")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Timber.d("Event listener: occurrence cancelled")
        }
    }

    private fun updateOccurrences(dataSnapshot: DataSnapshot) {
        val occurrence = dataSnapshot.getValue<FirebaseOccurrence>()
        occurrence?.let {
            val occurList = _occurrences.value!!
            deviceDatabase.occurrenceDao.insertOccurrences(it.asDatabaseModel())
            occurList.add(it.asDomainModel())
            _occurrences.setValue(occurList)

        }
    }

    private suspend fun loadData() {
        return withContext(Dispatchers.IO) {
            if (!deviceDatabase.usersDao.hasUser(user.id)) {
                //there are no info in the device database yet - first time use
                checkUserInFirebase()
            }
            //TODO o que fazer quando já temos o user
        }

    }

    private fun checkUserInFirebase() {
        firebaseDatabase.child(USERS).child(user.id).get()
            .addOnSuccessListener {
                val userFirebase = it.getValue(FirebaseUser::class.java)
                Timber.d("User from firebase return: $userFirebase")
                if (userFirebase == null) {
                    //user not in firebase so everything is new - instanciante firebase
                    populateFirebaseWithMockupData()
                } else {
                    //update occurrences from database
                    //TODO
                    getOccurrencesFromFirebase()
                }
            }.addOnFailureListener {
                Timber.d("Fail to check user ${user.id}")
            }
    }

    private fun getOccurrencesFromFirebase() {
        if (isNetworkAvailable()) {
            firebaseDatabase.child(OCCURRENCES).get().addOnSuccessListener {
                Timber.d("Updating local occurrences")
                val typeIndicator = object :
                    GenericTypeIndicator<Map<String, FirebaseOccurrence>>() {}
                val occurrences = it.getValue(typeIndicator)
                if (occurrences != null) {
                    val occrs = occurrences.values.toDatabaseModel()
                    deviceDatabase.occurrenceDao.insertOccurrences(*occrs.toTypedArray())
                }

            }.addOnFailureListener {
                Timber.d("Fail to ger occurrences from firebase")

            }
        }

    }

    private fun populateFirebaseWithMockupData() {
        if (isNetworkAvailable()) {
            firebaseDatabase.child(USERS).child(user.id).setValue(user)
            for (d: FirebaseDevice in RandomUtils.testDevices) {
                d.id?.let { firebaseDatabase.child(DEVICES).child(it).setValue(d) }
            }
            for (o: FirebaseOccurrence in RandomUtils.testOccurrences) {
                o.id?.let { firebaseDatabase.child(OCCURRENCES).child(it).setValue(o) }
            }
        }
    }


    private fun isNetworkAvailable(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        Timber.d("Network is off")
        return false

    }

}


