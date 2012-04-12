package liquibase.executor;

import java.util.List;
import java.util.Map;

import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.CallableSqlStatement;
import liquibase.statement.SqlStatement;

@SuppressWarnings("rawtypes")
public class ComboExecutor implements Executor {

    private final Executor mainExecutor;

    private final LoggingExecutor loggingExecutor;

    public ComboExecutor(final Executor mainExecutor, final LoggingExecutor loggingExecutor) {
        this.mainExecutor = mainExecutor;
        this.loggingExecutor = loggingExecutor;
    }

    public void setDatabase(final Database database) {
        mainExecutor.setDatabase(database);
        loggingExecutor.setDatabase(database);
    }

    public Object queryForObject(final SqlStatement sql, final Class requiredType) throws DatabaseException {
        return mainExecutor.queryForObject(sql, requiredType);
    }

    public Object queryForObject(final SqlStatement sql, final Class requiredType, final List<SqlVisitor> sqlVisitors)
            throws DatabaseException {
        return mainExecutor.queryForObject(sql, requiredType, sqlVisitors);
    }

    public long queryForLong(final SqlStatement sql) throws DatabaseException {
        return mainExecutor.queryForLong(sql);
    }

    public long queryForLong(final SqlStatement sql, final List<SqlVisitor> sqlVisitors) throws DatabaseException {
        return mainExecutor.queryForLong(sql, sqlVisitors);
    }

    public int queryForInt(final SqlStatement sql) throws DatabaseException {
        return mainExecutor.queryForInt(sql);
    }

    public int queryForInt(final SqlStatement sql, final List<SqlVisitor> sqlVisitors) throws DatabaseException {
        return mainExecutor.queryForInt(sql, sqlVisitors);
    }

    public List queryForList(final SqlStatement sql, final Class elementType) throws DatabaseException {
        return mainExecutor.queryForList(sql, elementType);
    }

    public List queryForList(final SqlStatement sql, final Class elementType, final List<SqlVisitor> sqlVisitors)
            throws DatabaseException {
        return mainExecutor.queryForList(sql, elementType, sqlVisitors);
    }

    public List<Map> queryForList(final SqlStatement sql) throws DatabaseException {
        return mainExecutor.queryForList(sql);
    }

    public List<Map> queryForList(final SqlStatement sql, final List<SqlVisitor> sqlVisitors) throws DatabaseException {
        return mainExecutor.queryForList(sql, sqlVisitors);
    }

    public void execute(final SqlStatement sql) throws DatabaseException {
        loggingExecutor.execute(sql);
        mainExecutor.execute(sql);
    }

    public void execute(final SqlStatement sql, final List<SqlVisitor> sqlVisitors) throws DatabaseException {
        loggingExecutor.execute(sql, sqlVisitors);
        mainExecutor.execute(sql, sqlVisitors);
    }

    public int update(final SqlStatement sql) throws DatabaseException {
        loggingExecutor.update(sql);
        return mainExecutor.update(sql);
    }

    public int update(final SqlStatement sql, final List<SqlVisitor> sqlVisitors) throws DatabaseException {
        loggingExecutor.update(sql, sqlVisitors);
        return mainExecutor.update(sql, sqlVisitors);
    }

    public Map call(final CallableSqlStatement csc, final List declaredParameters, final List<SqlVisitor> sqlVisitors)
            throws DatabaseException {
        throw new DatabaseException("Do not know how to output callable statement");
    }

    public void comment(final String message) throws DatabaseException {
        loggingExecutor.comment(message);
        mainExecutor.comment(message);
    }

    public boolean updatesDatabase() {
        return true;
    }

}
