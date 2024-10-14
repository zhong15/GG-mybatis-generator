    <where>
      <if test="where != null and where.whereSql != null and where.whereSql.length() != 0">
<#noparse>
        ${where.whereSql.toString()}
</#noparse>
      </if>
      AND is_deleted = 0
    </where>