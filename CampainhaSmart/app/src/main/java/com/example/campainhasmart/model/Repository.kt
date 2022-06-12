package com.example.campainhasmart.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.campainhasmart.model.*
import com.example.campainhasmart.model.database.CampainhaDatabase
import com.example.campainhasmart.model.database.asDomainModel
import com.example.campainhasmart.model.database.asDomainModelMap
import com.example.campainhasmart.model.domain.User
import com.example.campainhasmart.util.RandomUtils
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.min

class Repository private constructor(context: Context) {


    private val _notificationOccurrence: MutableLiveData<Occurrence?> = MutableLiveData()
    val notificationOccurrence: LiveData<Occurrence?>
        get() = _notificationOccurrence


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
        private const val LED = "ledOn"
        private const val MESSAGE_PART1 = "messageOnDisplay1"
        private const val MESSAGE_PART2 = "messageOnDisplay2"
        private const val DOOR = "openDoor"

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

    init {
        CoroutineScope(Dispatchers.IO).launch {
            updateUser()
        }
    }

    private suspend fun updateUser() {
        withContext(Dispatchers.IO) {
            if (deviceDatabase.usersDao.hasUser()) {
                Timber.d("Getting user from database on init")
                val u = deviceDatabase.usersDao.getUser().asDomainModel()
                val devices = deviceDatabase.devicesDao
                    .getDevices()
                    .asDomainModel(firebaseStorage)

                u.addDevices(*devices.toTypedArray())
                _user.postValue(u)
            } else {
                _user.postValue(User())
            }
            loadData()
            Timber.d("User updated with ${user.value?.devices?.size} devices")
        }
    }

    private val occurrencesListener = object : ChildEventListener {
        override fun onChildAdded(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: occurrence added")
            CoroutineScope(Dispatchers.IO).launch {
                updateOccurrences(dataSnapshot)
            }
        }

        override fun onChildChanged(
            dataSnapshot: DataSnapshot,
            previousChildName: String?
        ) {
            Timber.d("Event listener: occurrence changed")
            CoroutineScope(Dispatchers.IO).launch {
                updateOccurrences(dataSnapshot)
            }
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

    private val devicesListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.d("Event listener: device changed")
            CoroutineScope(Dispatchers.IO).launch {
                updateDevice(snapshot)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Timber.d("Event listener: device cancelled")
        }
    }

    /**
     * Updates the occurrences list [user.allOccurrences] and adds the new
     * occurrence to the database and to the correspondent device
     */
    private suspend fun updateOccurrences(dataSnapshot: DataSnapshot) {
        withContext(Dispatchers.IO) {
            val occurrence = dataSnapshot.getValue<FirebaseOccurrence>()

            val validOccurrence = occurrence?.let { validOccurrence(it) } == true
            if (occurrence != null && validOccurrence) {

                if (!deviceDatabase.occurrenceDao.hasOccurrence(occurrence.id!!)) {
                    Timber.d("updateOccurrences: Occurrence: $occurrence")
                    //1. update data base
                    deviceDatabase.occurrenceDao.insertOccurrences(occurrence.asDatabaseModel())
                    //2. update user device
                    val newUser = _user.value!!
                    val occurrenceDomain = occurrence.asDomainModel(firebaseStorage)
                    newUser.devices.forEach { userDevice ->
                        if (userDevice.id == occurrence.deviceId) {
                            userDevice.addOccurrences(
                                occurrenceDomain
                            )
                        }
                    }
                    //3. update user [user.allOccurrences]
                    newUser.allOccurrences[occurrence.id] =
                        occurrenceDomain
                    _user.postValue(newUser)

                    _notificationOccurrence.postValue(occurrenceDomain)
                }
            }
        }
    }

    private fun validOccurrence(occurrence: FirebaseOccurrence): Boolean {
        return occurrence.id != null
                && occurrence.date != null
                && occurrence.deviceId != null
                && occurrence.type != null
                && occurrence.photo != null
    }


    /**
     * Updates the user devices with new information
     */
    private suspend fun updateDevice(dataSnapshot: DataSnapshot) {
        withContext(Dispatchers.IO) {
            val device = dataSnapshot.getValue<FirebaseDevice>()
            device?.let { dev ->
                //1. update data base
                deviceDatabase.devicesDao.insertDevices(dev.asDatabaseModel())
                //2. update user device
                val newUser = _user.value!!
                newUser.devices.forEach { d ->
                    if (d.id == dev.id) {
                        d.update(device)
                    }
                }
                _user.postValue(newUser)
            }
        }


    }

    private suspend fun loadData() {
        Timber.d("Loading data...")
        return withContext(Dispatchers.IO) {
            if (!deviceDatabase.usersDao.hasUser()) {
                //there are no info in the device database yet - first time use
                deviceDatabase.usersDao.insertUser(user.value!!.asDatabaseModel())
                //add mockup user and devices to firebase (for not having an empty app)
                populateWithMockupData()
            }
            //update local database
//            updateDatabaseFromFirebase()
            loadDataFromDatabase()
            addListeners()
        }
    }

    //TODO depois pode ser apagado
    /**
     * Populates firebase and local database with mockup data
     */
    private fun populateWithMockupData() {
        if (isNetworkAvailable()) {
            Timber.d("Loading firebase and local database with mockup data")

            val userFirebase = firebaseDatabase.child(USERS).child(user.value!!.id)

            for (d: FirebaseDevice in RandomUtils.testDevices) {
                d.id?.let { deviceId ->
                    val firebaseDevice = firebaseDatabase.child(DEVICES).child(deviceId)
                    firebaseDevice.setValue(d)
                    //add device to user
                    user.value!!.addDevices(
                        d.asDomainModel(firebaseStorage)
                    )
                    //add mockup data to local database
                    deviceDatabase.devicesDao.insertDevices(d.asDatabaseModel())

                    for (o: FirebaseOccurrence in RandomUtils.testOccurrences) {
                        o.id?.let { occurrenceId ->
                            if (deviceId == o.deviceId) {
                                val deviceOccurrences =
                                    firebaseDevice.child(OCCURRENCES).child(occurrenceId)
                                deviceOccurrences.setValue(o)
                                //add occurrences to database
                                deviceDatabase.occurrenceDao.insertOccurrences(o.asDatabaseModel())
                            }
                        }
                    }
                }
            }
            //add user with devices to firebase
            userFirebase.setValue(user.value!!.asFirebaseModel())
        }
    }

    private suspend fun addListeners() {
        withContext(Dispatchers.IO) {
            if (isNetworkAvailable()) {
                Timber.d("Add listeners for firebase")

                val firebaseDevices = firebaseDatabase.child(DEVICES)

                for (d: Device in user.value!!.devices) {
                    // update devices
                    val deviceFirebase = firebaseDevices.child(d.id)
                    deviceFirebase.addValueEventListener(devicesListener)

                    //update occurrences
                    val deviceOccurrencesFirebase = deviceFirebase.child(OCCURRENCES)
                    deviceOccurrencesFirebase.addChildEventListener(occurrencesListener)
                }

            } else {
                Timber.d("Network not available: loading occurrences from local database")
                loadDataFromDatabase()
            }
        }
    }


    private fun loadDataFromDatabase() {
        Timber.d("Loading local database")
        if (deviceDatabase.usersDao.hasUser()) {

            val occurrences = deviceDatabase.occurrenceDao.getOccurrences()
            val userUpdate = _user.value!!
            userUpdate.allOccurrences =
                occurrences.asDomainModelMap(firebaseStorage)
            val devices =
                deviceDatabase.devicesDao.getDevices().asDomainModel(firebaseStorage)
            userUpdate.devices = devices as MutableList<Device>
            for (device: Device in userUpdate.devices) {
                val occrs =
                    deviceDatabase.occurrenceDao.getOccurrencesFromDevice(device.id)
                device.occurrences =
                    occrs.asDomainModel(firebaseStorage) as MutableList<Occurrence>
            }
            _user.postValue(userUpdate)

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


    fun setLedValue(device: Device, b: Boolean): Boolean {
        val internet = isNetworkAvailable()
        if (internet) {
            val i = if (b) 1 else 0
            firebaseDatabase.child(DEVICES).child(device.id).child(LED).setValue(i)
        }
        return internet

    }

    fun setOpenDoor(device: Device): Boolean {
        val internet = isNetworkAvailable()
        if (internet) {
            firebaseDatabase.child(DEVICES).child(device.id).child(DOOR).setValue(1)
        }
        return internet

    }

    fun setMessage(device: Device, message: String): Boolean {
        val internet = isNetworkAvailable()
        if (internet) {
            var part1 = ""
            var part2 = ""
            if (message.length > 16) {
                val min = min(32, message.length)
                part1 = message.subSequence(0, 16) as String
                part2 = message.subSequence(16, min) as String
            } else {
                part1 = message.subSequence(0, message.length) as String

            }

            firebaseDatabase.child(DEVICES).child(device.id).child(MESSAGE_PART1)
                .setValue(part1)
            firebaseDatabase.child(DEVICES).child(device.id).child(MESSAGE_PART2)
                .setValue(part2)
        }
        return internet

    }


}


