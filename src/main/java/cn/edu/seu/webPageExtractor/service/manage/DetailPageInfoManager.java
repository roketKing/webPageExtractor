package cn.edu.seu.webPageExtractor.service.manage;

import cn.edu.seu.webPageExtractor.core.DetailPageInfo;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.service.repository.DetailPageInfoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DetailPageInfoManager {
    @Autowired
    private DetailPageInfoRepository detailPageInfoRepository;

    public DetailPageInfo saveDetailPageInfo(DetailPage detailPage){
        try{
            DetailPageInfo detailPageInfo = new DetailPageInfo();
            BeanUtils.copyProperties(detailPage,detailPageInfo);
           return detailPageInfoRepository.save(detailPageInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<DetailPageInfo> findDetailPageByTaskId(Integer taskId){
        try{
            return detailPageInfoRepository.findDetailPageInfosByTaskId(taskId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<DetailPageInfo> findDetailPageByListId(Integer listId){
        try{
            return detailPageInfoRepository.findDetailPageInfosByListId(listId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
