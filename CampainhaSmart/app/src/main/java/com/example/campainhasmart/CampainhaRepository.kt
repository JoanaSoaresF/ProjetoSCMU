package com.example.campainhasmart

import android.content.Context
import android.net.ConnectivityManager
import com.example.campainhasmart.model.User
import com.example.campainhasmart.model.database.CampainhaDatabase

class CampainhaRepository private constructor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val database: CampainhaDatabase = CampainhaDatabase.getDatabase(context)


    companion object {
        /**
         * INSTANCE will keep a reference to any reference returned via GetInstance
         * This will help avoid repeatedly initializing the Repository and load the
         * museum from the database, which is expensive
         */
        @Volatile
        private var INSTANCE: CampainhaRepository? = null


        /**
         * Helper function to get a repository
         * If the repository is already been created, the previous repository will be
         * returned. Otherwise, create a new instance.
         * @param context The application context Singleton, used to get access to the
         * filesystem
         */
        fun getRepository(context: Context):
                CampainhaRepository {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = CampainhaRepository(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    val user : User? = null


}