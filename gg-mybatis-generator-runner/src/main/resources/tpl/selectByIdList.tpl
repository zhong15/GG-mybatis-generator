    <if test="columnList == null or columnList.size() == 0">
      SELECT * FROM ${tableName}
    </if>
    <if test="columnList != null and columnList.size() != 0">
      SELECT
      <foreach collection="columnList" item="column" separator=",">
        <#noparse>${column}</#noparse>
      </foreach>
      FROM ${tableName}
    </if>
    WHERE is_deleted = 0 AND ${pk.actualColumnName} IN
      <foreach collection="idList" item="id" open="(" close=")" separator=",">
        <#noparse>#{id}</#noparse>
      </foreach>