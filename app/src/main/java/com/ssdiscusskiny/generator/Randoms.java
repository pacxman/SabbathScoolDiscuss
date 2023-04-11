package com.ssdiscusskiny.generator;

import android.content.Context;
import android.util.Log;

import com.ssdiscusskiny.R;
import com.ssdiscusskiny.app.Variables;

import java.util.Random;


public class Randoms
{
	private static final Random random = new Random();
	
	private static final int[] anims = {R.anim.bounce,R.anim.bounce_on_x};
    private static final int[] numbers = {1,2};
	public static int getAnim()
	{
		// Select a random hello.
		int tps = anims.length;
		return anims[random.nextInt(tps)];
	}
	public static int toss(boolean firstRun){
        int tps = numbers.length;
        if (firstRun){
            return 0;
        }else{
            return numbers[random.nextInt(tps)];
        }
		
    }
	
	public static String exitsMsgs(Context context){
		int toss;
		Random rand = new Random();
		toss = rand.nextInt();
		if (toss%2==0){
			return context.getString(R.string.ext_mes_3);
		} else{
			return context.getString(R.string.ret_2);
		}
	}



	public static String pushMsg(String msg){
		//String[] msgs = {name+" posted in comments"+name+" sends a comment", name+" write in comment", name+" posts a comment", name+" shared a comment", "Read "+name+"'s comment", "Respond to "+name+"'s comment"};
        //Random random = new Random();
		if (msg.length()<=32){
			return msg;
		}else{
			String subMsg = msg.substring(0, 32);
			return  subMsg;
		}
	}
	public static String tossColor(){
		String[] colors = {"#2C384A","#806517","#827839","#7E3817","#7D0552","#614051","#800517","#810541","#483C32","#493D26","#254117","#728C00","#2B547E","#4863A0","#00f0ff","#887711","#123020","#ff11ee","#ffe631","#421000","#FF7F50","#1B4F72","#6E2C00","#307D7E","#728C00","#254117","#9DC209","#EAC117","#C58917","#806517","#827839","#493C32","#6F4E37","#C36241","#F660AB","#F52887","#FF00FF","#D16587","#800000","#FFA500","#808000","#28353C","#151B54","#151DEC","#38ACEC","#000000"};
		Random random = new Random();
		return colors[random.nextInt(colors.length-1)];
	}
    
}
