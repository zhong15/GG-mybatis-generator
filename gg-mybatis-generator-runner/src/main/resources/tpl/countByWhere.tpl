    <if test="columnList == null or columnList.size() == 0">
      SELECT COUNT(*) FROM ${tableName}
    </if>
    <if test="columnList != null and columnList.size() != 0">
      SELECT COUNT(
      <if test="distinct != null and distinct">
        DISTINCT
      </if>
      <foreach collection="columnList" item="column" separator=",">
        <#noparse>${column}</#noparse>
      </foreach>
      ) FROM ${tableName}
    </if>
    <include refid="sqlWhere2" />