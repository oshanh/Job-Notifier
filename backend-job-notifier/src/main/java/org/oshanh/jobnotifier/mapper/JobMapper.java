package org.oshanh.jobnotifier.mapper;

import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.model.Topjobs;
import java.util.ArrayList;
import java.util.List;

public class JobMapper {
    public static List<JobDTO> topJobsToJob(List<Topjobs> topjobs) {
        List<JobDTO> jobDTOS = new ArrayList<>();
        for (Topjobs topjob : topjobs) {
            JobDTO jobDTO = new JobDTO();
            jobDTO.setPosition(topjob.getPosition());
            jobDTO.setCompanyName(topjob.getCompanyName());
            jobDTO.setLocation(topjob.getLocation());
            jobDTO.setOpeningDate(topjob.getOpeningDate());
            jobDTO.setClosingDate(topjob.getClosingDate());
            jobDTO.setSource(topjob.getJobUrl());

            jobDTOS.add(jobDTO);
        }
        return jobDTOS;
    }

}
