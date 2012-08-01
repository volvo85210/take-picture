package com.example.picture;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import android.app.Activity;  
import android.content.Intent;  
import android.database.Cursor;  
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;  
import android.os.Bundle;  
import android.os.Environment;  
import android.provider.MediaStore;  
import android.util.Log;
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity  implements OnClickListener {    
	  private static final int RESULT_CAPTURE_IMAGE = 1;// 照相的requestCode    
	  private static final int REQUEST_CODE_TAKE_VIDEO = 2;// 摄像的照相的requestCode    
	      
	         private String strImgPath = "";// 照片文件绝对路径    
	         private String strVideoPath = "";// 视频文件的绝对路径    
	         Canvas canvas;
	         Button buttonShot;  
	         Button buttonVideo;   
	         String path;
	         @Override    
	         protected void onCreate(Bundle savedInstanceState) {    
	                 super.onCreate(savedInstanceState);    
	                 this.setContentView(R.layout.activity_main);    
	                 buttonShot = (Button)findViewById(R.id.ButtonShot);  
	                 buttonShot.setOnClickListener(this);  
	                 buttonVideo = (Button)findViewById(R.id.ButtonVideo);  
	                 buttonVideo.setOnClickListener(this);  

	         }    
	     
	        @Override    
	         protected void onActivityResult(int requestCode, int resultCode, Intent data) {    
	                 super.onActivityResult(requestCode, resultCode, data);    
	                 switch (requestCode) {    
	                case RESULT_CAPTURE_IMAGE://拍照    
	                         if (resultCode == RESULT_OK) {    
	                                 Toast.makeText(this, strImgPath, Toast.LENGTH_SHORT).show();    
	                                 Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	                                 ImageView ivTest = (ImageView)findViewById(R.id.ivTest);
	                                 
	      
	                                 // Best of quality is 80 and more, 3 is very low quality of image 
	                                 Bitmap bJPGcompress = codec(thumbnail, Bitmap.CompressFormat.PNG, 3);
	                                 ivTest.setImageBitmap(bJPGcompress);
	                                 httppost(bJPGcompress);///////////傳取得的照片出去
	                         }    
	                         break;    
	                 case REQUEST_CODE_TAKE_VIDEO://拍摄视频    
	                         if (resultCode == RESULT_OK) {    
	                                 Uri uriVideo = data.getData();    
	                                 Cursor cursor=this.getContentResolver().query(uriVideo, null, null, null, null);    
	                                 if (cursor.moveToNext()) {    
	                                         /* _data：文件的绝对路径 ，_display_name：文件名 */    
	                                         strVideoPath = cursor.getString(cursor.getColumnIndex("_data"));    
	                                         Toast.makeText(this, strVideoPath, Toast.LENGTH_SHORT).show();    
	                                 }    
	                         }    
	                         break;     
	                 }    
	         }    

	        private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,int quality) {
	    		ByteArrayOutputStream os = new ByteArrayOutputStream();
	    		src.compress(format, quality, os);
	     
	    		byte[] array = os.toByteArray();
	    		return BitmapFactory.decodeByteArray(array, 0, array.length);
	    	}    
	        
	        
	        /////////////post照片和資料////////////
	        public void httppost(Bitmap bitmap){//目前引數(BITMAP)沒有用到
	        	URL url;
	        	FileInputStream fileInputStream;
				try {
					fileInputStream = new FileInputStream(new File(path));//path是新照照片的檔案路徑
				
	        	final String BOUNDARY 	= "==================================";//
	        	final String HYPHENS 	= "--";								   //android 傳FILE固定格式需要用到的字串
	        	final String CRLF 		= "\r\n";							   //
	        	
				try {
					url = new URL("http://cyc.mrshih.com/dog/upload.php");
				
	            HttpURLConnection URLConn = (HttpURLConnection) url.openConnection(); 
	       
	            URLConn.setDoOutput(true); 
	            URLConn.setDoInput(true); 
	            ((HttpURLConnection) URLConn).setRequestMethod("POST"); 
	            URLConn.setUseCaches(false); 	          
	            URLConn.setInstanceFollowRedirects(false);
	            
                URLConn.setRequestProperty("Connection", "Keep-Alive");///////////////////////**********
	            
	            URLConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
	        	DataOutputStream dataOS = new DataOutputStream(URLConn.getOutputStream());//開OUTPUT串流	            

	        	////////////////////寫FILE////////////////DOWN
	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"name\""+CRLF);
	        	dataOS.writeBytes(CRLF);
	        	//String t =URLEncoder.encode("王", "utf-8");
	        	
	        	dataOS.writeBytes(URLEncoder.encode("王", "utf-8")+CRLF);
	        	
	        	
	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"phone\""+CRLF);
	        	dataOS.writeBytes(CRLF);
	        	dataOS.writeBytes(URLEncoder.encode("0933394940", "utf-8")+CRLF);
	        	
	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"address\""+CRLF);
	        	dataOS.writeBytes(CRLF);
	        	dataOS.writeBytes(URLEncoder.encode("屏東縣", "big5")+CRLF);
	        	
	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"gps\""+CRLF);
	        	dataOS.writeBytes(CRLF);
	        	dataOS.writeBytes(URLEncoder.encode("22.646066,120.328876", "utf-8")+CRLF);
	        	dataOS.writeBytes(CRLF);
	        	
	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	            String strContentDisposition = "Content-Disposition: form-data; name=\"file\"; filename=\"temp.jpeg\"";
	            String strContentType = "Content-Type: image/jpeg";
	            
	        	dataOS.writeBytes(strContentDisposition+CRLF);	// 寫(Disposition)
	        	dataOS.writeBytes(strContentType+CRLF);			// 寫(Content Type)
	        	dataOS.writeBytes(CRLF);	
	        	
	        	int iBytesAvailable = fileInputStream.available();
	        	byte[] byteData = new byte[iBytesAvailable];
	            int iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
	            
	            /////寫照片//
	        	while (iBytesRead > 0) {
	        		dataOS.write(byteData, 0, iBytesAvailable);	// 開始寫內容
	        		iBytesAvailable = fileInputStream.available();
	        		iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
	        	}
	        	//////寫照片//
	        	
	        	//dataOS.writeBytes(CRLF+HYPHENS+BOUNDARY+CRLF);// 寫--==================================
	        	//String content = URLEncoder.encode("name", "UTF-8"); 
	        	//dataOS.writeBytes(content+CRLF);//寫出資料 
	        	
	        	
	        	dataOS.writeBytes(CRLF+HYPHENS+BOUNDARY+HYPHENS+CRLF);	// (結束)寫--==================================--		
	        	fileInputStream.close();
	        	
	        	
	        //	URLConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//單獨POST用 不傳FILE之格式 但不知為什麼出錯了
	            URLConn.connect();  
	          
	            
		     // content+= URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode("0988123123", "UTF-8")+"\r\n";
		        
	       
	            dataOS.flush();
	            dataOS.close(); // flush and close
	            
	            java.io.BufferedReader rd = new java.io.BufferedReader( new java.io.InputStreamReader(URLConn.getInputStream())); //開INPUT串流
	            
	            /*收PHP ECHO的字串*/
	            String line; 
	            Log.e("%%%%%%%%%%%%%%%%%", path);
	            while ((line = rd.readLine()) != null) { 
	             Log.e("##################",line); 
	            } 	            
	            /**********************/
	            rd.close(); 

	            URLConn.disconnect();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
	        }
	         /**  
	          * 照相功能  
	          */    
	         private void cameraMethod() {    
	        	 
	                 Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    
	                 strImgPath = Environment.getExternalStorageDirectory().toString() + "/CONSDCGMPIC/";//存放照片的文件夹    
	                 
	                 Log.e("@@@@@@@@@@@@@@", Environment.getExternalStorageDirectory().toString());
	                 
	                 String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";//照片命名    
	                 
	                 File out = new File(strImgPath);    
	                 if (!out.exists()) {    
	                         out.mkdirs();    
	                 }    
	                 out = new File(strImgPath, fileName);    
	                 strImgPath = strImgPath + fileName;//该照片的绝对路径    
	                Uri uri = Uri.fromFile(out);    
	                 imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);    
	                 imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);    
	                 path=uri.getPath();
	                 startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);    
	     
	         }    
	     
	         /**  
	          * 拍摄视频  
	          */    
	         private void videoMethod() {    
	                 Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);    
	                 intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);    
	                 startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);    
	        }    
	     
	        /**  
	          * 录音功能  
	          */       
	     
	         /**  
	          * 提示信息  
	          * @param text  
	          * @param duration  
	          */    
	        private void showToast(String text, int duration) {    
	                 Toast.makeText(MainActivity.this, text, duration).show();    
	        }
	        public void onClick(View v) {  	        	
				int id = v.getId();  
		    	switch(id){  
		    		case R.id.ButtonShot:  
		    				cameraMethod();  
		    				break;  
		    		case R.id.ButtonVideo:  
		    				videoMethod();  
		    				break;  
		    	}  
	       }    
	           
	}  