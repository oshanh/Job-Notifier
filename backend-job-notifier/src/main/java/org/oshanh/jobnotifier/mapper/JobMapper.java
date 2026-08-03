package org.oshanh.jobnotifier.mapper;

import org.oshanh.jobnotifier.dto.Job;
import org.oshanh.jobnotifier.model.Topjobs;
import java.util.ArrayList;
import java.util.List;

public class JobMapper {
    public static List<Job> topJobsToJob(List<Topjobs> topjobs) {
        List<Job> jobs = new ArrayList<>();
        for (Topjobs topjob : topjobs) {
            Job job = new Job();
            job.setPosition(topjob.getPosition());
            job.setCompanyName(topjob.getCompanyName());
            job.setLocation(topjob.getLocation());
            job.setOpeningDate(topjob.getOpeningDate());
            job.setClosingDate(topjob.getClosingDate());
            job.setSource(topjob.getJobUrl());

            jobs.add(job);
        }
        return jobs;
    }

}
