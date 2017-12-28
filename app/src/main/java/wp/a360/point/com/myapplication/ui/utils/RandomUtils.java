package wp.a360.point.com.myapplication.ui.utils;

import java.util.Random;

import com.wp.point.qj.jb.R;

/**
 * 随机生成工具
 * Created by DN on 2017/12/9.
 */

public class RandomUtils {


    public static int [] color = {
            R.color.random_color1,R.color.random_color2, R.color.random_color3,
            R.color.random_color4, R.color.random_color5,R.color.random_color6,
            R.color.random_color8, R.color.random_color9,
            R.color.random_color10, R.color.random_color12,
            R.color.random_color13,R.color.random_color14,R.color.random_color15,
            R.color.random_color16,R.color.random_color18,
            R.color.random_color19,R.color.random_color20,
    };
    /**
     * 随机生成颜色
     * @return
     */
    public static int getRandomColor(){
        Random random = new Random();
        int i = random.nextInt(color.length);
        return color[i];
    }
    /*public static void main(String arg[]){
        int num=(int) (Math.random()*16777216);
        String hex = Integer.toHexString(num);
        System.err.println(hex);
    }*/
}
