package com.f.gupiao;

import com.f.gupiao.entity.Stock;
import com.f.gupiao.repository.StockRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fei on 2017/4/5.
 */
@Configuration
@EnableScheduling
public class Jqka {

    protected final Log logge = LogFactory.getLog(this.getClass());

    @Autowired
    StockRepository stockRepository;

    @Scheduled(cron = "0 25,45 9,14,15 ? * 1-5")
    synchronized void sync() {
        int i = 1;
        while (true) {
//        while (i < 2) {
            List<Stock> list = Jqka.getData(i++);
            if (list.size() == 0)
                break;

            for (Stock s : list) {
                stockRepository.removeByCodeAndSDate(s.getCode(), s.getsDate());
                stockRepository.save(s);
                fall(s);
            }
        }
        stockRepository.deleteOld();
    }

    private void fall(Stock stock) {
        Page<Stock> page = stockRepository.findByCodeOrderBySDateDesc(stock.getCode(), new PageRequest(0, 30));
        int i = -1;
        double price = 0;
        for (Stock s : page) {
            if (s.getPrice() > price) {
                price = s.getPrice();
                i++;
            } else
                break;
        }
        stockRepository.updateFall(i, stock.getId());
    }

    public static List<Stock> getData(int i) {
        List<Stock> list = new ArrayList<Stock>();
        try {

            Request request = Request.Get("http://q.10jqka.com.cn/index/index/board/all/field/xj/order/desc/page/" + i + "/ajax/1/");
            String data = request.execute().returnContent().asString();
            Pattern pattern = Pattern.compile("target=\"_blank\">(.*?)</a></td>[\\n].*?target=\"_blank\">(.*?)</a></td>[\\n].*?\">(.*?)</td>");
            Matcher matcher = pattern.matcher(data);

            String sdate = DateFormatUtils.format(new Date(), "yyyyMMdd");
            while (matcher.find()) {
                String code = matcher.group(1);
                String name = matcher.group(2).replace("&#032;", "");
                String price = matcher.group(3);
                list.add(new Stock(name, code, Double.valueOf(price), sdate));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void getHistory(int page) {
        Page<Stock> pageData = stockRepository.findBySDate(DateFormatUtils.format(new Date(), "yyyyMMdd"), new PageRequest(page++, 100,new Sort(Sort.Direction.ASC,"code")));
        if (pageData.getContent().size() == 0) return;
        for (Stock s : pageData.getContent()) {
            getAndSaveHis(s);
        }
        getHistory(page);
    }

    public void getAndSaveHis(Stock s){
        List<Stock> list = getHisData(s);
        stockRepository.save(list);
        stockRepository.deleteRepeat(s.getCode(), s.getCode());
    }

    public List<Stock> getHisData(Stock s) {
        List<Stock> list = new ArrayList<Stock>();
        try {
            Request request = Request.Get("http://stockpage.10jqka.com.cn/" + s.getCode() + "/funds/");
            String data = request.execute().returnContent().asString();
            Pattern pattern = Pattern.compile("class=\"border_l_none\">(.*?)</td>[\\s\\S]*?>(.*)</td>");
            Matcher matcher = pattern.matcher(data);
            while (matcher.find()) {
                String sdate = matcher.group(1);
                String price = matcher.group(2);
                list.add(new Stock(s.getName(), s.getCode(), Double.valueOf(price), sdate));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
