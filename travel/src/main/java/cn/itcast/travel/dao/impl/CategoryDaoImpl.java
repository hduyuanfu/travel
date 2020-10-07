package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.util.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    @Override
    public List<Category> findAll() {
        String sql = "select * from tab_category";
        // 查询时，需要把数据库表中每一行数据都转换为一个java对象
        // BeanPropertyRowMapper意思是：数据库中的属性/字段，每一行映射成一个Bean(也就是java标准类)
        return template.query(sql, new BeanPropertyRowMapper<Category>(Category.class));
    }
}
