package cn.edu.seu.webPageExtractor.service.impl;

import cn.edu.seu.webPageExtractor.constants.TaskStateEnum;
import cn.edu.seu.webPageExtractor.controller.dto.TaskInfoDto;
import cn.edu.seu.webPageExtractor.core.TaskInfo;
import cn.edu.seu.webPageExtractor.core.page.DetailPage;
import cn.edu.seu.webPageExtractor.service.TaskManageService;
import cn.edu.seu.webPageExtractor.service.repository.TaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TaskManageImpl implements TaskManageService {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskInfoDto createATask(TaskInfo taskInfo) {
        taskInfo = taskRepository.save(taskInfo);
        return  transTaskInfo2Dto(taskInfo);
    }

    @Override
    public boolean updateTaskState(TaskInfo taskInfo) {
        try {
            taskRepository.save(taskInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public TaskInfo queryTaskById(Integer id) {
        try {
            Optional<TaskInfo> taskInfoOptional = taskRepository.findById(id);
            if (taskInfoOptional.isPresent()) {
                return taskInfoOptional.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TaskInfoDto> queryAllTask() {
        try {
            List<TaskInfoDto> taskInfoList = new ArrayList<>();
            Iterable<TaskInfo> taskInfos = taskRepository.findAll();
            taskInfos.forEach(taskInfo -> {
                TaskInfoDto taskInfoDto = transTaskInfo2Dto(taskInfo);
                taskInfoList.add(taskInfoDto);
            });
            return taskInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> taskContextCalculate(List<DetailPage> detailPages) {
        Map<String,Integer> wordCountMap = new HashMap<>();
        List<String> result = new ArrayList<>();
        for (DetailPage detailPage : detailPages){
            List<String> contexts = detailPage.getSpeacilContext();
            contexts.forEach(context ->{
                if (wordCountMap.containsKey(context)){
                    wordCountMap.put(context,wordCountMap.get(context)+1);
                }else {
                    wordCountMap.put(context,1);
                }
            });
        }
        List<Map.Entry<String, Integer>> wordCountMapEntryList = new ArrayList<>(wordCountMap.entrySet());
        wordCountMapEntryList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });

        //获取前10个作为关键词
        for (int i=0;i<10;i++){
            result.add(wordCountMapEntryList.get(i).getKey());
        }
        return result;
    }

    private TaskInfoDto transTaskInfo2Dto(TaskInfo taskInfo){
        TaskInfoDto taskInfoDto = new TaskInfoDto();
        BeanUtils.copyProperties(taskInfo,taskInfoDto);

        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        taskInfoDto.setDateString(dft.format(taskInfoDto.getTime()));

        if (taskInfo.getState().equals(TaskStateEnum.NEWTASK.getState())){
            taskInfoDto.setStateName(TaskStateEnum.NEWTASK.getName());
        }else if (taskInfo.getState().equals(TaskStateEnum.PROCESTASK.getState())){
            taskInfoDto.setStateName(TaskStateEnum.PROCESTASK.getName());
        }else if (taskInfo.getState().equals(TaskStateEnum.FINISHTASK.getState())){
            taskInfoDto.setStateName(TaskStateEnum.FINISHTASK.getName());
        }

        return taskInfoDto;
    }
}
