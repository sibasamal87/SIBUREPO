package com.hnsamalco.music.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerMenuAdapter extends ArrayAdapter<String> {

	private int resource;
	private List<String> menuList;
	private LayoutInflater inflater;
	
	public DrawerMenuAdapter(Context context, int resource, List<String> menuList) {
		super(context, resource, menuList);
		// TODO Auto-generated constructor stub
		this.resource=resource;
		this.menuList=menuList;
		inflater = ((Activity)context).getLayoutInflater();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder=null ;
		
		if(convertView==null){
			
			holder = new ViewHolder();
			convertView = inflater.inflate(resource,parent,false);
			holder.image = (ImageView)convertView.findViewById(com.hnsamalco.music.R.id.menuimage);
			holder.text = (TextView)convertView.findViewById(com.hnsamalco.music.R.id.menuText);
									
			convertView.setTag(holder);
			
		}else{
			
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.image.setImageResource(com.hnsamalco.music.R.drawable.ic_launcher);
		holder.text.setText(menuList.get(position));
		
		return convertView;
	}
	
	class ViewHolder{
		
		ImageView image;
		TextView text;
	}

}
