package com.liflymark.normalschedule.logic.dao

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.liflymark.schedule.data.CourseNotice
import java.io.InputStream
import java.io.OutputStream

object CourseNoticeSerializer:Serializer<CourseNotice> {
    override val defaultValue: CourseNotice = CourseNotice
        .getDefaultInstance()
        .toBuilder()
        .setTriggerAtMillis(600000)
        .build()
    override suspend fun readFrom(input: InputStream): CourseNotice {
        try {
            return CourseNotice.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: CourseNotice,
        output: OutputStream
    ) = t.writeTo(output)
}