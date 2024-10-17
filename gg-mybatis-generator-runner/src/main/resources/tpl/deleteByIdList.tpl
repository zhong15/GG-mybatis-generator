UPDATE ${tableName} SET is_deleted = 1 WHERE is_deleted = 0 AND ${pk.actualColumnName} IN
<foreach collection="arg0" item="id" open="(" close=")" separator=",">
  <#noparse>#{id}</#noparse>
</foreach>