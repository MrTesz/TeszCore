module dbutils {
    requires java.base;
    requires static lombok;
    requires org.jetbrains.annotations;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.apache.logging.log4j.core;
    requires org.yaml.snakeyaml;
    requires ansi_impl;

    exports de.mrtesz.dbutils.api;
    exports de.mrtesz.dbutils.api.db.table;
    exports de.mrtesz.dbutils.api.db.manager;
    exports de.mrtesz.dbutils.utils.config;
    exports de.mrtesz.dbutils.utils.selection;
    exports de.mrtesz.dbutils.utils.logger.api;
    exports de.mrtesz.dbutils.utils.exceptions;
}