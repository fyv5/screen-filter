package com.activity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amastigote.darker.R;


/**
 * Created by fy on 2017/5/11.
 */

public class ExerciseActivity extends AppCompatActivity{

    public ImageView im;
    public Button bt1,bt2;
    public TextView tv;
    public Resources res;
    public int page=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        im=(ImageView)this.findViewById(R.id.imageView2);
        bt1=(Button)this.findViewById(R.id.button10);
        bt2=(Button)this.findViewById(R.id.button11);
        tv=(TextView)this.findViewById(R.id.textView2);
        res=this.getResources();
        final Drawable array[]=new Drawable[6];
        array[0]=res.getDrawable(R.drawable.ex1);
        array[1]=res.getDrawable(R.drawable.ex2);
        array[2]=res.getDrawable(R.drawable.ex3);
        array[3]=res.getDrawable(R.drawable.ex4);
        array[4]=res.getDrawable(R.drawable.ex5);
        array[5]=res.getDrawable(R.drawable.ex6);
        final String strArray[]=new String[6];
        strArray[0]="探天应穴\n以左右大拇指罗纹面接左右眉头下面的上眶角处。其他四指散开弯曲如弓状，支在前额上，按探面不要大。";
        strArray[1]="挤按睛明穴\n以左手或右手大拇指按鼻根部，先向下按、然后向上挤。";
        strArray[2]="揉四白穴\n先以左右食指与中指并拢，放在靠近鼻翼两侧，大拇指支撑在下腭骨凹陷处，然后放下中指，在面颊中央按揉。注意穴位不需移动，按揉面不要太大。";
        strArray[3]="揉四白穴\n先以左右食指与中指并拢，放在靠近鼻翼两侧，大拇指支撑在下腭骨凹陷处，然后放下中指，在面颊中央按揉。注意穴位不需移动，按揉面不要太大。";
        strArray[4]="按太阳穴、轮刮眼眶(太阳、攒竹、鱼腰、丝竹空、瞳子骱、承泣等)\n拳起四指，以左右大拇指罗纹面按住太阳穴，以左右食指第二节内侧面轮刮眼眶上下一圈，上侧从眉头开始，到眉梢为止，下面从内眼角起至外眼角止，先上后下，轮刮上下一圈。";
        strArray[5]="按太阳穴、轮刮眼眶(太阳、攒竹、鱼腰、丝竹空、瞳子骱、承泣等)\n拳起四指，以左右大拇指罗纹面按住太阳穴，以左右食指第二节内侧面轮刮眼眶上下一圈，上侧从眉头开始，到眉梢为止，下面从内眼角起至外眼角止，先上后下，轮刮上下一圈。";
        tv.setText(strArray[0]);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page==0)
                    return;
                page--;
                im.setBackground(array[page]);
                tv.setText(strArray[page]);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page==5)
                    return;
                page++;
                im.setBackground(array[page]);
                tv.setText(strArray[page]);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
