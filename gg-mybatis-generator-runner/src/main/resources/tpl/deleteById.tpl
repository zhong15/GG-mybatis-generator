<#if softDeleteColumn??>
    UPDATE ${tableName} SET ${softDeleteColumn} = ${softDeleteTrueValue} WHERE ${pk.actualColumnName} = <#noparse>#{id}</#noparse> AND ${softDeleteColumn} = ${softDeleteFalseValue}
<#else>
    DELETE FROM ${tableName} WHERE ${pk.actualColumnName} = <#noparse>#{id}</#noparse>
</#if>