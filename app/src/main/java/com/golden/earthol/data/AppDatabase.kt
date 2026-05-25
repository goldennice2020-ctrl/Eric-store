package com.golden.earthol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.golden.earthol.data.dao.AssetDao
import com.golden.earthol.data.dao.AttributeDao
import com.golden.earthol.data.dao.AiMemoryDao
import com.golden.earthol.data.dao.DebuffDao
import com.golden.earthol.data.dao.EventDao
import com.golden.earthol.data.dao.GuideDao
import com.golden.earthol.data.dao.HiddenQuestDao
import com.golden.earthol.data.dao.InventoryDao
import com.golden.earthol.data.dao.JournalDao
import com.golden.earthol.data.dao.LifeArchiveDao
import com.golden.earthol.data.dao.LifeStageDao
import com.golden.earthol.data.dao.LibraryContentDao
import com.golden.earthol.data.dao.PlaceDao
import com.golden.earthol.data.dao.PlayerDao
import com.golden.earthol.data.dao.PlayerStyleDao
import com.golden.earthol.data.dao.ProjectDao
import com.golden.earthol.data.dao.RandomEventDao
import com.golden.earthol.data.dao.RelationshipDao
import com.golden.earthol.data.dao.SettingEntryDao
import com.golden.earthol.data.dao.SkillDao
import com.golden.earthol.data.dao.SurvivalStatusDao
import com.golden.earthol.data.dao.TalentDao
import com.golden.earthol.data.dao.TaskDao
import com.golden.earthol.data.dao.WorldSettingDao
import com.golden.earthol.data.dao.WorldRuleDao
import com.golden.earthol.data.entity.AssetEntity
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.AiMemoryEntity
import com.golden.earthol.data.entity.DebuffEntity
import com.golden.earthol.data.entity.EventEntity
import com.golden.earthol.data.entity.GuideEntity
import com.golden.earthol.data.entity.HiddenQuestEntity
import com.golden.earthol.data.entity.InventoryEntity
import com.golden.earthol.data.entity.JournalEntity
import com.golden.earthol.data.entity.LifeArchiveEntity
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.LibraryContentEntity
import com.golden.earthol.data.entity.PlaceEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.PlayerStyleEntity
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.data.entity.RandomEventEntity
import com.golden.earthol.data.entity.RelationshipEntity
import com.golden.earthol.data.entity.SettingEntryEntity
import com.golden.earthol.data.entity.SkillEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.data.entity.WorldSettingEntity
import com.golden.earthol.data.entity.WorldRuleEntity

@Database(
    entities = [
        PlayerEntity::class,
        SurvivalStatusEntity::class,
        AttributeEntity::class,
        TalentEntity::class,
        TaskEntity::class,
        ProjectEntity::class,
        AssetEntity::class,
        PlaceEntity::class,
        LifeStageEntity::class,
        PlayerStyleEntity::class,
        WorldRuleEntity::class,
        GuideEntity::class,
        HiddenQuestEntity::class,
        RandomEventEntity::class,
        LifeArchiveEntity::class,
        LibraryContentEntity::class,
        DebuffEntity::class,
        SettingEntryEntity::class,
        EventEntity::class,
        SkillEntity::class,
        RelationshipEntity::class,
        InventoryEntity::class,
        WorldSettingEntity::class,
        JournalEntity::class,
        AiMemoryEntity::class
    ],
    version = 14,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun survivalStatusDao(): SurvivalStatusDao
    abstract fun attributeDao(): AttributeDao
    abstract fun talentDao(): TalentDao
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun assetDao(): AssetDao
    abstract fun placeDao(): PlaceDao
    abstract fun lifeStageDao(): LifeStageDao
    abstract fun playerStyleDao(): PlayerStyleDao
    abstract fun worldRuleDao(): WorldRuleDao
    abstract fun guideDao(): GuideDao
    abstract fun hiddenQuestDao(): HiddenQuestDao
    abstract fun randomEventDao(): RandomEventDao
    abstract fun lifeArchiveDao(): LifeArchiveDao
    abstract fun libraryContentDao(): LibraryContentDao
    abstract fun debuffDao(): DebuffDao
    abstract fun settingEntryDao(): SettingEntryDao
    abstract fun eventDao(): EventDao
    abstract fun skillDao(): SkillDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun worldSettingDao(): WorldSettingDao
    abstract fun journalDao(): JournalDao
    abstract fun aiMemoryDao(): AiMemoryDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private val Migration11To12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `library_contents` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `pairId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `tags` TEXT NOT NULL,
                        `importance` INTEGER NOT NULL,
                        `rawText` TEXT,
                        `structuredJson` TEXT,
                        `readableText` TEXT,
                        `sourceType` TEXT NOT NULL,
                        `createdAt` TEXT NOT NULL,
                        `updatedAt` TEXT NOT NULL,
                        `version` INTEGER NOT NULL,
                        `checksum` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val Migration12To13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `setting_entries` (
                        `key` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`key`)
                    )
                    """.trimIndent()
                )
            }
        }

        private val Migration13To14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `guides` ADD COLUMN `createdAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `guides` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `events` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `effectText` TEXT NOT NULL,
                        `occurredAt` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `skills` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `level` INTEGER NOT NULL,
                        `exp` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `relationships` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `role` TEXT NOT NULL,
                        `closeness` INTEGER NOT NULL,
                        `trust` INTEGER NOT NULL,
                        `energyEffect` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `inventory` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `quantity` INTEGER NOT NULL,
                        `valueScore` INTEGER NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `world_settings` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `key` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `version` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `journals` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `moodScore` INTEGER NOT NULL,
                        `importance` INTEGER NOT NULL,
                        `entryDate` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `ai_memories` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `memoryType` TEXT NOT NULL,
                        `importance` INTEGER NOT NULL,
                        `source` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "earth_ol.db"
                )
                    .addMigrations(Migration11To12, Migration12To13, Migration13To14)
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { instance = it }
            }
    }
}
