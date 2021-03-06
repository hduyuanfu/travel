package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.FavoriteDao;
import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.RouteImgDao;
import cn.itcast.travel.dao.SellerDao;
import cn.itcast.travel.dao.impl.FavoriteDaoImpl;
import cn.itcast.travel.dao.impl.RouteDaoImpl;
import cn.itcast.travel.dao.impl.RouteImgDaoImpl;
import cn.itcast.travel.dao.impl.SellerDaoImpl;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.RouteImg;
import cn.itcast.travel.domain.Seller;
import cn.itcast.travel.service.RouteService;

import java.util.List;

public class RouteServiceImpl implements RouteService {

    private RouteDao routeDao = new RouteDaoImpl();
    private RouteImgDao routeImgDao = new RouteImgDaoImpl();
    private SellerDao sellerDao = new SellerDaoImpl();
    private FavoriteDao favoriteDao = new FavoriteDaoImpl();

    @Override
    public PageBean<Route> pageQuery(int cid, int currentPage, int pageSize, String rname) {

        // 封装PageBean
        PageBean<Route> pb = new PageBean<>();
        // 设置当前页码
        pb.setCurrentPage(currentPage);
        // 设置每页显示条数
        pb.setPageSize(pageSize);
        // 设置总记录数
        int totalCount = routeDao.findTotalCount(cid, rname);
        pb.setTotalCount(totalCount);
        // 设置总页数
        pb.setTotalPage((int) Math.ceil(1.0 * totalCount / pageSize));
        // 设置当前页显示的数据集合
        int start = (currentPage - 1) * pageSize; // 数据库中记录是从0开始计数的
        List<Route> list = routeDao.findByPage(cid, start, pageSize, rname);
        pb.setList(list);

        return pb;
    }

    @Override
    public Route findOne(String rid) {
        // 1.根据rid去route表中查询route对象
        Route route = routeDao.findOne(Integer.parseInt(rid));

        // 2.根据rid查看route对象的图片集合,并将RouteImg集合放入route对象中
        List<RouteImg> routeImgs = routeImgDao.findByRid(route.getRid());
        route.setRouteImgList(routeImgs);

        // 3.根据route的sid(商家id)查询卖家对象,并将对象放入route对象中
        Seller seller = sellerDao.findById(route.getSid());
        route.setSeller(seller);

        // 4.查询该路线被收藏次数
        int count = favoriteDao.findCountByRid(route.getRid());
        route.setCount(count);

        return route;
    }
}
