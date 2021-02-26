package com.yanantec.ynarc;

import com.yanantec.annotation.LinkedHashMapCreate;
import com.yanantec.annotation.MapCreate;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/2
 * Describe
 */
@LinkedHashMapCreate(key = "111", position = 1)
@MapCreate(key = "22")
public class Test
{
    private int mAge;
    private void setAge(){

    }

    class BalloonFactory{
        Balloon createBallon(int color){
            switch (color){
                case 1:return new RedBalloon();
                case 2: return new WhiteBalloon();
            }
            return null;
        }
    }

    interface Balloon{
        void setcolor();
    }

    class RedBalloon implements Balloon{
        @Override
        public void setcolor()
        {

        }

    }

    class WhiteBalloon implements Balloon{
        @Override
        public void setcolor()
        {

        }
    }

//    public static void main(){
//
//    }


}
