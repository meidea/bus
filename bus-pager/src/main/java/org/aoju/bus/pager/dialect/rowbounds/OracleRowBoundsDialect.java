package org.aoju.bus.pager.dialect.rowbounds;

import org.aoju.bus.pager.dialect.AbstractRowBoundsDialect;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.session.RowBounds;

/**
 * oracle 基于 RowBounds 的分页
 *
 * @author aoju.org
 * @version 3.0.1
 * @group 839128
 * @since JDK 1.8
 */
public class OracleRowBoundsDialect extends AbstractRowBoundsDialect {

    @Override
    public String getPageSql(String sql, RowBounds rowBounds, CacheKey pageKey) {
        int startRow = rowBounds.getOffset();
        int endRow = rowBounds.getOffset() + rowBounds.getLimit();
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        if (startRow > 0) {
            sqlBuilder.append("SELECT * FROM ( ");
        }
        if (endRow > 0) {
            sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM ( ");
        }
        sqlBuilder.append(sql);
        if (endRow > 0) {
            sqlBuilder.append(" ) TMP_PAGE WHERE ROWNUM <= ");
            sqlBuilder.append(endRow);
            pageKey.update(endRow);
        }
        if (startRow > 0) {
            sqlBuilder.append(" ) WHERE ROW_ID > ");
            sqlBuilder.append(startRow);
            pageKey.update(startRow);
        }
        return sqlBuilder.toString();
    }

}
