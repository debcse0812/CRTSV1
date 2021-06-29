package com.example.crts;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class WelcomeSplash extends Activity {
    @Override
    protected void onCreate(Bundle sa){
        super.onCreate(sa);

        try{
            VideoView videoView = new VideoView(this);
            setContentView(videoView);
            Uri path = Uri.parse("android.resource://".concat(getPackageName()).concat("/raw/").concat(String.valueOf(R.raw.welcome_splash)));

            videoView.setVideoURI(path);

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    jump();
                }
            });
            videoView.start();
        }catch (Exception e){
            jump();
        }
    }

    private void jump() {
        if(isFinishing())
            return;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
