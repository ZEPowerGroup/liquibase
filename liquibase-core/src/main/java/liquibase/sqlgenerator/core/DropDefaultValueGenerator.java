package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.core.*;
import liquibase.exception.DatabaseException;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.DropDefaultValueStatement;

public class DropDefaultValueGenerator extends AbstractSqlGenerator<DropDefaultValueStatement> {

    @Override
    public boolean supports(DropDefaultValueStatement statement, Database database) {
        return !(database instanceof SQLiteDatabase);
    }

    public ValidationErrors validate(DropDefaultValueStatement dropDefaultValueStatement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();
        validationErrors.checkRequiredField("tableName", dropDefaultValueStatement.getTableName());
        validationErrors.checkRequiredField("columnName", dropDefaultValueStatement.getColumnName());

        if (database instanceof InformixDatabase) {
            validationErrors.checkRequiredField("columnDataType", dropDefaultValueStatement.getColumnDataType());
        }


        return validationErrors;
    }

    public Sql[] generateSql(DropDefaultValueStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        String sql;
        String escapedTableName = database.escapeTableName(statement.getSchemaName(), statement.getTableName());
         if (database instanceof MSSQLDatabase) {
           boolean sql2005OrLater = true;
           try {
               sql2005OrLater = database.getDatabaseMajorVersion() >= 9;
           } catch (DatabaseException e) {
               // Assume SQL Server 2005 or later
           }
           if (sql2005OrLater) {
               // SQL Server 2005 or later
               sql =
                       "DECLARE @sql [nvarchar](MAX)\r\n" +
                       "SELECT @sql = N'ALTER TABLE " + database.escapeStringForDatabase(escapedTableName) + " DROP CONSTRAINT ' + QUOTENAME([df].[name]) " +
                       "FROM [sys].[columns] AS [c] " +
                       "INNER JOIN [sys].[default_constraints] AS [df] " +
                       "ON [df].[object_id] = [c].[default_object_id] " +
                       "WHERE [c].[object_id] = OBJECT_ID(N'" + database.escapeStringForDatabase(escapedTableName) +  "') " +
                       "AND [c].[name] = N'" + database.escapeStringForDatabase(statement.getColumnName()) +  "'\r\n" +
                       "EXEC sp_executesql @sql";
           } else {
               // SQL Server 2000
               sql =
                       "DECLARE @sql [nvarchar](4000)\r\n" +
                       "SELECT @sql = N'ALTER TABLE " + database.escapeStringForDatabase(escapedTableName) + " DROP CONSTRAINT ' + QUOTENAME([df].[name]) " +
                       "FROM [dbo].[syscolumns] AS [c] " +
                       "INNER JOIN [dbo].[sysobjects] AS [df] " +
                       "ON [df].[id] = [c].[cdefault] " +
                       "WHERE [c].[id] = OBJECT_ID(N'" + database.escapeStringForDatabase(escapedTableName) +  "') " +
                       "AND [c].[name] = N'" + database.escapeStringForDatabase(statement.getColumnName()) +  "'\r\n" +
                       "EXEC sp_executesql @sql";
           }
        } else if (database instanceof MySQLDatabase) {
            sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " ALTER " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " DROP DEFAULT";
        } else if (database instanceof OracleDatabase || database instanceof SybaseASADatabase) {
            sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " MODIFY " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " DEFAULT NULL";
        } else if (database instanceof DerbyDatabase) {
            sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " ALTER COLUMN  " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " WITH DEFAULT NULL";
        } else if (database instanceof MaxDBDatabase) {
          	sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " COLUMN  " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " DROP DEFAULT";
        } else if (database instanceof InformixDatabase) {
        	/*
        	 * TODO If dropped from a not null column the not null constraint will be dropped, too.
        	 * If the column is "NOT NULL" it has to be added behind the datatype.
        	 */
        	sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " MODIFY (" + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " " + statement.getColumnDataType() + ")";
        } else {
            sql = "ALTER TABLE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()) + " ALTER COLUMN  " + database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " SET DEFAULT NULL";
         }

        return new Sql[] {
                new UnparsedSql(sql)
        };
    }
}
