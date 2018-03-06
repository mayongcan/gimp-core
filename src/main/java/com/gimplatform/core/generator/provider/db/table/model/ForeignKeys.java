package com.gimplatform.core.generator.provider.db.table.model;

import com.gimplatform.core.generator.utils.ListHashtable;

public class ForeignKeys {

    protected Table parentTable; // 宿主表
    protected ListHashtable associatedTables;

    public ForeignKeys(Table aTable) {
        super();
        parentTable = aTable;
        associatedTables = new ListHashtable();
    }

    /**
     * @param tableName
     * @param columnName
     * @param seq
     */
    public void addForeignKey(String tableName, String columnName, String parentColumn, Integer seq) {
        ForeignKey tbl = null;
        if (associatedTables.containsKey(tableName)) {
            tbl = (ForeignKey) associatedTables.get(tableName);
        } else {
            tbl = new ForeignKey(parentTable, tableName);
            associatedTables.put(tableName, tbl);
        }

        tbl.addColumn(columnName, parentColumn, seq);
    }

    public ListHashtable getAssociatedTables() {
        return associatedTables;
    }

    public int getSize() {
        return getAssociatedTables().size();
    }

    public boolean getHasImportedKeyColumn(String aColumn) {
        boolean isFound = false;
        int numKeys = getAssociatedTables().size();
        for (int i = 0; i < numKeys; i++) {
            ForeignKey aKey = (ForeignKey) getAssociatedTables().getOrderedValue(i);
            if (aKey.getHasImportedKeyColumn(aColumn)) {
                isFound = true;
                break;
            }
        }
        return isFound;
    }

    public ForeignKey getAssociatedTable(String name) {
        Object fkey = getAssociatedTables().get(name);
        if (fkey != null) {
            return (ForeignKey) fkey;
        } else
            return null;
    }

    public Table getParentTable() {
        return parentTable;
    }

    public boolean getHasImportedKeyParentColumn(String aColumn) {
        boolean isFound = false;
        int numKeys = getAssociatedTables().size();
        for (int i = 0; i < numKeys; i++) {
            ForeignKey aKey = (ForeignKey) getAssociatedTables().getOrderedValue(i);
            if (aKey.getHasImportedKeyParentColumn(aColumn)) {
                isFound = true;
                break;
            }
        }
        return isFound;
    }

    public ForeignKey getImportedKeyParentColumn(String aColumn) {
        ForeignKey aKey = null;
        int numKeys = getAssociatedTables().size();
        for (int i = 0; i < numKeys; i++) {
            aKey = (ForeignKey) getAssociatedTables().getOrderedValue(i);
            if (aKey.getHasImportedKeyParentColumn(aColumn)) {
                break;
            }
        }
        return aKey;
    }
}
