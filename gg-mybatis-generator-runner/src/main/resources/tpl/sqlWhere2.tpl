    WHERE is_deleted = 0
      <if test="where != null and where.whereSql != null and where.whereSql.length() != 0">
<#noparse>
        AND ${where.whereSql.toString()}
</#noparse>
      </if>