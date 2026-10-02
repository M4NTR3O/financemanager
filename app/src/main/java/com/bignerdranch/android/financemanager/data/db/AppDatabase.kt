package com.bignerdranch.android.financemanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.financemanager.data.db.dao.CategoryDao
import com.bignerdranch.android.financemanager.data.db.dao.TransactionDao
import com.bignerdranch.android.financemanager.data.db.dao.UserDao
import com.bignerdranch.android.financemanager.data.db.entity.CategoryEntity
import com.bignerdranch.android.financemanager.data.db.entity.TransactionEntity
import com.bignerdranch.android.financemanager.data.db.entity.UserEntity
import com.bignerdranch.android.financemanager.data.db.seed.SystemCategories
import com.bignerdranch.android.financemanager.domain.model.CategoryType

@Database(
    entities = [UserEntity::class, CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val categoryDao: CategoryDao
    abstract val transactionDao: TransactionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance.db"
                ).addCallback(SeedCallback).build().also { INSTANCE = it }
            }

        private val SeedCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Сид системных категорий — на низком уровне, чтобы не завязываться на DI
                SystemCategories.expense.forEach { (parent, children) ->
                    db.execSQL(
                        "INSERT INTO categories (name, parentCategoryId, type, isSystem) VALUES (?, NULL, ?, 1)",
                        arrayOf<Any?>(parent, CategoryType.Expense.name)
                    )
                    // дальше нужен id родителя → используем последний вставленный rowid
                    val parentId = db.query("SELECT last_insert_rowid()").use { c ->
                        c.moveToFirst(); c.getLong(0)
                    }
                    children.forEach { child ->
                        db.execSQL(
                            "INSERT INTO categories (name, parentCategoryId, type, isSystem) VALUES (?, ?, ?, 1)",
                            arrayOf<Any?>(child, parentId, CategoryType.Expense.name)
                        )
                    }
                }
                SystemCategories.income.forEach { (parent, children) ->
                    db.execSQL(
                        "INSERT INTO categories (name, parentCategoryId, type, isSystem) VALUES (?, NULL, ?, 1)",
                        arrayOf(parent, CategoryType.Income.name)
                    )
                    val parentId = db.query("SELECT last_insert_rowid()").use { c ->
                        c.moveToFirst(); c.getLong(0)
                    }
                    children.forEach { child ->
                        db.execSQL(
                            "INSERT INTO categories (name, parentCategoryId, type, isSystem) VALUES (?, ?, ?, 1)",
                            arrayOf<Any?>(child, parentId, CategoryType.Income.name)
                        )
                    }
                }
            }
        }
    }
}
