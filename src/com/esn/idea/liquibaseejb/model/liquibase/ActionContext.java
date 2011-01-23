package com.esn.idea.liquibaseejb.model.liquibase;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;

import java.util.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-24
 * Time: 16:00:24
 */
public class ActionContext
{
    private List<String> tables = new ArrayList<String>();
    private Map<String, List<String>> tableToColumns = new HashMap<String, List<String>>();

    public ActionContext(DatabaseModel databaseModel)
    {
        tables = databaseModel.getTableNames();

        for (String table : tables)
        {
            tableToColumns.put(table,  databaseModel.getColumnNames(table));
        }
    }

    public List<String> getTableNames()
    {
        return tables;
    }

    public List<String> getColumnNames(String tableName)
    {
        List<String> res = tableToColumns.get(tableName);

        if (res == null) return Collections.emptyList();
        
        return res;
    }
}
