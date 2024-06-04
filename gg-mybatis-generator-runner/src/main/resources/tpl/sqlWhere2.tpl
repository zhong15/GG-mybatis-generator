    <where>
      <if test="where != null and where.whereSql != null and where.whereSql.length() != 0">
<#if softDeleteColumn??>
        <if test="where.addBrackets != null and where.addBrackets">
          (
        </if>
</#if>
<#noparse>
        ${where.whereSql.toString()}
</#noparse>
<#if softDeleteColumn??>
        <if test="where.addBrackets != null and where.addBrackets">
          )
        </if>
</#if>
      </if>
<#if softDeleteColumn??>
      AND ${softDeleteColumn} = ${softDeleteFalseValue}
</#if>
    </where>