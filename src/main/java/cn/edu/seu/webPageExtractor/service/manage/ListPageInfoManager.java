package cn.edu.seu.webPageExtractor.service.manage;

import cn.edu.seu.webPageExtractor.core.ListPageInfo;
import cn.edu.seu.webPageExtractor.core.page.ListPage;
import cn.edu.seu.webPageExtractor.service.repository.ListPageInfoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ListPageInfoManager {
    @Autowired
    private ListPageInfoRepository listPageInfoRepository;

    public ListPageInfo saveListPageInfo(ListPage listPage){
        try{
            ListPageInfo listPageInfo = new ListPageInfo();
            BeanUtils.copyProperties(listPage,listPageInfo);
            listPageInfo.setTime(new Date());
            return listPageInfoRepository.save(listPageInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<ListPageInfo> findListPageInfoByTaskId(Integer taskId){
        try{
            return listPageInfoRepository.findListPageInfosByTaskId(taskId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
