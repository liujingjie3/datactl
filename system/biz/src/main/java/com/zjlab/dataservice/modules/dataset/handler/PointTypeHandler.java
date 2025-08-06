package com.zjlab.dataservice.modules.dataset.handler;

import org.apache.ibatis.type.MappedTypes;
import org.postgis.Point;

/**
 * 描述：
 *
 * @author
 * @date 2022-04-03
 */
@MappedTypes(Point.class)
public class PointTypeHandler extends AbstractGeometryTypeHandler<Point> {
}