package org.oshanh.jobnotifier.mapper;

import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.dto.KaleniUniJobDTO;
import org.oshanh.jobnotifier.model.Airportjobs;
import org.oshanh.jobnotifier.model.KaleniUniJob;
import org.oshanh.jobnotifier.model.Topjobs;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public static List<JobDTO> toAirportJobsToJob(Set<Airportjobs> airportjobs) {
        if(airportjobs.isEmpty()){
            return new ArrayList<>();
        }
        List<JobDTO> jobDTOS = new ArrayList<>();
        for (Airportjobs airportjob : airportjobs) {
            JobDTO jobDTO = new JobDTO();
            jobDTO.setPosition(airportjob.getPosition());
            jobDTO.setClosingDate(airportjob.getClosingDate());
            jobDTO.setSource(airportjob.getJobUrl());
            jobDTO.setCompanyName("Airport");
            jobDTOS.add(jobDTO);
        }
        return jobDTOS;
    }

    public static List<KaleniUniJobDTO> toKaleniUniJobsDTO(List<KaleniUniJob> kaleniUniJobs) {
        List<KaleniUniJobDTO> kaleniUniJobDTOS = new ArrayList<>();
        for(KaleniUniJob kaleniUniJob : kaleniUniJobs){
            KaleniUniJobDTO job=new KaleniUniJobDTO(
                    kaleniUniJob.getTitle(),
                    kaleniUniJob.getDeadline(),
                    kaleniUniJob.getPortalUrl(),
                    kaleniUniJob.getDepartment(),
                    kaleniUniJob.getEmploymentType(),
                    kaleniUniJob.getSalary(),
                    kaleniUniJob.getDescription(),
                    kaleniUniJob.getAdvertisementUrl(),
                    kaleniUniJob.getApplicationUrl()    
                    
            );
            kaleniUniJobDTOS.add(job);
        }
        return kaleniUniJobDTOS;
    }

}
