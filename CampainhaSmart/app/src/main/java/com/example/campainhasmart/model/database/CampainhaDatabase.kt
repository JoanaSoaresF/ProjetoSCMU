package com.example.campainhasmart.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseDevice::class,
        DatabaseOccurrence::class,
        DatabaseUser::class],
    version = 1
)
abstract class CampainhaDatabase : RoomDatabase() {

    /**
     * Connects the database to the DAO's
     */
    abstract val devicesDao: DeviceDao
    abstract val occurrenceDao: OccurrenceDao
    abstract val usersDao: UserDao

    companion object {

        private const val CAMPAINHA_DATABASE = "campainha_database"

        /**
         * INSTANCE will keep a reference to any reference returned cia GetInstance
         * This will help avoid repeatedly initializing the database, which is expensive
         */
        private var INSTANCE: CampainhaDatabase? = null

        /**
         * Helper function to get a database
         * Is the database is already been retrieved, the previous data base will be
         * returned. Otherwise, create a new instance.
         * @param context The application context Singleton, used to get access to the
         * filesystem
         */
        fun getDatabase(context: Context): CampainhaDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CampainhaDatabase::class.java,
                        CAMPAINHA_DATABASE
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }


}


