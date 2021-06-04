package org.truenewx.tnxjee.repo.jpa.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.version.Version;

/**
 * 使用数据表进行智能判断的数据初始化器
 */
public class DatabaseTableDataSourceInitializer extends SmartDataSourceInitializer {

    private String querySql;
    private String updateSql;
    @Autowired
    private Environment environment;

    public DatabaseTableDataSourceInitializer(String tableName, String keyFiledName, String valueFieldName) {
        this.querySql = "select " + valueFieldName + " from " + tableName + " where " + keyFiledName + "=?";
        this.updateSql = "update " + tableName + " set " + valueFieldName + "=? where " + keyFiledName + "=?";
    }

    @Override
    protected boolean executable(Connection connection, String version) throws SQLException {
        String lastVersion = getLastVersion(connection);
        if (lastVersion == null) { // 找不到最近执行版本，则返回false以避免在开发人员不知情的情况下执行脚本
            return false;
        }
        return new Version(version).compareTo(new Version(lastVersion)) > 0; // 比较版本高于最近执行版本才能执行
    }

    private String getLastVersion(Connection connection) throws SQLException {
        String lastVersion = null;
        PreparedStatement statement = connection.prepareStatement(this.querySql);
        statement.setString(1, getKey());
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            lastVersion = rs.getString(1);
        }
        rs.close();
        statement.close();
        return lastVersion;
    }

    private String getKey() {
        String appName = this.environment.getProperty(AppConstants.PROPERTY_SPRING_APP_NAME);
        return appName + ".sql_last_version";
    }

    @Override
    protected void updateVersion(Connection connection, String version) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(this.updateSql);
        statement.setString(1, version);
        statement.setString(2, getKey());
        statement.executeUpdate();
    }

}
