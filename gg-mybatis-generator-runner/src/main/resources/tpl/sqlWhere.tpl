    <where>
      <if test="whereSql != null and whereSql.length() != 0">
<#noparse>
        ${whereSql.toString()}
</#noparse>
      </if>
      AND is_deleted = 0
    </where>