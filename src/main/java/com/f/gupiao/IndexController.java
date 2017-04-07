package com.f.gupiao;

import com.f.gupiao.entity.Stock;
import com.f.gupiao.repository.StockRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by fei on 2017/4/5.
 */
@Controller
public class IndexController {

    @Autowired
    StockRepository stockRepository;

    @Autowired
    Jqka jqka;

    @RequestMapping("/fall")
    @ResponseBody
    Page<Stock> home() {
        return stockRepository.findBySDateOrderByFallDescCodeAsc(DateFormatUtils.format(new Date(), "yyyyMMdd"), new PageRequest(0, 100));
    }


    @RequestMapping("sync")
    @ResponseBody
    String sync() {
        long k = System.currentTimeMillis();
        jqka.sync();
        long t = System.currentTimeMillis() - k;
        return "用时" + (t / 1000) + "秒";

    }

    @RequestMapping("his")
    @ResponseBody
    String his() {
        long k = System.currentTimeMillis();
        jqka.getHistory(0);
        long t = System.currentTimeMillis() - k;
        return "用时" + (t / 1000) + "秒";
    }

    @RequestMapping("his/{code}")
    @ResponseBody
    String hisByCode(@PathVariable String code) {
        long k = System.currentTimeMillis();
        jqka.getAndSaveHis(new Stock("x", code));
        long t = System.currentTimeMillis() - k;
        return "用时" + (t / 1000) + "秒";
    }
}
