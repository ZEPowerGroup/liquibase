package liquibase.sqlgenerator.core;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import liquibase.database.Database;
import liquibase.database.core.OracleDatabase;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGenerator;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.CreateViewStatement;

public class CreateViewGeneratorTest {
  @Test
  public void noForceCreateView() {
    final String definition = "SELECT * FROM SOME_TABLE";
    final CreateViewStatement statement = new CreateViewStatement("VIEW_SCHEMA", "VIEW_NAME", definition, false, false);
    final CreateViewGenerator generator = new CreateViewGenerator();
    final SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>();
    generators.add(generator);
    final Database database = new OracleDatabase();
    final SqlGeneratorChain chain = new SqlGeneratorChain(generators);
    final Sql[] sqls = generator.generateSql(statement, database, chain);
    Assert.assertEquals(1, sqls.length);
    Assert.assertEquals("CREATE VIEW VIEW_SCHEMA.VIEW_NAME AS SELECT * FROM SOME_TABLE",
        sqls[0].toSql());
  }

  @Test
  public void noForceRereateView() {
    final String definition = "SELECT * FROM SOME_TABLE";
    final CreateViewStatement statement = new CreateViewStatement("VIEW_SCHEMA", "VIEW_NAME", definition, true, false);
    final CreateViewGenerator generator = new CreateViewGenerator();
    final SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>();
    generators.add(generator);
    final Database database = new OracleDatabase();
    final SqlGeneratorChain chain = new SqlGeneratorChain(generators);
    final Sql[] sqls = generator.generateSql(statement, database, chain);
    Assert.assertEquals(1, sqls.length);
    Assert.assertEquals("CREATE OR REPLACE VIEW VIEW_SCHEMA.VIEW_NAME AS SELECT * FROM SOME_TABLE",
        sqls[0].toSql());
  }

  @Test
  public void forceCreateView() {
    final String definition = "SELECT * FROM SOME_TABLE";
    final CreateViewStatement statement = new CreateViewStatement("VIEW_SCHEMA", "VIEW_NAME", definition, false, true);
    final CreateViewGenerator generator = new CreateViewGenerator();
    final SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>();
    generators.add(generator);
    final Database database = new OracleDatabase();
    final SqlGeneratorChain chain = new SqlGeneratorChain(generators);
    final Sql[] sqls = generator.generateSql(statement, database, chain);
    Assert.assertEquals(1, sqls.length);
    Assert.assertEquals("CREATE VIEW FORCE VIEW_SCHEMA.VIEW_NAME AS SELECT * FROM SOME_TABLE",
        sqls[0].toSql());
  }

  @Test
  public void forceRereateView() {
    final String definition = "SELECT * FROM SOME_TABLE";
    final CreateViewStatement statement = new CreateViewStatement("VIEW_SCHEMA", "VIEW_NAME", definition, true, true);
    final CreateViewGenerator generator = new CreateViewGenerator();
    final SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>();
    generators.add(generator);
    final Database database = new OracleDatabase();
    final SqlGeneratorChain chain = new SqlGeneratorChain(generators);
    final Sql[] sqls = generator.generateSql(statement, database, chain);
    Assert.assertEquals(1, sqls.length);
    Assert.assertEquals("CREATE OR REPLACE VIEW FORCE VIEW_SCHEMA.VIEW_NAME AS SELECT * FROM SOME_TABLE",
        sqls[0].toSql());
  }
}
