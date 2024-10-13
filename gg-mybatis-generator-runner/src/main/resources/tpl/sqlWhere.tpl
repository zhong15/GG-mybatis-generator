    <where>
      <if test="whereSql != null and whereSql.length() != 0">
        <if test="addBrackets != null and addBrackets">
          (
        </if>
<#noparse>
        ${whereSql.toString()}
</#noparse>
        <if test="addBrackets != null and addBrackets">
          )
        </if>
      </if>
      AND is_deleted = 0
    </where>