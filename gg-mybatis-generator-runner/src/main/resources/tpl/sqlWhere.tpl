    WHERE is_deleted = 0
      <if test="whereSql != null and whereSql.length() != 0">
<#noparse>
        AND ${whereSql.toString()}
</#noparse>
      </if>