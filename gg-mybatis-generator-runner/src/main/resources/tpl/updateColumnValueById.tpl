    UPDATE ${tableName}
    <#noparse>SET ${column} = #{value}</#noparse>
    WHERE ${pk.actualColumnName} = <#noparse>#{id}</#noparse>
    AND is_deleted = 0