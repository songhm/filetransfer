package com.example.file_transfer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.file_transfer.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * �ļ�����࣬�������û�ѡ���ļ���������Ҫ�����ļ��ľ���·��
 * 
 */
public class FileActivity extends BaseActivity implements OnItemClickListener{
	
	private final static String TAG = "FileActivity";
	private String path = "/";	//��ǰ·��
	private Button mselect_back;
	private ListView itemList;
	private List<Map<String, Object>> adapterList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.files);
		mApplication.addActivity(this);
		refreshListItems(path);
	}
	@Override  
    protected void onResume() {  
        super.onResume(); 
      //����activity��Ϊ��ǰActivity
      mApplication.setCurrentActivity(this); 
    }  

	@Override
	protected void onDestroy(){
		mApplication.deleteActivity(this);
		super.onDestroy();
	}
	// ˢ��ListView
	private void refreshListItems(String path) {
		// TODO Auto-generated method stub
		//��ȡ�ؼ�
		findViews();
		//���ÿؼ�����
		setListener();
		adapterList = buildListForSimpleAdapter(path);
		SimpleAdapter listAdapter = new SimpleAdapter(this, adapterList, R.layout.file_item, 
				new String[]{"name", "img"}, 
				new int[]{R.id.file_name, R.id.file_img});
		
		itemList.setAdapter(listAdapter);
		itemList.setOnItemClickListener(this);
		itemList.setSelection(0);
		
	}

	private List<Map<String, Object>> buildListForSimpleAdapter(String path) {
		// TODO Auto-generated method stub
		File nowFile = new File(path);
		
		
		adapterList = new ArrayList<Map<String, Object>>();
		
		//���ϸ�Ŀ¼
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("name", "��Ŀ¼/");
		root.put("img", R.drawable.img_dictionary2);
		root.put("path", "�ظ�Ŀ¼");
		adapterList.add(root);
		
		if(!nowFile.isDirectory()){	//���ǵ�ǰ·����Ӧ�����ļ����򷵻�
			return adapterList;
		}
		
		File[] files = nowFile.listFiles();	//�õ�path�������ļ�
		
		for(File file:files){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", file.getName());
			map.put("path", file.getPath());
			if(file.isDirectory()){
				map.put("img", R.drawable.img_dictionary3);
			}else{
				String type = getFileType(file.getName());
				if(isMusic(type)){
					map.put("img", R.drawable.img_music);
				}else if(isVideo(type)){
					map.put("img", R.drawable.img_video);
				}else if(isDoc(type)){
					map.put("img", R.drawable.img_doc);
				}else if(isPpt(type)){
					map.put("img", R.drawable.img_ppt);
				}else if(isExcel(type)){
					map.put("img", R.drawable.img_xls);
				}else if(isImage(type)){
					map.put("img", R.drawable.img_image);
				}else if(isPdf(type)){
					map.put("img", R.drawable.img_pdf);
				}else if(isRar(type)){
					map.put("img", R.drawable.img_rar);
				}else if(isZip(type)){
					map.put("img", R.drawable.img_zip);
				}else if(isTxt(type)){
					map.put("img", R.drawable.img_txt);
				}else{
					map.put("img", R.drawable.img_other);
				}
			}
			adapterList.add(map);
		}
		
		return adapterList;
	}
	@Override
	protected void findViews() {
		// TODO Auto-generated method stub
		itemList = (ListView) findViewById(R.id.file_detail);
		mselect_back = (Button) findViewById(R.id.select_back);
		
	}
	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mselect_back.setOnClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		Log.i(TAG, "λ��[" + position + "]�ϵ�item�����");
		if(position == 0){	//�ظ�Ŀ¼
			path = "/";
			refreshListItems(path);
		}else{
			
			path = (String) adapterList.get(position).get("path");
			File file = new File(path);
			if(!file.isDirectory()){	//���ǵ�ǰ·����Ӧ�����ļ�
				//����
			    showAskDialog();
			}
			else refreshListItems(path);
		}
	}

	private void goToParent() {
		// TODO Auto-generated method stub
		File file = new File(path);
		File pFile = file.getParentFile();	//�õ����ļ�
		if(pFile == null){
			Toast.makeText(this,R.string.noback, 
					Toast.LENGTH_SHORT).show();
			refreshListItems(path);
		}else{
			path = pFile.getAbsolutePath();
			refreshListItems(path);
		}
	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.select_back){
			Intent intent = new Intent(FileActivity.this,FriendsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); 
			startActivity(intent);
		}
	}

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goToParent();
			return true;
		} else return super.onKeyDown(keyCode, event);
	}
	private void showAskDialog() {
			new AlertDialog.Builder(FileActivity.this)
					.setMessage("ȷ�Ϸ��� \""+getFileName()+"\"?")
					.setTitle(R.string.remind)
					.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent =getIntent();    
									intent.putExtra("filePaths", path);
									//Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
									setResult(RESULT_OK, intent);
									finish();
									dialog.dismiss();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();

	}
	public String getFileName()
	{
		return path.substring(path.lastIndexOf("/")+1);
	}
	/**
     * ��ȡ�ļ���׺��
     * 
     * @param fileName
     * @return �ļ���׺��
     */
    @SuppressLint("DefaultLocale")
	public static String getFileType(String fileName) {
            if (fileName != null) {
                    int typeIndex = fileName.lastIndexOf(".");
                    if (typeIndex != -1) {
                            String fileType = fileName.substring(typeIndex + 1).toLowerCase();
                            return fileType;
                    }
            }
            return "";
    }

    /**
     * ���ݺ�׺���ж��Ƿ���ͼƬ�ļ�
     * 
     * @return �Ƿ���ͼƬ���true or false
     */
    public static boolean isImage(String type) {
            if (type != null
                            && (type.equals("jpg") || type.equals("gif")
                                            || type.equals("png") || type.equals("jpeg")
                                            || type.equals("bmp") || type.equals("wbmp")
                                            || type.equals("ico") || type.equals("jpe"))) {
                    return true;
            }
            return false;
    }
    public static boolean isVideo(String type) {
        if (type != null
                        && (type.equals("asx") || type.equals("wmv")
                                        || type.equals("asf") || type.equals("rm")
                                        || type.equals("mpg") || type.equals("rmvb")
                                        || type.equals("mpeg") || type.equals("mpe")
                                        || type.equals("3gp")|| type.equals("mov")
                                        || type.equals("m4v")|| type.equals("mp4")
                                        || type.equals("avi")|| type.equals("mkv")
                                        || type.equals("flv"))) {
                return true;
        }
        return false;
    }
    public static boolean isMusic(String type){
    	if (type != null
                && (type.equals("asx") || type.equals("mp3")
                                || type.equals("wma")|| type.equals("waw") || type.equals("m4a")
                                || type.equals("mod") || type.equals("ogg")
                                || type.equals("ra")|| type.equals("flac")
                                || type.equals("cd")|| type.equals("ape")
                                || type.equals("asf")|| type.equals("mid")
                                || type.equals("aac"))) {
    		return true;
    	}
    	return false;
    }
    public static boolean isPdf(String type) {
        if (type != null
                        && (type.equals("pdf"))) {
                return true;
        }
        return false;
    }
    public static boolean isDoc(String type) {
        if (type != null
                        && (type.equals("doc")||(type.equals("docx")))) {
                return true;
        }
        return false;
    }
    public static boolean isPpt(String type) {
        if (type != null
                        && (type.equals("ppt")||(type.equals("pptx")))) {
                return true;
        }
        return false;
    }
    public static boolean isExcel(String type) {
        if (type != null
                        && (type.equals("xls"))) {
                return true;
        }
        return false;
    }
    public static boolean isRar(String type) {
        if (type != null
                        && ((type.equals("rar")))) {
                return true;
        }
        return false;
    }
    public static boolean isZip(String type) {
        if (type != null
                        && ((type.equals("zip")))) {
                return true;
        }
        return false;
    }
    public static boolean isTxt(String type) {
        if (type != null
                        && ((type.equals("txt")))) {
                return true;
        }
        return false;
    }


	
    
}

