package liquibase.changelog.visitor;

import liquibase.change.ColumnConfig;
import liquibase.change.core.CreateTableChange;
import liquibase.change.core.CreateViewChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.SetupException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ValidatingVisitorTest {

    private ChangeSet changeSet1;
    private ChangeSet changeSet2;

    private ChangeSet changeSet1a;

    @Before
    public void setup() {
        changeSet1 = new ChangeSet("1", "testAuthor", false, false, "path/changelog", null, null);
        changeSet2 = new ChangeSet("2", "testAuthor", false, false, "path/changelog", null, null);
        changeSet1a = new ChangeSet("1", "testAuthor", false, false, "path/changelog", null, null);
        CreateViewChange change = new CreateViewChange();
        change.setSchemaName("testSchema");
        change.setViewName("testView");
        change.setSelectQuery("select 1 from dual");
        changeSet1a.addChange(change);
    }


    @Test
    public void visit_successful() {
        CreateTableChange change1 = new CreateTableChange();
        change1.setTableName("table1");
        ColumnConfig column1 = new ColumnConfig();
        change1.addColumn(column1);
        column1.setName("col1");
        column1.setType("int");

        CreateTableChange change2 = new CreateTableChange();
        change2.setTableName("table2");
        ColumnConfig column2 = new ColumnConfig();
        change2.addColumn(column2);
        column2.setName("col2");
        column2.setType("int");

        changeSet1.addChange(change1);
        changeSet2.addChange(change2);

        ValidatingVisitor handler = new ValidatingVisitor(new ArrayList<RanChangeSet>());
        handler.visit(changeSet1, new DatabaseChangeLog(), null);
        handler.visit(changeSet2, new DatabaseChangeLog(), null);

        assertTrue(handler.validationPassed());

    }

    @Test
    public void visit_setupException() {
        changeSet1.addChange(new CreateTableChange() {
            @Override
            public void init() throws SetupException {
                throw new SetupException("Test message");
            }
        });

        ValidatingVisitor handler = new ValidatingVisitor(new ArrayList<RanChangeSet>());
        handler.visit(changeSet1, new DatabaseChangeLog(), null);

        assertEquals(1, handler.getSetupExceptions().size());
        assertEquals("Test message", handler.getSetupExceptions().get(0).getMessage());

        assertFalse(handler.validationPassed());
    }

    @Test
    public void visit_duplicateIdentical() {

        ValidatingVisitor handler = new ValidatingVisitor(new ArrayList<RanChangeSet>());
        handler.visit(changeSet1, new DatabaseChangeLog(), null);
        handler.visit(changeSet1, new DatabaseChangeLog(), null);

        assertEquals(0, handler.getDuplicateChangeSets().size());

        assertTrue(handler.validationPassed());
    }

    @Test
    public void visit_duplicateDifferent() {
        ValidatingVisitor handler = new ValidatingVisitor(new ArrayList<RanChangeSet>());
        handler.visit(changeSet1, new DatabaseChangeLog(), null);
        handler.visit(changeSet1a, new DatabaseChangeLog(), null);

        assertEquals(1, handler.getDuplicateChangeSets().size());

        assertFalse(handler.validationPassed());
    }
}
