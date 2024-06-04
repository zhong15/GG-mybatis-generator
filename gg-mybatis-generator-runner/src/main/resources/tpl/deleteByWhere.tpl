<#if softDeleteColumn??>
    UPDATE ${tableName} SET ${softDeleteColumn} = ${softDeleteTrueValue}
<#else>
    DELETE FROM ${tableName}
</#if>
    <include refid="sqlWhere" />