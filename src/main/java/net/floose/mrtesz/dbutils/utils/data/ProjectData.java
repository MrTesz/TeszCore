package net.floose.mrtesz.dbutils.utils.data;

import lombok.Getter;
import lombok.Setter;
import net.floose.mrtesz.dbutils.utils.mariadb.MariaDBManager;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectData {

    private final String projectName;
    private List<MariaDBManager> mariaDBManagers;

    public ProjectData(String projectName, List<MariaDBManager> mariaDBManagers) {
        this.projectName = projectName;
        this.mariaDBManagers = new ArrayList<>(mariaDBManagers);
    }
}
