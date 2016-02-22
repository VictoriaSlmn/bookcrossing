package victoriaslmn.bookcrossing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import victoriaslmn.bookcrossing.data.document.DocumentDto;
import victoriaslmn.bookcrossing.data.user.UserDto;


public class OrmLiteSqlite extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "bookcrossing.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<UserDto, Long> userDao = null;
    private Dao<DocumentDto, Long> documentDao = null;

    public OrmLiteSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserDto.class);
            TableUtils.createTable(connectionSource, DocumentDto.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, DocumentDto.class, true);
            TableUtils.dropTable(connectionSource, UserDto.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<UserDto, Long> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(UserDto.class);
        }
        return userDao;
    }

    public Dao<DocumentDto, Long> getDocumentDao() throws SQLException {
        if (documentDao == null) {
            documentDao = getDao(DocumentDto.class);
        }
        return documentDao;
    }

    @Override
    public void close() {
        super.close();
        documentDao = null;
        userDao = null;
    }
}