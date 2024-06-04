    UPDATE ${tableName}
    <#noparse>SET ${column} = #{value}</#noparse>
    WHERE ${pk.actualColumnName} = <#noparse>#{id}</#noparse>
<#if softDeleteColumn??>
    AND ${softDeleteColumn} = ${softDeleteFalseValue}
</#if>