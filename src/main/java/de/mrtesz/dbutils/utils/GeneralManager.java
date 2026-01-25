package de.mrtesz.dbutils.utils;

import de.mrtesz.dbutils.utils.data.ProjectData;
import de.mrtesz.dbutils.utils.exceptions.DatabaseException;
import de.mrtesz.dbutils.utils.logger.DebugLevel;
import de.mrtesz.dbutils.utils.mariadb.MariaDBManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeneralManager {

    private final Init init;
    @Getter
    private final HashMap<String, ProjectData> projects;

    public GeneralManager(Init init) {
        this.init = init;

        this.projects = new HashMap<>();
    }

    public @Nullable MariaDBManager getMariaDBManager(String projectName, String databaseName) {
        ProjectData data = getProject(projectName);

        if (data == null)
            return null;

        for (MariaDBManager mariaDBManager : data.getMariaDBManagers()) {
            if (mariaDBManager.getName().equals(databaseName))
                return mariaDBManager;
        }

        return null;
    }

    public void initializeProject(@NotNull ProjectData projectData) {
        projects.put(projectData.getProjectName(), projectData);

        init.getLogger(DebugLevel.LEVEL5, "DBUtils").info("Initialized Project " + projectData.getProjectName());
    }

    public MariaDBManager addManager(String projectName, MariaDBManager mariaDBManager) throws DatabaseException {
        ProjectData project = getProject(projectName);

        if(project == null)
            throw new DatabaseException(projectName + " wanted to initialize a MariaDBManager but had no saved ProjectData. " +
                    "Please initialize your Project first with DBUtilsApi.getInstance().initializeProject");

        List<MariaDBManager> openedManagerList = new ArrayList<>(project.getMariaDBManagers());
        openedManagerList.add(mariaDBManager);

        project.setMariaDBManagers(openedManagerList);

        init.getLogger(DebugLevel.LEVEL5, "DBUtils").info(projectName + " added the MariaDBManager " + mariaDBManager.getName());

        return mariaDBManager;
    }

    public @Nullable ProjectData getProject(String projectName) {
        return projects.get(projectName);
    }
}
