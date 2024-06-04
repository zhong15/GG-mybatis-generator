    <if test="columnList == null or columnList.size() == 0">
      SELECT * FROM ${tableName}
    </if>
    <if test="columnList != null and columnList.size() != 0">
      SELECT
      <if test="distinct != null and distinct">
        DISTINCT
      </if>
      <foreach collection="columnList" item="column" separator=",">
        <#noparse>${column}</#noparse>
      </foreach>
      FROM ${tableName}
    </if>
    <include refid="sqlWhere2" />
    <if test="orderBy != null and orderBy.trim() != ''">
      <#noparse>ORDER BY ${orderBy}</#noparse>
    </if>
    <if test="rowCount != null">
      LIMIT
      <if test="offset != null">
        <#noparse>#{offset},</#noparse>
      </if>
      <#noparse>#{rowCount}</#noparse>
    </if>