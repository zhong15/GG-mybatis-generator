    <where>
      <if test="where != null and where.whereSql != null and where.whereSql.length() != 0">
        <if test="where.addBrackets != null and where.addBrackets">
          (
        </if>
<#noparse>
        ${where.whereSql.toString()}
</#noparse>
        <if test="where.addBrackets != null and where.addBrackets">
          )
        </if>
      </if>
      AND is_deleted = 0
    </where>