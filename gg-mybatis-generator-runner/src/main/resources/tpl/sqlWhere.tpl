    <where>
      <if test="whereSql != null and whereSql.length() != 0">
<#if softDeleteColumn??>
        <if test="addBrackets != null and addBrackets">
          (
        </if>
</#if>
<#noparse>
        ${whereSql.toString()}
</#noparse>
<#if softDeleteColumn??>
        <if test="addBrackets != null and addBrackets">
          )
        </if>
</#if>
      </if>
<#if softDeleteColumn??>
      AND ${softDeleteColumn} = ${softDeleteFalseValue}
</#if>
    </where>