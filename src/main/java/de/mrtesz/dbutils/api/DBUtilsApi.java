package de.mrtesz.dbutils.api;

import de.mrtesz.dbutils.utils.Init;
import de.mrtesz.dbutils.utils.exceptions.DatabaseException;
import de.mrtesz.dbutils.utils.logger.DBLogger;
import de.mrtesz.dbutils.utils.logger.DebugLevel;
import de.mrtesz.dbutils.utils.mariadb.MariaDBManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DBUtilsApi {

    @Getter
    private static DBUtilsApi instance;

    private final Init init;

    public DBUtilsApi() {
        this.init = new Init();

        instance = this;
    }
    
    public static void initialize() {
        new DBUtilsApi();
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
     * Creates a functional MariaDBManager for your Project
     * @param projectName The name of the Project using the method
     * @param infoWhenCredentialsAreNull Should it be logged, when url, user or password is null?
     * @param name Name of Database wich is used (Only important for /sqldebug), when null "Main"
     * @param url Url for the Database
     * @param user User for the Database
     * @param password Password for the Database
     * @return A functional MariaDBManager
     * @throws DatabaseException when the Project was not initialized before with initializeProject()
     */
    public MariaDBManager createMariaDBManager(@NotNull String projectName, boolean infoWhenCredentialsAreNull, @Nullable String name,
                                               @Nullable String url, @Nullable String user, @Nullable String password) throws DatabaseException {
        return new MariaDBManager(infoWhenCredentialsAreNull, name, url, user, password, null, projectName);
    }
}
