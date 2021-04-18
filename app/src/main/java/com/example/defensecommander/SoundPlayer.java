package com.example.defensecommander;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

class SoundPlayer {

    private static final String TAG = "SoundPlayer";
    private static SoundPlayer instance;
    private final SoundPool soundPool;
    private static final int MAX_STREAMS = 10;
    private final HashSet<Integer> loaded = new HashSet<>();
    private final HashMap<String, Integer> soundNameToResource = new HashMap<>();
    private boolean noBackground = true;

    private SoundPlayer() {
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(MAX_STREAMS);
        this.soundPool = builder.build();

        this.soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            Log.d(TAG, "onLoadComplete: #" + sampleId + "  " + status);
            loaded.add(sampleId);
            if (noBackground && loaded.contains(soundNameToResource.get("background"))) {
                Integer resId = soundNameToResource.get("background");
                soundPool.play(resId, 1f, 1f, 1, -1, 1f);
                noBackground = false;
            }
        });
    }

    static SoundPlayer getInstance() {
        if (instance == null)
            instance = new SoundPlayer();
        return instance;
    }

    void setupSound(Context context, String id, int resource) {
        int soundId = soundPool.load(context, resource, 1);
        soundNameToResource.put(id, soundId);

        Log.d(TAG, "setupSound: " + id + ": #" + soundId);
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }


    boolean backgroundReady(){
        if(soundNameToResource.containsKey("background")){
            return true;
        }
        return false;
    }

    void start(final String id) {

        if (!loaded.contains(soundNameToResource.get(id))) {
            Log.d(TAG, "start: SOUND NOT LOADED: " + id);
            return;
        }

        Integer resId = soundNameToResource.get(id);

        if(id.equalsIgnoreCase("background")){
            soundPool.play(resId, 1f, 1f, 1, -1, 1f);
            return;
        }


        if (resId == null)
            return;
        else{
            soundPool.play(resId, 1f, 1f, 1, 0, 1f);
        }
    }

    void stop(final String id){
        soundPool.stop(soundNameToResource.get(id));
    }

    void unload(final String id){
        soundPool.unload(soundNameToResource.get(id));
    }

    void release() { soundPool.release();}
}
