package com.example.mediaproviderdemo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MediaProviderDemo extends Activity {

	Button add;
	Button view;
	ListView show;
	
	ArrayList<String> names = new ArrayList<String>();
	ArrayList<String> descs = new ArrayList<String>();
	ArrayList<String> fileNames = new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        add = (Button)findViewById(R.id.add);
        view = (Button)findViewById(R.id.view);
        show = (ListView)findViewById(R.id.show);
        
        //Ϊadd��ť�ĵ����¼����¼�������
        add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//����ContentValues����׼����������
				ContentValues values = new ContentValues();
				values.put(Media.DISPLAY_NAME, "a");
				values.put(Media.DATE_MODIFIED, "��Ů");
				values.put(Media.MIME_TYPE, "image/jpeg");
				//�������ݣ��������������ݶ�Ӧ��Uri
				Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
				//����Ӧ�ó����µ�aͼƬ
				Bitmap bitmap = BitmapFactory.decodeResource(MediaProviderDemo.this.getResources(), R.drawable.a);
				
				OutputStream os = null;
				try 
				{
					os = getContentResolver().openOutputStream(uri); //��ȡ�ղ�������ݵ�Uri��Ӧ�������
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os); //��bitmapͼƬ���浽Uri��Ӧ�����ݽڵ���
					os.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		});
        
      //Ϊview��ť�ĵ����¼����¼�������
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ���names��descs��fileNames������ԭ�е�����
				names.clear();
				descs.clear();
				fileNames.clear();
				//ͨ��ContentResolver��ѯ����ͼƬ��Ϣ
				Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, null, null, null);
				while (cursor.moveToNext())
				{
					String name = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)); //��ȡͼƬ����ʾ��
					String desc = cursor.getString(cursor.getColumnIndex(Media.DATE_MODIFIED)); //��ȡͼƬ����ϸ����
					byte[] data = cursor.getBlob(cursor.getColumnIndex(Media.DATA)); //��ȡͼƬ�ı���λ�õ�����
					
					names.add(name); //��ͼƬ��ӵ�names������
					descs.add(desc); //��ͼƬ������ӵ�descs������
					fileNames.add(new String(data, 0, data.length - 1)); //��ͼƬ����·����ӵ�fileNames������
				}
				//����һ��List���ϣ�List���ϵ�Ԫ����Map
				List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
				//��names��descs�������϶��������ת����Map������
				for (int i = 0; i < names.size(); i++)
				{
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("name", names.get(i));
        			listItem.put("desc", descs.get(i));
        			listItems.add(listItem);
				}
				//����һ��SimpleAdapter
				SimpleAdapter simpleAdapter = new SimpleAdapter(MediaProviderDemo.this, 
						listItems, R.layout.line, new String[] {"name", "desc"}, new int[] {R.id.name, R.id.desc});
				show.setAdapter(simpleAdapter); //Ϊshow ListView�������Adapter
			}
		});
        
      //Ϊshow ListView���б�����¼���Ӽ�����
        show.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View source, int position, long id) {
				//����view.xml���沼�ִ������ͼ
				View viewDialog = getLayoutInflater().inflate(R.layout.view, null);
				//��ȡviewDialog��IDΪimage�����
				ImageView image = (ImageView)viewDialog.findViewById(R.id.image);
				image.setImageBitmap(BitmapFactory.decodeFile(fileNames.get(position)));
				//ʹ�öԻ�����ʾ�û�������ͼƬ
				new AlertDialog.Builder(MediaProviderDemo.this)
				.setView(viewDialog)
				.setPositiveButton("ȷ��", null)
				.show();
			}
		});
    }
}
