package Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ThanhSon on 3/17/2018.
 */

public class DatabaseBook extends AppCompatActivity{
    private String Name;
    private String Author;
    private Bitmap Jacket;
    private int Reading;
    private int ReadingLine;
    public static boolean IsSeeAdmod = true;
    private String PageFB;
    private String Email;
    private String[][] Category;

    private String UserId;
    private String UserName;
    private int TextSize;
    private String TimeRead;
    private boolean IsRemind;
    private int ThemeId;
    private int TimerScroll;

    String TABLE_NAME_USER = "tbUser";
    String TABLE_NAME_CONFIG = "tbConfig";
    String TABLE_NAME_CATEGORY = "tbCategory";
    String TABLE_NAME_CONTENT = "tbContent";
    String DATABASE_NAME = "DatabaseBook.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;

    public void DatabaseBook(){
        readConfig();
    }

    public int[] getIdWordSearch(String text)
    {
        open();
        int[] result;
        try {
            Cursor cursor=database.rawQuery("SELECT id FROM "+ TABLE_NAME_CONTENT +" WHERE Content LIKE '%"+text+"%'", null);
            result = new int[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext())
            {
                result[i] = cursor.getInt(0);
                i++;
            }
            close();
            return result;
        }
        catch (Exception ex)
        {
            close();
            result = new int[1];
            result[0] = -1;
            return result;
        }
    }

    public String getName() {
        return Name;
    }

    public String getAuthor() {
        return Author;
    }

    public Bitmap getJacket() {
        return Jacket;
    }

    public int getReading() {
        return Reading;
    }

    public int getReadingLine() {
        return ReadingLine;
    }

    public boolean isSeeAdmod() {
        return IsSeeAdmod;
    }

    public String getPageFB() {
        return PageFB;
    }

    public String getEmail() {
        return Email;
    }

    public String[][] getCategory() {
        readCategory();
        return Category;
    }

    public void setReading(int reading) {
        edit(TABLE_NAME_CONFIG, "reading", reading+"", "id", 1);
        Reading = reading;
    }

    public void setReadingLine(int readingLine) {
        edit(TABLE_NAME_CONFIG, "readingline", readingLine+"", "id", 1);
        ReadingLine = readingLine;
    }

    public void setSeeAdmod(boolean seeAdmod) {
        //edit(TABLE_NAME_CONFIG, "isseeadmob", (seeAdmod==true?1:0)+"", "id", 1);
        IsSeeAdmod = seeAdmod;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public int getTextSize() {
        return TextSize;
    }

    public String getTimeRead() {
        return TimeRead;
    }

    public boolean isRemind() {
        return IsRemind;
    }

    public int getThemeId() {
        return ThemeId;
    }

    public int getTimerScroll() {
        return TimerScroll;
    }

    public void setTimerScroll(int timerScroll) {
        edit(TABLE_NAME_USER, "TimerScroll", timerScroll+"", "id", 1);
        TimerScroll = timerScroll;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setTextSize(int textSize) {
        edit(TABLE_NAME_USER, "textsize", textSize+"", "id", 1);
        TextSize = textSize;
    }

    public void setTimeRead(String time) {
        edit(TABLE_NAME_USER, "timeread", time, "id", 1);
        TimeRead = time;
    }

    public void setRemind(boolean remind) {
        edit(TABLE_NAME_USER, "isremind", (remind?1:0)+"", "id", 1);
        IsRemind = remind;
    }

    public void setThemeId(int themeId) {
        edit(TABLE_NAME_USER, "themeid", themeId+"", "id", 1);
        ThemeId = themeId;
    }

    public void open()
    {
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE,null);
    }

    public void close()
    {
        database.close();
    }

    private void edit(String tbName, String columnName, String value, String idColumnName, int id)
    {
        try {
            open();
            ContentValues row = new ContentValues();
            row.put(columnName, value);
            database.update(tbName, row, idColumnName + "=" + id, null);
        }
        catch (Exception ex)
        {
            Toast.makeText(DatabaseBook.this, ex.toString(), Toast.LENGTH_LONG).show();
        }
        close();
    }

    public boolean readUser()
    {
        try
        {
            open();
            Cursor cursor=database.query(TABLE_NAME_USER, null,null,null,null,null,null);
            cursor.moveToNext();

            UserId = cursor.getString(1);
            UserName = cursor.getString(2);
            TextSize = cursor.getInt(3);
            TimeRead = cursor.getString(4);
            IsRemind = cursor.getInt(5) > 0;
            ThemeId = cursor.getInt(6);
            TimerScroll = cursor.getInt(7);

            cursor.close();
            close();
            return true;
        }
        catch(Exception ex)
        {
            close();
            return false;
        }
    }

    private boolean readConfig()
    {
        try {
            open();
            Cursor cursor=database.query(TABLE_NAME_CONFIG, null,null,null,null,null,null);
            cursor.moveToNext();

             Name = cursor.getString(1);
             Author = cursor.getString(2);

             byte[] jacketbyte = cursor.getBlob(3);
             ByteArrayInputStream imageStream = new ByteArrayInputStream(jacketbyte);
             Jacket = BitmapFactory.decodeStream(imageStream);

            Reading = cursor.getInt(4);
            //IsSeeAdmod = cursor.getInt(5) > 0;
            PageFB = cursor.getString(6);
            Email = cursor.getString(7);
            ReadingLine = cursor.getInt(8);

            cursor.close();
            close();
            return true;
        }
        catch (Exception ex)
        {
            close();
            return false;
        }
    }

    private boolean readCategory()
    {
        try {
            int i = 0;
            open();
            Cursor cursor=database.query(TABLE_NAME_CATEGORY, null,null,null,null,null,null);
            Category = new String[cursor.getCount()][2];

            while (cursor.moveToNext())
            {
                Category[i][0] = cursor.getString(1);
                Category[i][1] = cursor.getString(2);
                i++;
            }
            cursor.close();
            close();
            return true;
        }
        catch (Exception ex)
        {
            close();
            return false;
        }
    }

    public String readContent(int id)
    {
        try {
            int i = 0;
            open();

            String[] columns = {"id", "Content"};
            String selection = "id=?";
            String[] selectionArgs = {""+id};
            Cursor cursor=database.query(TABLE_NAME_CONTENT, columns, selection,selectionArgs,null,null,null);
            String content = "";//cursor.getString(0);
            if (cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    content += cursor.getString(1);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            close();
            return content;
        }
        catch (Exception ex)
        {
            close();
            return "lỗi "+ id+ ": "+ ex;
        }
    }

    public void copyDatabaseFromAccessToSystem() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if(!dbFile.exists())
        {
            copyDatabaseFromAccess();
        }
    }

    private void copyDatabaseFromAccess() {
        try
        {
            InputStream myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getPathDatabase();

            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();

            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = myInput.read(buffer)) > 0)
            {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception ex) {Toast.makeText(DatabaseBook.this, "lỗi:" + ex.toString(), Toast.LENGTH_SHORT).show(); }
    }

    private String getPathDatabase()
    {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }
}
