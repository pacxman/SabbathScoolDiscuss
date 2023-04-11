package com.ssdiscusskiny.adapters;


import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ssdiscusskiny.R;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.fragments.LessonEight;
import com.ssdiscusskiny.fragments.LessonFive;
import com.ssdiscusskiny.fragments.LessonFour;
import com.ssdiscusskiny.fragments.LessonOne;
import com.ssdiscusskiny.fragments.LessonSeven;
import com.ssdiscusskiny.fragments.LessonSix;
import com.ssdiscusskiny.fragments.LessonThree;
import com.ssdiscusskiny.fragments.LessonTwo;
import com.ssdiscusskiny.tools.TopProgressBar;
import com.ssdiscusskiny.utils.Parser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainPageAdapter extends FragmentPagerAdapter
{

	private List<String> pages;
	private TopProgressBar pbar;
	
	private Parser parse;
	private Context context;

	private final Fragment fOne,fTwo,fThree,fFour,fFive,fSix,fSeven,fEight;
	
	public MainPageAdapter(Context context, FragmentManager fm, List<String> pages, Parser parse, TopProgressBar pbar)
	{
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.pages = pages;
		this.pbar = pbar;
		this.context = context;
		
		this.parse = parse;
		fOne = LessonOne.newInstance(Variables.dateIds.get(0), parse, pbar);
		fTwo = LessonTwo.newInstance(Variables.dateIds.get(1), parse, pbar);
		fThree = LessonThree.newInstance(Variables.dateIds.get(2), parse, pbar);
		fFour = LessonFour.newInstance(Variables.dateIds.get(3), parse, pbar);
		fFive = LessonFive.newInstance(Variables.dateIds.get(4), parse, pbar);
		fSix = LessonSix.newInstance(Variables.dateIds.get(5), parse, pbar);
		fSeven = LessonSeven.newInstance(Variables.dateIds.get(6), parse, pbar);
		fEight = LessonEight.newInstance(Variables.dateIds.get(7), parse, pbar);
	}

	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return 8;
	}

	@Override
	public Fragment getItem(int pos)
	{
		// TODO: Implement this method
		switch (pos)
		{
			case 0:
				return fOne;
			case 1:
				return fTwo;
			case 2:
				return fThree;
			case 3:
				return fFour;
			case 4:
				return fFive;
			case 5:
				return fSix;
			case 6:
				return fSeven;
			case 7:
				return fEight;
			
			default: return LessonOne.newInstance("no id", parse, pbar);
		}
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		String date = pages.get(position);
		String dateNow = new SimpleDateFormat("EEEE\ndd/MM/yyyy").format(Calendar.getInstance().getTime());
		Log.v("MPADT", date);
		if (date.equals(dateNow)) return context.getString(R.string.today_new)+new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
		else return pages.get(position);
	}


};