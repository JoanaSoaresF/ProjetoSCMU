package com.example.campainhasmart.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.campainhasmart.model.database.CampainhaDatabase
import com.example.campainhasmart.model.database.asDomainModel
import com.example.campainhasmart.model.database.asDomainModelMap
import com.example.campainhasmart.model.domain.User
import com.example.campainhasmart.util.RandomUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository private constructor(context: Context) {


//    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//        .setContentTitle("Nova ocorrência")
//        .setContentText("A cua campainha registou uma nova ocorrência")
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        // Set the intent that will fire when the user taps the notification
//        .setAutoCancel(true)


    private val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    private val deviceDatabase: CampainhaDatabase = CampainhaDatabase.getDatabase(context)
    private val firebaseDatabase = Firebase.database.reference

    private val firebaseStorage = Firebase.storage.reference


    private val _user: MutableLiveData<User> = MutableLiveData<User>(User())
    val user: LiveData<User>
        get() = _user


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

    private val occurrencesListener = object : ChildEventListener {
        override fun onChildAdded(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: occurrence added")
            updateOccurrences(dataSnapshot)
            //TODO notificação
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
            Timber.d("Event listener: occurrence moved")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Timber.d("Event listener: occurrence cancelled")
        }
    }

    private val devicesListener = object : ChildEventListener {
        override fun onChildAdded(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: device added")
        }

        override fun onChildChanged(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: device changed")
            updateDevice(dataSnapshot)
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            Timber.d("Event listener: device removed")
        }

        override fun onChildMoved(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: device moved")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Timber.d("Event listener: device cancelled")
        }
    }

    /**
     * Updates the occurrences list [user.allOccurrences] and adds the new
     * occurrence to
     * the
     * database and to the correspondent device
     */
    private fun updateOccurrences(dataSnapshot: DataSnapshot) {

        val occurrence = dataSnapshot.getValue<FirebaseOccurrence>()
        occurrence?.let { occ ->
            //update data base
            deviceDatabase.occurrenceDao.insertOccurrences(occ.asDatabaseModel())
            //update local list live data
            //update device
            val newUser = _user.value!!
            newUser.devices.forEach { d ->
                if (d.id == occ.deviceId) {
                    d.addOccurrences(occ.asDomainModel(firebaseStorage))
                }
            }
            newUser.allOccurrences[occ.id!!] = occ.asDomainModel(firebaseStorage)
            _user.postValue(newUser)
        }
    }


    /**
     * Updates the user devices with new information
     */
    private fun updateDevice(dataSnapshot: DataSnapshot) {

        val device = dataSnapshot.getValue<FirebaseDevice>()
        device?.let { dev ->
            //update data base
            deviceDatabase.devicesDao.insertDevices(dev.asDatabaseModel())
            //update local list live data
            //update device
            val newUser = _user.value!!
            newUser.devices.forEach { d ->
                if (d.id == dev.id) {
                    d.update(device)
                }
            }
            _user.postValue(newUser)
        }
    }

    suspend fun loadData() {
        return withContext(Dispatchers.IO) {
            if (!deviceDatabase.usersDao.hasUser(user.value!!.id)) {
                //there are no info in the device database yet - first time use
//                checkUserInFirebase()
                deviceDatabase.usersDao.insertUser(user.value!!.asDatabaseModel())
                //add mockup user and devices to firebase (for not having an empty app)
                populateWithMockupData()
            }
            //update local database
            updateDatabaseFromFirebase()
            loadDataFromDatabase()
        }

    }

    //TODO depois pode ser apagado
    /**
     * Populates firebase and local database with mockup data
     */
    fun populateWithMockupData() {
        if (isNetworkAvailable()) {

            val userFirebase = firebaseDatabase.child(USERS).child(user.value!!.id)

            for (d: FirebaseDevice in RandomUtils.testDevices) {
                d.id?.let { deviceId ->
                    val firebaseDevice = firebaseDatabase.child(DEVICES).child(deviceId)
                    firebaseDevice.setValue(d)
                    //add device to user
                    user.value!!.addDevices(
                        d.asDomainModel(
                            firebaseStorage.child(
                                d.entrancePhoto!!
                            )
                        )
                    )
                    //add mockup data to local database
//                    deviceDatabase.devicesDao.insertDevices(d.asDatabaseModel())

                    for (o: FirebaseOccurrence in RandomUtils.testOccurrences) {
                        o.id?.let { occurrenceId ->
                            if (deviceId == o.deviceId) {
                                val deviceOccurrences =
                                    firebaseDevice.child(OCCURRENCES).child(occurrenceId)
                                deviceOccurrences.setValue(o)
                                //add occurrences to database
//                                deviceDatabase.occurrenceDao.insertOccurrences(o.asDatabaseModel())
                            }
                        }
                    }
                }
            }
            //add user with devices to firebase
            userFirebase.setValue(user.value!!.asFirebaseModel())
        }
    }


    /**
     * Refreshes the devices on the local database, retrieving then from firebase
     * if there is no network available only occurrences in the local database will be
     * displayed
     */
    private suspend fun updateDatabaseFromFirebase() {
        if (isNetworkAvailable()) {
            withContext(Dispatchers.IO) {
                val firebaseDevices = firebaseDatabase.child(DEVICES)

                for (d: Device in user.value!!.devices) {
                    val deviceFirebase = firebaseDevices.child(d.id)
                    deviceFirebase.addChildEventListener(devicesListener)
                    deviceFirebase.get().addOnSuccessListener {
                        val device = it.getValue<FirebaseDevice>()
                        if (device != null) {
                            Timber.d("Got device ${device.id} from firebase")
                            //add to database
                            CoroutineScope(Dispatchers.IO).launch {
                                deviceDatabase.devicesDao.insertDevices(device.asDatabaseModel())
                            }
                        } else {
                            Timber.d("Device ${d.id} not found in firebase")
                        }
                    }.addOnFailureListener {
                        Timber.d("Fail to get device ${d.id} from firebase")
                    }

                    val deviceOccurrencesFirebase = deviceFirebase.child(OCCURRENCES)
                    deviceOccurrencesFirebase.addChildEventListener(occurrencesListener)

                    deviceOccurrencesFirebase.get().addOnSuccessListener {
                        val typeIndicator = object :
                            GenericTypeIndicator<Map<String, FirebaseOccurrence>>() {}
                        val deviceOccurrences = it.getValue(typeIndicator)
                        if (deviceOccurrences != null) {
                            Timber.d("Got device ${d.id} from firebase")
                            val occrs = deviceOccurrences.values
                            //add to database
                            CoroutineScope(Dispatchers.IO).launch {
                                deviceDatabase.occurrenceDao.insertOccurrences(
                                    *occrs.asDatabaseModel().toTypedArray()
                                )
                            }

                        } else {
                            Timber.d(
                                "Cannot find occurrences of device ${d.id} not " +
                                        "found in firebase"
                            )
                        }
                    }.addOnFailureListener {
                        Timber.d(
                            "Fail to get occurrences from device ${d.id} from " +
                                    "firebase"
                        )
                    }
                }
            }
        } else {
            Timber.d("Network not available: loading occurrences from local database")
        }
    }

    private suspend fun loadDataFromDatabase() {
        if (deviceDatabase.usersDao.hasUser(user.value!!.id)) {
            withContext(Dispatchers.IO) {
                val occurrences = deviceDatabase.occurrenceDao.getOccurrences()
                _user.value!!.allOccurrences =
                    occurrences.asDomainModelMap(firebaseStorage)
                val devices =
                    deviceDatabase.devicesDao.getDevices().asDomainModel(firebaseStorage)
                user.value!!.devices = devices as MutableList<Device>
                for (device: Device in user.value!!.devices) {
                    val occrs =
                        deviceDatabase.occurrenceDao.getOccurrencesFromDevice(device.id)
                    device.occurrences =
                        occrs.asDomainModel(firebaseStorage) as MutableList<Occurrence>
                }
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


