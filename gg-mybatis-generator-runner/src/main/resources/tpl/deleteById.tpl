UPDATE ${tableName} SET is_deleted = 1 WHERE ${pk.actualColumnName} = <#noparse>#{id}</#noparse> AND is_deleted = 0