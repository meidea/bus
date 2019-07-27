package org.aoju.bus.mapper.builder;

import org.aoju.bus.mapper.annotation.Version;
import org.aoju.bus.mapper.criteria.Assert;
import org.aoju.bus.mapper.entity.EntityColumn;
import org.aoju.bus.mapper.entity.EntityTableName;
import org.aoju.bus.mapper.version.VersionException;

import java.util.Set;

/**
 * 拼常用SQL的工具类
 *
 * @author aoju.org
 * @version 3.0.1
 * @group 839128
 * @since JDK 1.8
 */
public class SqlSourceBuilder {

    /**
     * 获取表名 - 支持动态表名
     *
     * @param entityClass
     * @param tableName
     * @return
     */
    public static String getDynamicTableName(Class<?> entityClass, String tableName) {
        if (EntityTableName.class.isAssignableFrom(entityClass)) {
            StringBuilder sql = new StringBuilder();
            sql.append("<choose>");
            sql.append("<when test=\"@org.aoju.bus.mapper.builder.OGNL@isDynamicParameter(_parameter) and dynamicTableName != null and dynamicTableName != ''\">");
            sql.append("${dynamicTableName}\n");
            sql.append("</when>");
            //不支持指定列的时候查询全部列
            sql.append("<otherwise>");
            sql.append(tableName);
            sql.append("</otherwise>");
            sql.append("</choose>");
            return sql.toString();
        } else {
            return tableName;
        }
    }

    /**
     * 获取表名 - 支持动态表名，该方法用于多个入参时，通过parameterName指定入参中实体类的@Param的注解值
     *
     * @param entityClass
     * @param tableName
     * @param parameterName
     * @return
     */
    public static String getDynamicTableName(Class<?> entityClass, String tableName, String parameterName) {
        if (EntityTableName.class.isAssignableFrom(entityClass)) {
            if (Assert.isNotEmpty(parameterName)) {
                StringBuilder sql = new StringBuilder();
                sql.append("<choose>");
                sql.append("<when test=\"@org.aoju.bus.mapper.builder.OGNL@isDynamicParameter(" + parameterName + ") and " + parameterName + ".dynamicTableName != null and " + parameterName + ".dynamicTableName != ''\">");
                sql.append("${" + parameterName + ".dynamicTableName}");
                sql.append("</when>");
                //不支持指定列的时候查询全部列
                sql.append("<otherwise>");
                sql.append(tableName);
                sql.append("</otherwise>");
                sql.append("</choose>");
                return sql.toString();
            } else {
                return getDynamicTableName(entityClass, tableName);
            }

        } else {
            return tableName;
        }
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column
     * @return
     */
    public static String getBindCache(EntityColumn column) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_cache\" ");
        sql.append("value=\"").append(column.getProperty()).append("\"/>");
        return sql.toString();
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column
     * @return
     */
    public static String getBindValue(EntityColumn column, String value) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_bind\" ");
        sql.append("value='").append(value).append("'/>");
        return sql.toString();
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column
     * @return
     */
    public static String getIfCacheNotNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(column.getProperty()).append("_cache != null\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 如果_cache == null
     *
     * @param column
     * @return
     */
    public static String getIfCacheIsNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(column.getProperty()).append("_cache == null\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfNotNull(EntityColumn column, String contents, boolean empty) {
        return getIfNotNull(null, column, contents, empty);
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfIsNull(EntityColumn column, String contents, boolean empty) {
        return getIfIsNull(null, column, contents, empty);
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param entityName
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfNotNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (Assert.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            if (Assert.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 判断自动==null的条件结构
     *
     * @param entityName
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfIsNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (Assert.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" == null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" or ");
            if (Assert.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" == '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,criteria...
     *
     * @param entityClass
     * @return
     */
    public static String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityBuilder.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * select xxx,xxx...
     *
     * @param entityClass
     * @return
     */
    public static String selectAllColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(getAllColumns(entityClass));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * select count(x)
     *
     * @param entityClass
     * @return
     */
    public static String selectCount(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        Set<EntityColumn> pkColumns = EntityBuilder.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }
        return sql.toString();
    }

    /**
     * select case when count(x) > 0 then 1 else 0 end
     *
     * @param entityClass
     * @return
     */
    public static String selectCountExists(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CASE WHEN ");
        Set<EntityColumn> pkColumns = EntityBuilder.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }
        sql.append(" > 0 THEN 1 ELSE 0 END AS result ");
        return sql.toString();
    }

    /**
     * from tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String fromTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM ");
        sql.append(getDynamicTableName(entityClass, defaultTableName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * update tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName) {
        return updateTable(entityClass, defaultTableName, null);
    }

    /**
     * update tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName 默认表名
     * @param entityName       别名
     * @return
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName, String entityName) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(getDynamicTableName(entityClass, defaultTableName, entityName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * delete tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String deleteFromTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(getDynamicTableName(entityClass, defaultTableName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String insertIntoTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(getDynamicTableName(entityClass, defaultTableName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert table()列
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    public static String insertColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityBuilder.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlSourceBuilder.getIfNotNull(column, column.getColumn() + ",", notEmpty));
            } else {
                sql.append(column.getColumn() + ",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * insert-values()列
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    public static String insertValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityBuilder.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlSourceBuilder.getIfNotNull(column, column.getColumnHolder() + ",", notEmpty));
            } else {
                sql.append(column.getColumnHolder() + ",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * update set列
     *
     * @param entityClass
     * @param entityName  实体映射名
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    public static String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnList = EntityBuilder.getColumns(entityClass);
        //对乐观锁的支持
        EntityColumn versionColumn = null;
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (versionColumn != null) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                versionColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (column == versionColumn) {
                    Version version = versionColumn.getEntityField().getAnnotation(Version.class);
                    String versionClass = version.nextVersion().getCanonicalName();
                    sql.append(column.getColumn())
                            .append(" = ${@VersionUtil@nextVersion(\"")
                            .append(versionClass).append("\", ")
                            .append(column.getProperty()).append(")},");
                } else if (notNull) {
                    sql.append(SqlSourceBuilder.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * where主键条件
     *
     * @param entityClass
     * @return
     */
    public static String wherePKColumns(Class<?> entityClass) {
        return wherePKColumns(entityClass, false);
    }

    /**
     * where主键条件
     *
     * @param entityClass
     * @return
     */
    public static String wherePKColumns(Class<?> entityClass, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnList = EntityBuilder.getPKColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            sql.append(" AND " + column.getColumnEqualsHolder());
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass));
        }
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass
     * @param empty
     * @return
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty) {
        return whereAllIfColumns(entityClass, empty, false);
    }

    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass
     * @param empty
     * @param useVersion
     * @return
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> columnList = EntityBuilder.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!useVersion || !column.getEntityField().isAnnotationPresent(Version.class)) {
                sql.append(getIfNotNull(column, " AND " + column.getColumnEqualsHolder(), empty));
            }
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass));
        }
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 乐观锁字段条件
     *
     * @param entityClass
     * @return
     */
    public static String whereVersion(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityBuilder.getColumns(entityClass);
        boolean hasVersion = false;
        String result = "";
        for (EntityColumn column : columnList) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (hasVersion) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                hasVersion = true;
                result = " AND " + column.getColumnEqualsHolder();
            }
        }
        return result;
    }

    /**
     * 获取默认的orderBy，通过注解设置的
     *
     * @param entityClass
     * @return
     */
    public static String orderByDefault(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        String orderByClause = EntityBuilder.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append(" ORDER BY ");
            sql.append(orderByClause);
        }
        return sql.toString();
    }

    /**
     * 支持查询指定列时
     *
     * @return
     */
    public static String selectColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@org.aoju.bus.mapper.builder.OGNL@hasSelectColumns(_parameter)\">");
        sql.append("<foreach collection=\"_parameter.selectColumns\" item=\"selectColumn\" separator=\",\">");
        sql.append("${selectColumn}");
        sql.append("</foreach>");
        sql.append("</when>");
        //不支持指定列的时候查询全部列
        sql.append("<otherwise>");
        sql.append(getAllColumns(entityClass));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * 支持查询指定列时
     *
     * @return
     */
    public static String countColumn(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@org.aoju.bus.mapper.builder.OGNL@hasCountColumn(_parameter)\">");
        sql.append("COUNT(${countColumn})");
        sql.append("</when>");
        sql.append("<otherwise>");
        sql.append("COUNT(0)");
        sql.append("</otherwise>");
        sql.append("</choose>");
        //不支持指定列的时候查询全部列
        sql.append("<if test=\"@org.aoju.bus.mapper.builder.OGNL@hasNoSelectColumns(_parameter)\">");
        sql.append(getAllColumns(entityClass));
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 查询中的orderBy条件，会判断默认orderBy
     *
     * @return
     */
    public static String orderBy(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"orderByClause != null\">");
        sql.append("order by ${orderByClause}");
        sql.append("</if>");
        String orderByClause = EntityBuilder.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append("<if test=\"orderByClause == null\">");
            sql.append("ORDER BY " + orderByClause);
            sql.append("</if>");
        }
        return sql.toString();
    }

    /**
     * 支持 for update
     *
     * @return
     */
    public static String forUpdate() {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"@org.aoju.bus.mapper.builder.OGNL@hasForUpdate(_parameter)\">");
        sql.append("FOR UPDATE");
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 支持 for update
     *
     * @return
     */
    public static String check(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"checkEntityClass\" value=\"@org.aoju.bus.mapper.builder.OGNL@checkEntityClass(_parameter, '");
        sql.append(entityClass.getCanonicalName());
        sql.append("')\"/>");
        return sql.toString();
    }

    /**
     * 查询中的where结构，用于只有一个参数时
     *
     * @return
     */
    public static String whereClause() {
        return "<if test=\"_parameter != null\">" +
                "<where>\n" +
                "  <foreach collection=\"oredCriteria\" item=\"criteria\">\n" +
                "    <if test=\"criteria.valid\">\n" +
                "      ${@org.aoju.bus.mapper.builder.OGNL@andOr(criteria)}" +
                "      <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
                "          <choose>\n" +
                "            <when test=\"criterion.noValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.singleValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.betweenValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.listValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "              <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                #{listItem}\n" +
                "              </foreach>\n" +
                "            </when>\n" +
                "          </choose>\n" +
                "        </foreach>\n" +
                "      </trim>\n" +
                "    </if>\n" +
                "  </foreach>\n" +
                "</where>" +
                "</if>";
    }

    /**
     * Update中的where结构，用于多个参数时，带@Param("condition")注解时
     *
     * @return
     */
    public static String updateByWhereClause() {
        return "<where>\n" +
                "  <foreach collection=\"condition.oredCriteria\" item=\"criteria\">\n" +
                "    <if test=\"criteria.valid\">\n" +
                "      ${@org.aoju.bus.mapper.builder.OGNL@andOr(criteria)}" +
                "      <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
                "          <choose>\n" +
                "            <when test=\"criterion.noValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.singleValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.betweenValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.listValue\">\n" +
                "              ${@org.aoju.bus.mapper.builder.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "              <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                #{listItem}\n" +
                "              </foreach>\n" +
                "            </when>\n" +
                "          </choose>\n" +
                "        </foreach>\n" +
                "      </trim>\n" +
                "    </if>\n" +
                "  </foreach>\n" +
                "</where>";
    }

}
