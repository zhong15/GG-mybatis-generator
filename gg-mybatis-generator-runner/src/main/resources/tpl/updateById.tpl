    UPDATE ${tableName}
    <set>
<#list allColumns as c>
      <if test="row.${c.javaProperty} != null">
        ${c.actualColumnName} = <#noparse>#{</#noparse>row.${c.javaProperty},jdbcType=${c.jdbcTypeName}},
      </if>
</#list>
      <if test="setNullColumnList != null and setNullColumnList.size() != 0">
        <foreach collection="setNullColumnList" item="column" separator=",">
          <#noparse>${column} = NULL</#noparse>
        </foreach>
      </if>
    </set>
    WHERE is_deleted = 0 AND ${pk.actualColumnName} = <#noparse>#{row.</#noparse>${pk.javaProperty},jdbcType=${pk.jdbcTypeName}}