package com.solankys123.creditcardcrm

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val inquiryType: String,
    val status: String,
    val priority: String,
    val nextAction: String,
    val phone: String = "",
    val applicationNumber: String = "",
    val pendingReason: String = "",
    val lastContact: String = "",
    val note: String = ""
)

@Entity(tableName = "follow_ups")
data class FollowUpEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val reason: String,
    val dueAt: String,
    val priority: String,
    val completed: Boolean = false
)

@androidx.room.Dao
interface CustomerDao {
    @androidx.room.Query("SELECT * FROM customers ORDER BY id DESC")
    suspend fun getAll(): List<CustomerEntity>
    @androidx.room.Insert
    suspend fun insert(customer: CustomerEntity): Long
    @androidx.room.Update
    suspend fun update(customer: CustomerEntity)
    @androidx.room.Delete
    suspend fun delete(customer: CustomerEntity)
    @androidx.room.Query("SELECT COUNT(*) FROM customers")
    suspend fun count(): Int

    @androidx.room.Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CustomerEntity?
}

@androidx.room.Dao
interface FollowUpDao {
    @androidx.room.Query("SELECT * FROM follow_ups ORDER BY dueAt ASC")
    suspend fun getAll(): List<FollowUpEntity>

    @androidx.room.Query("SELECT * FROM follow_ups WHERE customerName = :customerName AND completed = 0 ORDER BY dueAt ASC")
    suspend fun getPendingForCustomer(customerName: String): List<FollowUpEntity>
    @androidx.room.Insert
    suspend fun insert(followUp: FollowUpEntity): Long
    @androidx.room.Update
    suspend fun update(followUp: FollowUpEntity)
    @androidx.room.Delete
    suspend fun delete(followUp: FollowUpEntity)
}

@Database(entities = [CustomerEntity::class, FollowUpEntity::class], version = 1, exportSchema = false)
abstract class CrmDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun followUpDao(): FollowUpDao

    companion object {
        @Volatile private var INSTANCE: CrmDatabase? = null

        fun getInstance(context: android.content.Context): CrmDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    CrmDatabase::class.java,
                    "credit_card_crm.db"
                ).build().also { INSTANCE = it }
            }
    }
}
