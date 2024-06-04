    <if test="columnList == null or columnList.size() == 0">
      SELECT * FROM ${tableName}
    </if>
    <if test="columnList != null and columnList.size() != 0">
      SELECT
      <foreach collection="columnList" item="column" separator=",">
        <#noparse>${column}</#noparse>
      </foreach>
      FROM ${tableName}
    </if>
    WHERE ${pk.actualColumnName} = <#noparse>#{id}</#noparse>
<#if softDeleteColumn??>
    AND ${softDeleteColumn} = ${softDeleteFalseValue}
</#if>