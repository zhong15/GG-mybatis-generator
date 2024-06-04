    <if test="columnList == null or columnList.size() == 0">
      SELECT a.* FROM ${tableName} a
    </if>
    <if test="columnList != null and columnList.size() != 0">
      SELECT
      <foreach collection="columnList" item="column" separator=",">
        <#noparse>a.${column}</#noparse>
      </foreach>
      FROM ${tableName} a
    </if>
    INNER JOIN (SELECT ${pk.actualColumnName} FROM ${tableName}
    <include refid="sqlWhere2" />
    <if test="orderBy != null and orderBy.trim() != ''">
      <#noparse>ORDER BY ${orderBy}</#noparse>
    </if>
    <#noparse>LIMIT #{offset}, #{rowCount}</#noparse>
    ) b ON a.${pk.actualColumnName} = b.${pk.actualColumnName}
    <if test="orderBy != null and orderBy.trim() != ''">
      <#noparse>ORDER BY ${orderBy}</#noparse>
    </if>