package com.soloscape.util.model

import android.annotation.SuppressLint
import com.soloscape.util.toRealmInstant
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant


open class Report : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var ownerId : String = ""
    var mood : String = Mood.Neutral.name
    var title : String = ""
    var description : String = ""
    var images : RealmList<String> = realmListOf()
    @SuppressLint("NewApi")
    var date : RealmInstant = Instant.now().toRealmInstant()
}