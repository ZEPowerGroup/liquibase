package liquibase.changelog;

import java.util.List;

import liquibase.database.Database;

public interface ChangeLogParameterGenerator {

    Object generateValue(String key, Database currentDatabase, List<String> currentContexts);

}
