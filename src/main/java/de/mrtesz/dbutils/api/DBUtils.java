package de.mrtesz.dbutils.api;

import de.mrtesz.dbutils.utils.Init;
import de.mrtesz.dbutils.utils.exceptions.DatabaseException;
import de.mrtesz.dbutils.utils.logger.DBLogger;
import de.mrtesz.dbutils.utils.logger.DebugLevel;
import de.mrtesz.dbutils.utils.mariadb.MariaDBManager;
import de.mrtesz.dbutils.utils.sqlite.SqliteManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DBUtils {

    @Getter
    private static DBUtils instance;

    private final Init init;

    public DBUtils() {
        this.init = new Init();

        instance = this;
    }
    
    public static void initialize() {
        new DBUtils();
    }

    /** Logs an exception */
    public void logException(Throwable throwable) {
        init.getLogger(DebugLevel.LEVEL0).logException(throwable);
    }

    /**
     * Creates a DBLogger wich you can use for Logging
     * @param debugLevel The Level of Logging used for your Message
     */
    public DBLogger getLogger(DebugLevel debugLevel) {
        return init.getLogger(debugLevel);
    }

    /** Creates a DBLogger with a specific ProjectName for specific logs */
    public DBLogger getLogger(DebugLevel debugLevel, String projectName) {
        return init.getLogger(debugLevel, projectName);
    }

    public static DBLogger logging(DebugLevel debugLevel) {
        return getInstance().getLogger(debugLevel);
    }
    public static DBLogger logging(DebugLevel debugLevel, String projectName) {
        return getInstance().getLogger(debugLevel, projectName);
    }

    /**
     * Creates a MariaDBManager for your Project
     * @param projectName The name of the Project using the method
     * @param infoWhenCredentialsAreNull Should it be logged, when url, user or password is null?
     * @param name Name of Database wich is used, when null "Main"
     * @param url Url for the Database
     * @param user User for the Database
     * @param password Password for the Database
     * @return A functional MariaDBManager
     */
    public MariaDBManager createMariaDBManager(@NotNull String projectName, boolean infoWhenCredentialsAreNull, @Nullable String name,
                                               @Nullable String url, @Nullable String user, @Nullable String password) throws DatabaseException {
        return new MariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, null, projectName);
    }

    /**
     * Creates a SqliteManager for your Project
     * @param projectName The name of the Project using the method
     * @param infoWhenCredentialsAreNull Should it be logged, when url, user or password is null?
     * @param path Path to the .db file
     * @param name Name of Database and name of the .db file
     * @return A functional MariaDBManager
     */
    public SqliteManager createSqliteManager(@NotNull String projectName, boolean infoWhenCredentialsAreNull, @Nullable String path,
                                              @NotNull String name) throws DatabaseException {
        return new SqliteManager(infoWhenCredentialsAreNull, path, name, null, projectName);
    }
}
