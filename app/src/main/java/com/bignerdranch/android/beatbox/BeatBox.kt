package com.bignerdranch.android.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import java.io.IOException
import java.lang.Exception

private const val TAG = "BeatBox"
private const val SOUND_FOLDER = "sample_sound"
private const val MAX_SOUNDS = 5


class BeatBox (private val asset: AssetManager) {
    val sounds: List<Sound>
    private val soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_SOUNDS)
            .build()

    init {
        sounds = loadSounds()
    }
    fun play(sound: Sound){
        sound.soundId?.let {
            soundPool.play(it,1.0f,1.0f,1,0,1.0f)
        }
    }
    fun release(){
        soundPool.release()
    }

   private fun loadSounds(): List<Sound> {
        val soundNames: Array<String>
        try {
            soundNames = asset.list(SOUND_FOLDER)!!
        } catch (e: Exception) {
            Log.e(TAG, "Could not list assets", e)
            return emptyList()
        }
        val sounds = mutableListOf<Sound>()
        soundNames.forEach { filename ->
            val assetPath = "$SOUND_FOLDER/$filename"
            val sound = Sound(assetPath)
            try {
                load(sound)
                sounds.add(sound)
            }catch (ioe: IOException){
                Log.e(TAG,"Count not load sound $filename",ioe)
            }
        }
        return sounds
    }

    private fun load(sound: Sound){
        val afd: AssetFileDescriptor = asset.openFd(sound.assetPath)
        val soundId = soundPool.load(afd,1)
        sound.soundId = soundId
    }
}
