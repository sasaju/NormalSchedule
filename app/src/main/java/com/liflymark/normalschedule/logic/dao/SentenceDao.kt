package com.liflymark.normalschedule.logic.dao

import android.content.Context
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.model.Sentence

object SentenceDao {

    suspend fun saveSentence(sentences: List<Sentence>){
        val emptySentence = Sentence("","")
        with(normalSharePreferences().edit()){
            for (i in 0..2){
                putString("$i", sentences.getOrElse(i){emptySentence}.sentence)
                putString("${i}a", sentences.getOrElse(i){emptySentence}.author)
            }
            apply()
        }
    }

    suspend fun getSentences(): List<Sentence> {
        val sentences = mutableListOf<Sentence>()
        for (i in 0..2){
            val oneSentence = normalSharePreferences().getString("$i", "")
            val oneSentenceAuthor = normalSharePreferences().getString("${i}a", "")
            if (oneSentence != null && oneSentenceAuthor != null) {
                sentences.add(Sentence(oneSentence, oneSentenceAuthor))
            } else {
                sentences.add(Sentence("",""))
            }
        }
        return sentences.toList()
    }

    fun isSentenceSaved() = normalSharePreferences().contains("0")

    private fun normalSharePreferences() = NormalScheduleApplication.context.getSharedPreferences("normal_sentence", Context.MODE_PRIVATE)
}