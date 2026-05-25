package com.golden.earthol.data

import com.golden.earthol.data.entity.AssetEntity
import com.golden.earthol.data.entity.AttributeEntity
import com.golden.earthol.data.entity.DebuffEntity
import com.golden.earthol.data.entity.GuideEntity
import com.golden.earthol.data.entity.HiddenQuestEntity
import com.golden.earthol.data.entity.LifeArchiveEntity
import com.golden.earthol.data.entity.LifeStageEntity
import com.golden.earthol.data.entity.LibraryContentEntity
import com.golden.earthol.data.entity.PlaceEntity
import com.golden.earthol.data.entity.PlayerEntity
import com.golden.earthol.data.entity.PlayerStyleEntity
import com.golden.earthol.data.entity.ProjectEntity
import com.golden.earthol.data.entity.RandomEventEntity
import com.golden.earthol.data.entity.SettingEntryEntity
import com.golden.earthol.data.entity.SurvivalStatusEntity
import com.golden.earthol.data.entity.TalentEntity
import com.golden.earthol.data.entity.TaskEntity
import com.golden.earthol.data.entity.WorldRuleEntity

data class SettingDocumentSnapshot(
    val player: PlayerEntity?,
    val survivalStatus: SurvivalStatusEntity?,
    val settingEntries: List<SettingEntryEntity>,
    val worldRules: List<WorldRuleEntity>,
    val guides: List<GuideEntity>,
    val attributes: List<AttributeEntity>,
    val talents: List<TalentEntity>,
    val tasks: List<TaskEntity>,
    val projects: List<ProjectEntity>,
    val assets: List<AssetEntity>,
    val places: List<PlaceEntity>,
    val lifeStages: List<LifeStageEntity>,
    val playerStyles: List<PlayerStyleEntity>,
    val hiddenQuests: List<HiddenQuestEntity>,
    val randomEvents: List<RandomEventEntity>,
    val lifeArchives: List<LifeArchiveEntity>,
    val libraryContents: List<LibraryContentEntity>,
    val debuffs: List<DebuffEntity>
)
