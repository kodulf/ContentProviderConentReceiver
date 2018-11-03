package kodulf.contentproviderconentreceiver;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Content Provider 我们一般用不到，用到的都是ContentReslover()
 *
 * ContentResolver(). 诱惑（获get，getContentResolver()）制服（Uri）,查找（resolver.query），编号（cursor.getColumenNameIndex，getString(int)),电话Phone，短信Sms，电话记录Call
 *
 * 诱惑制服查找编号，电话短信通过记录。
 *
 *
 *
 * Content Provider 是Android 提供的第三方应用数据的访问方案，可以派生Content Provider 类，对外提供数据，可以像数据库一样进行
 *
 * 选择排序，屏蔽内部数据的存储细节，向外提供统一的接口模型，大大简化上层应用，对数据的整合提供了更方便的途径
 *
 * 最大的作用就是能够集中管理数据源的操作，，
 *
 * 创建的时候,要继承的方法就是onCreate(),query(),getType(),insert(),delete(),update(),onCreate的方法里面只执行获取上下文和初始化DBHelper的操作，
 *
 * 而不进行打开数据库的操作。getType 就是默认的就可以了。还有要注意的就是在ContentProvider里面query 的时候要记住不能关闭数据库，因为Cursor 要返回呢
 *
 * inset 的时候的返回值，是返回的Uri 应该变成 content://xxx......../students/记录id// 将 记录id与uri进行拼接,返回 新纪录的 uri
 *
 * ret = ContentUris.withAppendedId(uri, rid);
 *
 *
 *
 * (联系人:ContactsContract.Contacts.CONTENT_URI,短信Telephony.SMS.CONTENT_URI)
 *
 *
 *
 * 在另外调用的一方也可以向调用本地数据库一样的方便的使用增删改查。注意了Uri.parse里面是content://再加上authrioty
 *
 *
 *
 * ContentResolver resolver = getContentResolver();
 *
 *         Uri uri = Uri.parse("content://studentsprovider/students");
 *
 *         Cursor cursor = resolver.query(uri,null,null,null,null);
 *
 *         resolver.insert(uri,new ContentValues());
 *
 *         resolver.update(uri,new ContentValues(),null,null);
 *
 *         resolver.delete(uri,null,null);
 *
 *         if(cursor!=null)
 *
 *         cursor.close();
 *
 *
 * ---------------------
 * 作者：千雅爸爸
 * 来源：CSDN
 * 原文：https://blog.csdn.net/Rodulf/article/details/72453296
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ContentResolver contentResolver = getContentResolver();
//        Uri uri = Uri.parse();

//        Cursor query = contentResolver.query(uri, null, null, null, null);

//        ContentValues contentValues = new ContentValues();
//        contentValues.put(ContactsContract.PhoneLookup.DISPLAY_NAME,"hello");
//        contentValues.put(ContactsContract.P);
//        contentResolver.insert(uri,);


//        while (query.moveToNext()){
//            int nameIndex = query.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
//            String name = query.getString(nameIndex);
//            String contactId = query.getString(query.getColumnIndex(ContactsContract.Contacts._ID));
//
//        }
//        if(query!=null){
//            query.close();
//        }


        try {
            getPhoneNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取手机联系人号码,
     * 需要添加下面的权限：不然会抛出权限的异常
     <uses-permission android:name="android.permission.READ_CONTACTS"></uses-permission>
     <uses-permission android:name="android.permission.wRITE_CONTACTS"></uses-permission>
     * 
     */
    public void getPhoneNumber() throws Exception{
        // smslist=getListView();
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext())
        {
            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            //name += (name);
            // 取得联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+ contactId, null, null);
            // 取得电话号码(可能存在多个号码)
            while (phone.moveToNext())
            {
                String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                System.out.println(name+":"+strPhoneNumber);
                //msg += name+":"+strPhoneNumber+"\n";
                //text.setText(msg);
            }
            phone.close();
        }
        cursor.close();
    }
    /**
     * 获取短信信息
     * @return smsBuilder.toString()
     */
    @SuppressWarnings("unused")
    public String getSmsMessage(){

        final String SMS_URI_ALL  = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_SEND = "content://sms/sent";
        final String SMS_URI_DRAFT = "content://sms/draft";

        StringBuilder smsBuilder = new StringBuilder();

        try{
            ContentResolver cr = getContentResolver();
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type"};
            Uri uri = Uri.parse(SMS_URI_ALL);
            Cursor cur = cr.query(uri, projection, null, null, "date desc");

            if (cur.moveToFirst()) {
                String name;
                String phoneNumber;
                String smsbody;
                String date;
                String type;

                int nameColumn = cur.getColumnIndex("person"); //发送人
                int phoneNumberColumn = cur.getColumnIndex("address");  //号码
                int smsbodyColumn = cur.getColumnIndex("body");  //内容
                int dateColumn = cur.getColumnIndex("date");  //时间
                int typeColumn = cur.getColumnIndex("type");  //接收还是发送

                do{
                    name = cur.getString(nameColumn);
                    phoneNumber = cur.getString(phoneNumberColumn);
                    smsbody = cur.getString(smsbodyColumn);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
                    date = dateFormat.format(d);

                    int typeId = cur.getInt(typeColumn);
                    if(typeId == 1){
                        type = "接收";
                    } else if(typeId == 2){
                        type = "发送";
                    } else {
                        type = "";
                    }
                    //System.out.println("nsc :"+name+":"+phoneNumber+":"+smsbody+":"+date+":"+type +"\n");
                    String smsmsg = name+":"+phoneNumber+":"+smsbody+":"+date+":"+type+"\n";
                    //con.add(smsmsg);
                    if(smsbody == null) smsbody = "";
                }while(cur.moveToNext());
            } else {
                smsBuilder.append("no result!");
            }

            smsBuilder.append("getSßmsInPhone has executed!");
        } catch(SQLiteException ex) {
            Log.d("SQLite getSmsInPhone", ex.getMessage());
        }
        return smsBuilder.toString();
    }
}
