package com.pps.core.plug.filterplug;

import org.apache.ibatis.mapping.BoundSql;

public interface SqlResove {

    String filterSql(Condition condition, BoundSql boundSql, Object parameter);
}
